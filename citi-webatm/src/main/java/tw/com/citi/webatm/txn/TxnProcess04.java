/*
 * @(#)TxnProcess04.java
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

import tw.com.citi.elf.TxnP4113;
import tw.com.citi.elf.TxnP4118;

/**
 * 網銀PIC Change
 **/
public class TxnProcess04 extends TxnProcessGeneric {

    // log4J category
    static Logger LOG = LoggerFactory.getLogger("TxnProcess04");

    public TxnProcess04(TxnTerminal txn) {
        super.LOG = LOG;
        txnTml = txn;
    }

    public static final String ActionCode_R_HostTimeout = "04";
    public static final String ActionCode_R_Success = "000";
    private String NewPinBlk = null;
    private String OldPinBlk = null;

    /**
     * @param newPinBlk
     *            The NewPinBlk to set.
     */
    public void setNewPinBlk(String newPinBlk) {
        NewPinBlk = newPinBlk;
    }

    /**
     * @param oldPinBlk
     *            The NewPinBlk to set.
     */
    public void setOldPinBlk(String oldPinBlk) {
        OldPinBlk = oldPinBlk;
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
        TxnP4113 txn4113 = new TxnP4113(txnTml.getTmlID(), MsgSeqNo);
        TxnP4113 txn4113_receive = null;
        txn4113.setAuthTkn1(new String(Misc.bin2Hex(IssuerAccount.getBytes())));
        txn4113.setAuthTkn2(new String(Misc.bin2Hex(TxnTerminal.getKeyGeneration().getBytes())));
        // txn4113.setAuthTkn3(new String(Misc.bin2Hex(OldPinBlk.getBytes())));
        txn4113.setStartSessDT(ClientDT);
        txn4113.setNewPinBlk(new String(Misc.bin2Hex(NewPinBlk.getBytes())));

        insertTxnLog(String.valueOf(txn4113.getMsgFunCode()));

        txn4113_receive = txnTml.doTxnP4113(txn4113);

        if (txn4113_receive == null) {
            this.setTimeoutFlag(TTxnLog.TimeoutFlag_Y);
            updateRespTxnLog();
        } else {
            this.setRespCode(txn4113_receive.getAcqRespCode_R());
            this.setActionCode(txn4113_receive.getActionCode_R());
            updateRespTxnLog();
            if (txn4113_receive.getActionCode_R().equals(ActionCode_R_Success)) {
                insertTxnUsrLog();
            }
        }

        // 判斷是否要做4118 Reversal交易
        if ((txn4113_receive == null) || (txn4113_receive.getActionCode_R().equals(ActionCode_R_HostTimeout))) {
            this.setReversalReason("14");
            this.setReversalStatus(TTxnLog.ReversalStatus_W);
            // 發送Reversal 直到ActionCode=000 次數最多5次
            for (int i = 1; i <= 5; i++) {
                this.setReversalTimes(String.valueOf(i));
                updateRespTxnLog();

                MsgSeqNo = txnTml.getTml_SeqNo(txnTml.getTmlID());
                TxnP4118 txn4118 = new TxnP4118(txnTml.getTmlID(), MsgSeqNo);
                TxnP4118 txn4118_receive = null;

                txn4118.setAuthTkn1(new String(Misc.bin2Hex(IssuerAccount.getBytes())));
                txn4118.setAuthTkn2(new String(Misc.bin2Hex(TxnTerminal.getKeyGeneration().getBytes())));
                // txn4118.setAuthTkn3(new String(Misc.bin2Hex(OldPinBlk.getBytes())));
                txn4118.setStartSessDT(ClientDT);
                txn4118.setNewPinBlk(new String(Misc.bin2Hex(NewPinBlk.getBytes())));

                txn4118.setOrigMsgSeqNo(Misc.padZero(Long.toHexString(Long.parseLong(txn4113.getMsgSeqNo())), 8));

                txn4118.setRevReasonCode("14");

                insertReversalLog(txn4118.getMsgSeqNo(), i); // 新增沖正交易記錄檔

                txn4118_receive = txnTml.doTxnP4118(txn4118);

                txnTml.setMsgSeqNo(String.valueOf(OrigMsgSeqNo)); // update 原4113交易資料，所以要setMsgSeqNo為OrigMsgSeqNo
                if (txn4118_receive != null && txn4118_receive.getActionCode_R().equals(ActionCode_R_Success)) {
                    this.setReversalStatus(TTxnLog.ReversalStatus_S);

                    this.setRespCode(txn4118_receive.getAcqRespCode_R());
                    this.setActionCode(txn4118_receive.getActionCode_R());

                    updateRespReversalLog(txn4113.getMsgSeqNo(), i); // 更新沖正交易記錄檔內容

                    if (txn4113_receive != null) {
                        this.setRespCode(txn4113_receive.getAcqRespCode_R());
                        this.setActionCode(txn4113_receive.getActionCode_R());
                    } else {
                        this.setRespCode(null);
                        this.setActionCode(null);
                    }

                    updateRespTxnLog();
                    insertTxnUsrLog();
                    break;
                } else if (txn4118_receive != null) {
                    this.setRespCode(txn4118_receive.getAcqRespCode_R());
                    this.setActionCode(txn4118_receive.getActionCode_R());
                    updateRespReversalLog(txn4118.getMsgSeqNo(), i); // 更新沖正交易記錄檔內容
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
