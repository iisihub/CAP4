/*
 * @(#)TxnProcessGeneric.java
 *
 * Copyright (c) 2008 Citi Group Incorporated. All rights reserved.
 *
 * Modify History:
 *  v1.00, 2008/07/07, Kevin Chung
 *   1) First release
 *
 */
package tw.com.citi.webatm.txn;

import static com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl.WSS_3DES_KEY;
import static com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl.WSS_3DES_TYPE;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import org.slf4j.Logger;

import com.citi.utils.Misc;
import com.citi.webatm.rmi.TReversalLogMgr;
import com.citi.webatm.rmi.TTxnLogMgr;
import com.citi.webatm.rmi.TTxnUsrLogMgr;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl;
import com.iisigroup.colabase.webatm.common.CCConstants;
import com.iisigroup.cap.utils.CapAppContext;

import tw.com.citi.ws.hsm.WSSecurity;

/**
 * Define some value for setting into ELF class provide some method(insertTxnLog..) for log to DB
 */
public abstract class TxnProcessGeneric {

    protected static Logger LOG = null;
    protected TxnTerminal txnTml = null;
    private final static String SP_Cmd_01 = "SP_TxnLog_ins";
    private final static String SP_Cmd_02 = "SP_TxnLog_upd_response";
    private final static String SP_Cmd_03 = "SP_TxnUsrLog_ins";
    private final static String SP_Cmd_04 = "SP_ReversalLog_ins";
    private final static String SP_Cmd_05 = "SP_ReversalLog_upd_response";

    private final static String BlankChar = "";
    protected boolean OnUsCard = true; // 是否為自行卡
    protected String TxnDate = null;
    protected String IssuerBankCode = null;
    protected String IssuerAccount = null;
    protected String PCode = null;
    protected String SourceAccount = null;
    protected String DestBankCode = null;
    protected String DestAccount = null;
    protected String TxnAmount = null;
    protected String Memo = null;
    protected String ICCTxnNo = null;
    protected String ICCRemark = null;
    protected String TmlCheckCode = null;
    protected String TmlType = null;
    protected String TAC = null;
    protected String MAC = null;
    protected String SessionID = null;
    protected String ClientIP = null;
    protected String ClientDT = null;
    private String SendReqDT = null;
    protected String RcvRespDT = null;
    protected String HostDT = null;
    protected String BizDayFlag = null;
    protected String AccountBalance = null;
    protected String AvailableBalance = null;
    protected String FeeCharge = null;
    protected String RespCode = null;
    protected String ActionCode = null;
    protected String STAN = null;
    protected String TxnConfNo = null;
    protected String AuthFlag = null;
    protected String TimeoutFlag = null;
    protected String ReversalReason = null;
    protected String ReversalStatus = null;
    protected String ReversalTimes = null;

    protected String OrigTrnsStatus = null; // 第一次交易回覆狀態

    private static APSystemService APSystem = CapAppContext.getApplicationContext().getBean(APSystemServiceImpl.class);

    /**
     * need sub-class to implement the detail process
     **/
    public abstract boolean doTxn();

    /**
     * @param onUsCard
     *            The onUsCard to set.
     */
    public void setOnUsCard(boolean onUsCard) {
        OnUsCard = onUsCard;
    }

    /**
     * @param accountBalance
     *            The accountBalance to set.
     */
    public void setAccountBalance(String accountBalance) {
        AccountBalance = accountBalance;
    }

    /**
     * @param actionCode
     *            The actionCode to set.
     */
    public void setActionCode(String actionCode) {
        ActionCode = actionCode;
    }

    /**
     * @param authFlag
     *            The authFlag to set.
     */
    public void setAuthFlag(String authFlag) {
        AuthFlag = authFlag;
    }

    /**
     * @param availableBalance
     *            The availableBalance to set.
     */
    public void setAvailableBalance(String availableBalance) {
        AvailableBalance = availableBalance;
    }

    /**
     * @param bizDayFlag
     *            The bizDayFlag to set.
     */
    public void setBizDayFlag(String bizDayFlag) {
        BizDayFlag = bizDayFlag;
    }

