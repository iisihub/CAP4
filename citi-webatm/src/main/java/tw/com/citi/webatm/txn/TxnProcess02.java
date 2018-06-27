/*
 * @(#)TxnProcess02.java
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
import tw.com.citi.elf.TxnP4411;
import tw.com.citi.elf.TxnP4412;

/**
 * 自行轉帳/繳費
 **/
@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TxnProcess02 extends TxnProcessGeneric {

    // log4J category
    static Logger LOG = LoggerFactory.getLogger("TxnProcess02");

    public TxnProcess02(TxnTerminal txn) {
        super.LOG = LOG;
        txnTml = txn;
    }

    public static final String ActionCode_R_HostTimeout = "04";
    public static final String ActionCode_R_Success = "000";
    public static final int TransactionType_Transfer = 0;
    public static final int TransactionType_Payment = 1;
    // private String ICCSEQ = null;
    private int TransactionType = 0;

    /**
     * @param iccseq
     *            The iCCSEQ to set.
     */
    // public void setICCSEQ(String iccseq) {
    // ICCSEQ = iccseq;
    // }

    /**
     * @param transactionType
     *            The transactionType to set.
     */
    public void setTransactionType(int transactionType) {
        TransactionType = transactionType;
    }

    /**
     * this method need to handle txn process step 1.set data step 2.insert txnlog step 3.do txn step 3-1.check if need to run reversal txn step 4.set return data step 5.update txnlog
     */
    public boolean doTxn() {
        String MsgSeqNo = txnTml.getTml_SeqNo(txnTml.getTmlID());
        if (MsgSeqNo == null)
            return false;
        // MsgSeqNo = "1";
        int OrigMsgSeqNo = Integer.parseInt(MsgSeqNo);
        StringBuffer OrigChipInputBlk;
        TxnP4411 txn4411 = new TxnP4411(txnTml.getTmlID(), MsgSeqNo);
        TxnP4411 txn4411_receive = null;
        txn4411.setAuthTkn1(TAC);
        txn4411.setAuthTkn2(ICCRemark);
        txn4411.setStartSessDT(ClientDT);
        txn4411.setTranSerialNO(ICCTxnNo);
        OrigChipInputBlk = new StringBuffer();
        OrigChipInputBlk.append(PCode).append(Misc.padZero(TxnAmount, 12)).append("00").append(Misc.padZero(DestAccount, 16))
                // .append(txnTml.getTmlID())
                .append(TmlCheckCode).append(ClientDT.substring(8)).append(Misc.padZero(SourceAccount, 16));
        txn4411.setChipInputBlk(OrigChipInputBlk.toString());
        txn4411.setTransAmount(TxnAmount);
        if (TransactionType == TransactionType_Transfer)
            txn4411.setTTC(TxnMain.TTC_Transfer);
        else
            txn4411.setTTC(TxnMain.TTC_Payment);

        txn4411.setSourceAccountNO(SourceAccount);
        txn4411.setDestAccountNO(DestAccount);
        txn4411.setSessInfoValue(ClientIP);
        txn4411.setTmlType(TmlType);

        insertTxnLog(String.valueOf(txn4411.getMsgFunCode()));

        txnTml.setIssuerBankCode(IssuerBankCode);
        txnTml.setIssuerAccount(SourceAccount);
        txnTml.setPCode(PCode);

        // String txnDateTime = Misc.genDate(Misc.DT_DATETIME);
        // TxnDate = txnDateTime.substring(0,8);
        // ClientDT = txnDateTime; // not need to reset
        // SendReqDT = txnDateTime;

        txn4411_receive = txnTml.doTxnP4411(txn4411);

        if (txn4411_receive == null) {
            this.setTimeoutFlag(TTxnLog.TimeoutFlag_Y);
            updateRespTxnLog();
        } else {
            this.setHostDT(txn4411_receive.getNetworkDT_R());
            this.setAccountBalance(txn4411_receive.getCurBalance_R());
            this.setAvailableBalance(txn4411_receive.getAvaBalance_R());
            this.setRespCode(txn4411_receive.getAcqRespCode_R());
            this.setActionCode(txn4411_receive.getActionCode_R());
            this.setFeeCharge(txn4411_receive.getHandlingCharge_R());
            this.setBizDayFlag(txn4411_receive.getNextBusinessDay_R());
            this.setTxnConfNo(txn4411_receive.getTransferNumber_R());
            updateRespTxnLog();
            if (txn4411_receive.getActionCode_R().equals(ActionCode_R_Success)) {
                insertTxnUsrLog();
            }
        }

        // 判斷是否要做4412 Reversal交易
        if (// true ||
        (txn4411_receive == null) || (txn4411_receive.getActionCode_R().equals(ActionCode_R_HostTimeout))) {
            this.setReversalReason("14");
            this.setReversalStatus(TTxnLog.ReversalStatus_W);
            // 發送Reversal 直到ActionCode=000 次數最多5次
            for (int i = 1; i <= 5; i++) {
                this.setReversalTimes(String.valueOf(i));
                updateRespTxnLog();

                MsgSeqNo = txnTml.getTml_SeqNo(txnTml.getTmlID());
                TxnP4412 txn4412 = new TxnP4412(txnTml.getTmlID(), MsgSeqNo);
                TxnP4412 txn4412_receive = null;

                txn4412.setAuthTkn1(TAC);
                txn4412.setAuthTkn2(ICCRemark);
                txn4412.setStartSessDT(ClientDT);
                txn4412.setTranSerialNO(ICCTxnNo);
                // txn4412.setChipInputBlk(OrigChipInputBlk.toString());
                txn4412.setChipInputBlk(

                        new StringBuffer().append(PCode).append(Misc.padZero(TxnAmount, 12)).append("00").append(Misc.padZero(DestAccount, 16))
                                // .append(txnTml.getTmlID())
                                .append(TmlCheckCode).append(ClientDT.substring(8)).append(Misc.padZero(SourceAccount, 16)).toString());
                // txn4412.setOrigMsgSeqNo(txn4411.getMsgSeqNo());
                txn4412.setOrigMsgSeqNo(Misc.padZero(Long.toHexString(Long.parseLong(txn4411.getMsgSeqNo())), 8));

                txn4412.setRevReasonCode("14");
                txn4412.setTransAmount(TxnAmount);
                if (TransactionType == TransactionType_Transfer)
                    txn4412.setTTC(TxnMain.TTC_Transfer);
                else
                    txn4412.setTTC(TxnMain.TTC_Payment);

                txn4412.setSourceAccountNO(SourceAccount);
                txn4412.setDestAccountNO(DestAccount);
                // txn4412.setTransferNumber((txn4411_receive==null) ? "" : txn4411_receive.getTransferNumber_R());
                txn4412.setSessInfoValue(ClientIP);
                txn4412.setTmlType(TmlType);

                insertReversalLog(txn4411.getMsgSeqNo(), i); // 新增沖正交易記錄檔

                txn4412_receive = txnTml.doTxnP4412(txn4412);

                txnTml.setMsgSeqNo(String.valueOf(OrigMsgSeqNo)); // update 原4411交易資料，所以要setMsgSeqNo為OrigMsgSeqNo
                if (txn4412_receive != null && txn4412_receive.getActionCode_R().equals(ActionCode_R_Success)) {
                    this.setReversalStatus(TTxnLog.ReversalStatus_S);

                    this.setRespCode(txn4412_receive.getAcqRespCode_R());
                    this.setActionCode(txn4412_receive.getActionCode_R());

                    updateRespReversalLog(txn4411.getMsgSeqNo(), i); // 更新沖正交易記錄檔內容

                    if (txn4411_receive != null) {
                        this.setRespCode(txn4411_receive.getAcqRespCode_R());
                        this.setActionCode(txn4411_receive.getActionCode_R());
                    } else {
                        this.setRespCode(null);
                        this.setActionCode(null);
                    }

                    updateRespTxnLog();
                    insertTxnUsrLog();
                    break;
                } else if (txn4412_receive != null) {
                    this.setRespCode(txn4412_receive.getAcqRespCode_R());
                    this.setActionCode(txn4412_receive.getActionCode_R());
                    updateRespReversalLog(txn4411.getMsgSeqNo(), i); // 更新沖正交易記錄檔內容
                }
                this.setReversalStatus(TTxnLog.ReversalStatus_F);
                updateRespTxnLog();
            }
            return false;
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
