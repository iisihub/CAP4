/*
 * @(#)TxnProcess01.java
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
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.citi.utils.Misc;
import com.citi.webatm.rmi.TTxnLog;

import tw.com.citi.elf.TxnMain;
import tw.com.citi.elf.TxnP4501;

/**
 * 餘額查詢
 **/
@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TxnProcess01 extends TxnProcessGeneric {

    // log4J category
    static Logger LOG = LoggerFactory.getLogger("TxnProcess01");

    public TxnProcess01(TxnTerminal txn) {
        super.LOG = LOG;
        txnTml = txn;
    }

    // private String ICCSEQ = null;

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
        TxnP4501 txn4501 = new TxnP4501(txnTml.getTmlID(), MsgSeqNo);
        TxnP4501 txn4501_receive = null;

        txn4501.setAuthTkn1(TAC);
        txn4501.setAuthTkn2(ICCRemark);

        if (OnUsCard) {
            txn4501.setCATCardCC(TxnMain.CATCardCC_Citi);
            txn4501.setChipInputBlk(new StringBuffer().append(PCode).append(Misc.padZero(SourceAccount, 16)).append(TmlCheckCode).append("000000000000000000000000000000000000").toString());
        } else {
            txn4501.setCATCardCC(TxnMain.CATCardCC_Other);
            txn4501.setChipInputBlk(new StringBuffer().append(PCode).append(TmlCheckCode).append(Misc.padZero(SourceAccount, 16)).toString());
        }
        txn4501.setStartSessDT(ClientDT);
        txn4501.setTranSerialNO(ICCTxnNo);
        txn4501.setIssuerBankID(IssuerBankCode);
        txn4501.setAccountNO(SourceAccount);
        txn4501.setAuthIndicator(AuthFlag);
        txn4501.setSessInfoValue(ClientIP);
        txn4501.setTmlType(TmlType);

        insertTxnLog(String.valueOf(txn4501.getMsgFunCode()));

        txnTml.setIssuerBankCode(IssuerBankCode);
        txnTml.setIssuerAccount(SourceAccount);
        txnTml.setPCode(PCode);
        txn4501_receive = txnTml.doTxnP4501(txn4501);

        if (txn4501_receive == null) {
            this.setTimeoutFlag(TTxnLog.TimeoutFlag_Y);
            updateRespTxnLog();
            return false;
        } else {
            this.setHostDT(txn4501_receive.getNetworkDT_R());
            this.setAccountBalance(txn4501_receive.getCurBalance_R());
            this.setAvailableBalance(txn4501_receive.getAvaBalance_R());
            this.setRespCode(txn4501_receive.getAcqRespCode_R());
            this.setActionCode(txn4501_receive.getActionCode_R());
            this.setSTAN(txn4501_receive.getSTAN_R());
            updateRespTxnLog();
            return true;
        }
    }

    /**
     * @param args
     *            Creation date:(2008/8/15 上午 11:34:00)
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
}