    /**
     * @param clientDT
     *            The clientDT to set.
     */
    public void setClientDT(String clientDT) {
        ClientDT = clientDT;
    }

    /**
     * @param clientIP
     *            The clientIP to set.
     */
    public void setClientIP(String clientIP) {
        ClientIP = clientIP;
    }

    /**
     * @param destAccount
     *            The destAccount to set.
     */
    public void setDestAccount(String destAccount) {
        DestAccount = destAccount;
    }

    /**
     * @param destBankCode
     *            The destBankCode to set.
     */
    public void setDestBankCode(String destBankCode) {
        DestBankCode = destBankCode;
    }

    /**
     * @param feeCharge
     *            The feeCharge to set.
     */
    public void setFeeCharge(String feeCharge) {
        FeeCharge = feeCharge;
    }

    /**
     * @param hostDT
     *            The hostDT to set.
     */
    public void setHostDT(String hostDT) {
        if (hostDT == null || hostDT.equals(""))
            return;

        // BAFES時間為格林威治時間，所以要+8小時
        Calendar dt = Calendar.getInstance();

        dt.set(Calendar.YEAR, Integer.parseInt(hostDT.substring(0, 4)));
        dt.set(Calendar.MONTH, Integer.parseInt(hostDT.substring(4, 6)) - 1);
        dt.set(Calendar.DATE, Integer.parseInt(hostDT.substring(6, 8)));
        dt.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hostDT.substring(8, 10)));
        dt.set(Calendar.MINUTE, Integer.parseInt(hostDT.substring(10, 12)));
        dt.set(Calendar.SECOND, Integer.parseInt(hostDT.substring(12)));
        dt.add(Calendar.HOUR_OF_DAY, 8);

        HostDT = (new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())).format(dt.getTime());

        // HostDT = hostDT;
    }

    /**
     * @param remark
     *            The iCCRemark to set.
     */
    public void setICCRemark(String remark) {
        ICCRemark = remark;
    }

    /**
     * @param txnNo
     *            The iCCTxnNo to set.
     */
    public void setICCTxnNo(String txnNo) {
        ICCTxnNo = txnNo;
    }

    /**
     * @param issuerAccount
     *            The issuerAccount to set.
     */
    public void setIssuerAccount(String issuerAccount) {
        IssuerAccount = issuerAccount;
    }

    /**
     * @param issuerBankCode
     *            The issuerBankCode to set.
     */
    public void setIssuerBankCode(String issuerBankCode) {
        IssuerBankCode = issuerBankCode;
    }

    /**
     * @param mac
     *            The mAC to set.
     */
    public void setMAC(String mac) {
        MAC = mac;
    }

    /**
     * @param mac
     *            The mEmo to set.
     */
    public void setMemo(String memo) {
        Memo = memo;
    }

    /**
     * @param code
     *            The pCode to set.
     */
    public void setPCode(String code) {
        PCode = code;
    }

    /**
     * @param rcvRespDT
     *            The rcvRespDT to set.
     */
    public void setRcvRespDT(String rcvRespDT) {
        RcvRespDT = rcvRespDT;
    }

    /**
     * @param respCode
     *            The respCode to set.
     */
    public void setRespCode(String respCode) {
        RespCode = respCode;
    }

    /**
     * @param reversalReason
     *            The reversalReason to set.
     */
    public void setReversalReason(String reversalReason) {
        ReversalReason = reversalReason;
    }

    /**
     * @param reversalStatus
     *            The reversalStatus to set.
     */
    public void setReversalStatus(String reversalStatus) {
        ReversalStatus = reversalStatus;
    }

    /**
     * @param reversalTimes
     *            The reversalTimes to set.
     */
    public void setReversalTimes(String reversalTimes) {
        ReversalTimes = reversalTimes;
    }

    /**
     * @param sendReqDT
     *            The sendReqDT to set.
     */
    public void setSendReqDT(String sendReqDT) {
        SendReqDT = sendReqDT;
    }

    /**
     * @param sessionID
     *            The sessionID to set.
     */
    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    /**
     * @param sourceAccount
     *            The sourceAccount to set.
     */
    public void setSourceAccount(String sourceAccount) {
        SourceAccount = sourceAccount;
    }

    /**
     * @param stan
     *            The sTAN to set.
     */
    public void setSTAN(String stan) {
        STAN = stan;
    }

    /**
     * @param tac
     *            The tAC to set.
     */
    public void setTAC(String tac) {
        TAC = tac;
    }

    /**
     * @param timeoutFlag
     *            The timeoutFlag to set.
     */
    public void setTimeoutFlag(String timeoutFlag) {
        TimeoutFlag = timeoutFlag;
    }

    /**
     * @param tmlCheckCode
     *            The tmlCheckCode to set.
     */
    public void setTmlCheckCode(String tmlCheckCode) {
        TmlCheckCode = tmlCheckCode;
    }

    /**
     * @param tmlType
     *            The tmlType to set.
     */
    public void setTmlType(String tmlType) {
        TmlType = tmlType;
    }

    /**
     * @param txnAmount
     *            The txnAmount to set.
     */
    public void setTxnAmount(String txnAmount) {
        TxnAmount = txnAmount;
    }

    /**
     * @param txnConfNo
     *            The txnConfNo to set.
     */
    public void setTxnConfNo(String txnConfNo) {
        TxnConfNo = txnConfNo;
    }

    /**
     * @return Returns the accountBalance.
     */
    public String getAccountBalance() {
        return AccountBalance;
    }

    /**
     * @return Returns the actionCode.
     */
    public String getActionCode() {
        return ActionCode;
    }

    /**
     * @return Returns the availableBalance.
     */
    public String getAvailableBalance() {
        return AvailableBalance;
    }

    /**
     * @return Returns the bizDayFlag.
     */
    public String getBizDayFlag() {
        return BizDayFlag;
    }

    /**
     * @return Returns the feeCharge.
     */
    public String getFeeCharge() {
        return FeeCharge;
    }

    /**
     * @return Returns the hostDT.
     */
    public String getHostDT() {
        return HostDT;
    }

    /**
     * @return Returns the respCode.
     */
    public String getRespCode() {
        return RespCode;
    }

    /**
     * @return Returns the sTAN.
     */
    public String getSTAN() {
        return STAN;
    }

    /**
     * @return Returns the timeoutFlag.
     */
    public String getTimeoutFlag() {
        return TimeoutFlag;
    }

    /**
     * @return Returns the txnConfNo.
     */
    public String getTxnConfNo() {
        return TxnConfNo;
    }

    /**
     * @return Returns the trnStatus.
     */
    public String getTrnsStatus() {
        // 為什麼要用OrigTrnsStatus呢??
        // 因為如果用原本的txnTml.getTrnsStatus()的話
        // 可能會取到後續沖正交易的狀態
        if (OrigTrnsStatus != null)
            return OrigTrnsStatus;
        return txnTml.getTrnsStatus();
    }

    /**
     * insert txn data to TxnLog
     **/
    protected final boolean insertTxnLog(String MsgFunCode) {
        TTxnLogMgr TxnLogMgr;
        ArrayList alField = new ArrayList();

        LOG.info("method: - Start doInsertTxnLog()");
        try {
            String txnDateTime = Misc.genDate(Misc.DT_DATETIME);
            this.TxnDate = txnDateTime.substring(0, 8);
            // ClientDT = txnDateTime;
            // this.TxnDate = ClientDT.substring(0,8);
            this.SendReqDT = txnDateTime; // set this for follow-up updateTxnLog

            TxnLogMgr = (TTxnLogMgr) Naming.lookup("rmi://" + APSystem.getRmiSrvName() + ":" + APSystem.getRmiSrvPort() + "/TTxnLog");
            alField.add(TxnDate);
            alField.add(txnTml.getTmlID());
            alField.add(txnTml.getMsgSeqNo());
            alField.add((IssuerBankCode == null) ? BlankChar : IssuerBankCode);
            alField.add((IssuerAccount == null) ? BlankChar : IssuerAccount);
            alField.add(MsgFunCode);
            alField.add((PCode == null) ? BlankChar : PCode);
            alField.add((SourceAccount == null) ? BlankChar : SourceAccount);
            alField.add((DestBankCode == null) ? BlankChar : DestBankCode);
            alField.add((DestAccount == null) ? BlankChar : DestAccount);
            alField.add((TxnAmount == null) ? "0" : TxnAmount);
            // alField.add((Memo == null) ? BlankChar : Memo);
            alField.add((ICCTxnNo == null) ? "0" : ICCTxnNo);
            alField.add((ICCRemark == null) ? BlankChar : ICCRemark);
            alField.add((TmlCheckCode == null) ? BlankChar : TmlCheckCode);
            alField.add((TmlType == null) ? BlankChar : TmlType);
            alField.add((TAC == null) ? BlankChar : TAC);
            alField.add((MAC == null) ? BlankChar : MAC);
            alField.add((SessionID == null) ? BlankChar : SessionID);
            alField.add((ClientIP == null) ? BlankChar : ClientIP);
            alField.add((ClientDT == null) ? BlankChar : ClientDT);
            alField.add((SendReqDT == null) ? BlankChar : SendReqDT);
            alField.add((RcvRespDT == null) ? BlankChar : RcvRespDT);
            alField.add((HostDT == null) ? BlankChar : HostDT);
            alField.add((BizDayFlag == null) ? BlankChar : BizDayFlag);
            alField.add((AccountBalance == null) ? "0" : AccountBalance);
            alField.add((AvailableBalance == null) ? "0" : AvailableBalance);
            alField.add((FeeCharge == null) ? "0" : FeeCharge);
            alField.add((RespCode == null) ? BlankChar : RespCode);
            alField.add((ActionCode == null) ? BlankChar : ActionCode);
            alField.add((STAN == null) ? "0" : STAN);
            alField.add((TxnConfNo == null) ? BlankChar : TxnConfNo);
            alField.add((AuthFlag == null) ? BlankChar : AuthFlag);
            alField.add((TimeoutFlag == null) ? BlankChar : TimeoutFlag);
            alField.add((ReversalReason == null) ? BlankChar : ReversalReason);
            alField.add((ReversalStatus == null) ? BlankChar : ReversalStatus);
            alField.add((ReversalTimes == null) ? BlankChar : ReversalTimes);

            int iRtn = TxnLogMgr.insert_SP_TxnLog(SP_Cmd_01, alField);
            if (iRtn == 0) {
                LOG.warn("doInsertTxnLog: SP_TxnLog_ins error happened!");
                return false;
            }
            return true;
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("doInsertTxnLog: 存取遠端 RMI 錯誤！");
            return false;
        } catch (Exception ex) {
            LOG.error("doInsertTxnLog: Other Exception:" + ex.getMessage());
            return false;
        } // try 1
    }

    /**
     * insert reversal txn data to ReversalLog
     **/
    protected final boolean insertReversalLog(String MsgSeqNo, int ReversalSeq) {
        TReversalLogMgr ReversalLogMgr;
        ArrayList alField = new ArrayList();

        LOG.info("method: - Start doInsertReversalLog()");
        try {
            String txnDateTime = Misc.genDate(Misc.DT_DATETIME);
            // this.TxnDate = txnDateTime.substring(0,8);
            // ClientDT = txnDateTime;
            // this.TxnDate = ClientDT.substring(0,8);
            // this.SendReqDT = txnDateTime;

            ReversalLogMgr = (TReversalLogMgr) Naming.lookup("rmi://" + APSystem.getRmiSrvName() + ":" + APSystem.getRmiSrvPort() + "/TReversalLog");
            alField.add(TxnDate);
            alField.add(txnTml.getTmlID());
            alField.add(MsgSeqNo);
            alField.add(String.valueOf(ReversalSeq));
            alField.add(txnTml.getMsgSeqNo());

            alField.add((SendReqDT == null) ? BlankChar : txnDateTime);
            alField.add(BlankChar);
            alField.add(BlankChar);
            alField.add(BlankChar);

            int iRtn = ReversalLogMgr.insert_SP_ReversalLog(SP_Cmd_04, alField);
            if (iRtn == 0) {
                LOG.warn("doInsertReversalLog: SP_ReversalLog_ins error happened!");
                return false;
            }
            return true;
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("doInsertReversalLog: 存取遠端 RMI 錯誤！");
            return false;
        } catch (Exception ex) {
            LOG.error("doInsertReversalLog: Other Exception:" + ex.getMessage());
            return false;
        } // try 1
    }

    /**
     * insert little txn data to TxnUsrLog
     **/
    protected final boolean insertTxnUsrLog() {
        TTxnUsrLogMgr TxnUsrLogMgr;
        ArrayList alField = new ArrayList();

        LOG.info("method: - Start doInsertTxnUsrLog()");
        try {
            TxnUsrLogMgr = (TTxnUsrLogMgr) Naming.lookup("rmi://" + APSystem.getRmiSrvName() + ":" + APSystem.getRmiSrvPort() + "/TTxnUsrLog");
            alField.add(this.TxnDate);
            alField.add(txnTml.getTmlID());
            alField.add(txnTml.getMsgSeqNo());
            alField.add(ClientDT);
            alField.add((IssuerBankCode == null) ? BlankChar : IssuerBankCode);
            alField.add((SourceAccount == null) ? BlankChar : SourceAccount);
            alField.add((DestBankCode == null) ? BlankChar : DestBankCode);
            alField.add((DestAccount == null) ? BlankChar : DestAccount);
            alField.add((TxnAmount == null) ? "0" : TxnAmount);
            alField.add((FeeCharge == null) ? "0" : FeeCharge);
            alField.add((Memo == null) ? BlankChar : Memo);

            int iRtn = TxnUsrLogMgr.insert_SP_TxnUsrLog(SP_Cmd_03, alField);
            if (iRtn == 0) {
                LOG.warn("doInsertTxnUsrLog: SP_TxnUsrLog_ins error happened!");
                return false;
            }
            return true;
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("doInsertTxnUsrLog: 存取遠端 RMI 錯誤！");
            return false;
        } catch (Exception ex) {
            LOG.error("doInsertTxnUsrLog: Other Exception:" + ex.getMessage());
            return false;
        } // try 1
    }

    /**
     * update txn data to TxnLog when receive RespTxn data
     **/
    protected final boolean updateRespTxnLog() {
        TTxnLogMgr TxnLogMgr;
        HashMap hm = null;
        LOG.info("method: - Start updateRespTxnLog(" + TxnDate + "-" + txnTml.getTmlID() + "-" + txnTml.getMsgSeqNo() + ")");
        String txnDateTime = Misc.genDate(Misc.DT_DATETIME);
        hm = new HashMap();
        hm.put("_TxnDate", TxnDate);
        hm.put("_TmlID", txnTml.getTmlID());
        hm.put("_MsgSeqNo", txnTml.getMsgSeqNo());
        hm.put("_SendReqDT", SendReqDT);
        hm.put("RcvRespDT", txnDateTime);
        hm.put("HostDT", (HostDT == null) ? BlankChar : HostDT);
        hm.put("BizDayFlag", (BizDayFlag == null) ? BlankChar : BizDayFlag);
        hm.put("AccountBalance", (AccountBalance == null) ? "0" : AccountBalance);
        hm.put("AvailableBalance", (AvailableBalance == null) ? "0" : AvailableBalance);
        hm.put("FeeCharge", (FeeCharge == null) ? "0" : FeeCharge);
        hm.put("RespCode", (RespCode == null) ? BlankChar : RespCode);
        hm.put("ActionCode", (ActionCode == null) ? BlankChar : ActionCode);
        hm.put("STAN", (STAN == null) ? "0" : STAN);
        hm.put("TxnConfNo", (TxnConfNo == null) ? BlankChar : TxnConfNo);
        // hm.put("AuthFlag", (AuthFlag == null) ? BlankChar : AuthFlag);
        hm.put("TimeoutFlag", (TimeoutFlag == null) ? BlankChar : TimeoutFlag);
        if (OrigTrnsStatus == null) {
            OrigTrnsStatus = txnTml.getTrnsStatus();
        }
        hm.put("TrnsStatus", (OrigTrnsStatus == null) ? txnTml.getTrnsStatus() : OrigTrnsStatus);
        hm.put("ReversalReason", (ReversalReason == null) ? BlankChar : ReversalReason);
        hm.put("ReversalStatus", (ReversalStatus == null) ? BlankChar : ReversalStatus);
        hm.put("ReversalTimes", (ReversalTimes == null) ? "0" : ReversalTimes);

        try {
            TxnLogMgr = (TTxnLogMgr) Naming.lookup("rmi://" + APSystem.getRmiSrvName() + ":" + APSystem.getRmiSrvPort() + "/TTxnLog");
            int iRtn = TxnLogMgr.update_SP_TxnLog(SP_Cmd_02, hm);
            if (iRtn == 0) {
                return false;
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("updateRespTxnLog: 存取遠端 RMI 錯誤！");
            return false;
        } catch (Exception ex) {
            LOG.error("updateRespTxnLog: Other Exception:" + ex.getMessage());
            return false;
        } // try 1
        return true;
    }

    /**
     * update txn data to ReversalLog when receive RespReversalTxn data
     **/
    protected final boolean updateRespReversalLog(String OrgMsgSeqNo, int ReversalSeq) {
        TReversalLogMgr ReversalLogMgr;
        HashMap hm = null;
        LOG.info("method: - Start updateRespReversalLog(" + TxnDate + "-" + txnTml.getTmlID() + "-" + txnTml.getMsgSeqNo() + ")");
        String txnDateTime = Misc.genDate(Misc.DT_DATETIME);
        hm = new HashMap();
        hm.put("_TxnDate", TxnDate);
        hm.put("_TmlID", txnTml.getTmlID());
        hm.put("_OrgMsgSeqNo", OrgMsgSeqNo);
        hm.put("_ReversalSeq", String.valueOf(ReversalSeq));
        hm.put("RcvRespDT", txnDateTime);
        hm.put("RespCode", (RespCode == null) ? BlankChar : RespCode);
        hm.put("ActionCode", (ActionCode == null) ? BlankChar : ActionCode);

        try {
            ReversalLogMgr = (TReversalLogMgr) Naming.lookup("rmi://" + APSystem.getRmiSrvName() + ":" + APSystem.getRmiSrvPort() + "/TReversalLog");
            int iRtn = ReversalLogMgr.update_SP_ReversalLog(SP_Cmd_05, hm);
            if (iRtn == 0) {
                return false;
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("updateRespReversalLog: 存取遠端 RMI 錯誤！");
            return false;
        } catch (Exception ex) {
            LOG.error("updateRespReversalLog: Other Exception:" + ex.getMessage());
            return false;
        } // try 1
        return true;
    }

    /**
     * no use temporary
     **/
    protected boolean CheckMAC() throws Exception {

        // session, 不足24Byte補0x00
        String sSession = SessionID;
        if (sSession == null)
            sSession = "";
        byte[] baSession = new byte[24];
        if (sSession.length() <= 24) {
            System.arraycopy(sSession.getBytes(), 0, baSession, 0, sSession.length());
        } else {
            System.arraycopy(sSession.getBytes(), 0, baSession, 0, 24);
        }
        String sTAC = TAC;
        WSSecurity wss = null;
        if (wss == null) {
            wss = new WSSecurity((String) APSystem.getSYS_PRAM_MAP().get("WS_ADDR"), (String) APSystem.getSYS_PRAM_MAP().get("EC_SLOT"), CCConstants.WSS_SYS_ID, CCConstants.WSS_CHK_CODE,
                    CCConstants.WSS_CHK_USR, (String) APSystem.getSYS_PRAM_MAP().get("EC_PWD"));
        }
        String ComMac = wss.getComponentMAC(WSS_3DES_TYPE, WSS_3DES_KEY, sTAC, sSession).substring(0, 16);

        // System.out.println(ComMac);
        // if((new String(HexBin.bin2Hex(baMac))).equals((String)hmData.get("ICMAC"))) {
        if (ComMac.equals(MAC)) {
            return true;
        } else {
            return false;
        }
    }
}
