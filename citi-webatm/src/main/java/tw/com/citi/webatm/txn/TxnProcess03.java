/*
 * @(#)TxnProcess03.java
 *
 * Copyright (c) 2008 Citi Group Incorporated. All rights reserved.
 *
 * Modify History:
 *  v1.00, 2008/07/07, Kevin Chung
 *   1) First release
 *
 */
package tw.com.citi.webatm.txn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citi.utils.Misc;
import com.citi.webatm.rmi.TTxnLog;

import tw.com.citi.elf.TxnMain;
import tw.com.citi.elf.TxnP4404;

/**
 * 跨行轉帳/繳費/移轉性計畫
 **/
public class TxnProcess03 extends TxnProcessGeneric {

    // log4J category
    static Logger LOG = LoggerFactory.getLogger("TxnProcess03");

    public TxnProcess03(TxnTerminal txn) {
        super.LOG = LOG;
        txnTml = txn;
    }

    public static final String ActionCode_R_Success = "000";
    public static final int TransactionType_Transfer = 0;
    public static final int TransactionType_Payment = 1;
    // private String ICCSEQ = null;
    private int TransactionType = 0;

    /**
     * @param transactionType
     *            The transactionType to set.
     */
    public void setTransactionType(int transactionType) {
        TransactionType = transactionType;
    }

    /**
     * @param iccseq
     *            The iCCSEQ to set.
     */
    // public void setICCSEQ(String iccseq) {
    // ICCSEQ = iccseq;
    // }

    /**
     * this method need to handle txn process step 1.set data step 2.insert txnlog step 3.do txn step 4.set return data step 5.update txnlog
     */
    public boolean doTxn() {
        String MsgSeqNo = txnTml.getTml_SeqNo(txnTml.getTmlID());
        if (MsgSeqNo == null)
            return false;
        TxnP4404 txn4404 = new TxnP4404(txnTml.getTmlID(), MsgSeqNo);
        TxnP4404 txn4404_receive = null;

        txn4404.setAuthTkn1(TAC);
        txn4404.setAuthTkn2(ICCRemark);
        if (OnUsCard)
            txn4404.setCATCardCC(TxnMain.CATCardCC_Citi);
        else
            txn4404.setCATCardCC(TxnMain.CATCardCC_Other);
        txn4404.setStartSessDT(ClientDT);
        txn4404.setTranSerialNO(ICCTxnNo);
        if (PCode.startsWith("252")) {
            txn4404.setChipInputBlk(new StringBuffer().append(PCode).append(Misc.padZero(TxnAmount, 12)).append("00").append(txnTml.getTmlID()).append(TmlCheckCode).append(ClientDT)
                    .append(Misc.padZero(DestAccount, 16)).append(Misc.padZero(SourceAccount, 16)).toString());
        } else if (PCode.startsWith("256")) {
            txn4404.setChipInputBlk(new StringBuffer().append(PCode).append(Misc.padZero(TxnAmount, 12)).append("00").append(txnTml.getTmlID()).append(TmlCheckCode).append(ClientDT)
                    .append(Misc.padZero(SourceAccount, 16)).toString());
        }

        if (TransactionType == TransactionType_Transfer) {
            txn4404.setTTC(TxnMain.TTC_Transfer);
            txn4404.setTQC(TxnMain.TQC_Transfer);
        } else {
            txn4404.setTTC(TxnMain.TTC_Payment);
            txn4404.setTQC(TxnMain.TQC_Payment);
        }

        txn4404.setIssuerBankID(IssuerBankCode);
        txn4404.setTransAmount(TxnAmount);
        txn4404.setSourceBankCode(IssuerBankCode);
        txn4404.setSourceAccountNO(SourceAccount);
        txn4404.setBankRouteCode(DestBankCode);
        txn4404.setDestAccountNO(DestAccount);
        txn4404.setSessInfoValue(ClientIP);
        txn4404.setTmlType(TmlType);

        insertTxnLog(String.valueOf(txn4404.getMsgFunCode()));

        txnTml.setIssuerBankCode(IssuerBankCode);
        txnTml.setIssuerAccount(SourceAccount);
        txnTml.setPCode(PCode);
        txn4404_receive = txnTml.doTxnP4404(txn4404);

        if (txn4404_receive == null) {
            this.setTimeoutFlag(TTxnLog.TimeoutFlag_Y);
            updateRespTxnLog();
            return false;
        } else {
            this.setHostDT(txn4404_receive.getNetworkDT_R());
            this.setAccountBalance(txn4404_receive.getCurBalance_R());
            this.setAvailableBalance(txn4404_receive.getAvaBalance_R());
            this.setRespCode(txn4404_receive.getAcqRespCode_R());
            this.setActionCode(txn4404_receive.getActionCode_R());
            this.setBizDayFlag(txn4404_receive.getNextBusinessDay_R());
            this.setFeeCharge(txn4404_receive.getHandlingCharge_R());
            this.setHostDT(txn4404_receive.getNetworkDT_R());
            this.setTxnConfNo(txn4404_receive.getTransferNumber_R());
            this.setSTAN(txn4404_receive.getSTAN_R());
            updateRespTxnLog();
            if (txn4404_receive.getActionCode_R().equals(ActionCode_R_Success)) {
                insertTxnUsrLog();
            }
        }
        return true;
    }

    /**
     * @param args
     *            Creation date:(2008/8/15 上午 11:34:00)
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
}
