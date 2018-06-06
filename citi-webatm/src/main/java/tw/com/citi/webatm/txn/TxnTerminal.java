/*
 * @(#)TxnTerminal.java
 *
 * Copyright (c) 2008 Citi Group Incorporated. All rights reserved.
 *
 * Modify History:
 *  v1.00, 2008/07/07, Kevin Chung
 *   1) First release
 *
 */
package tw.com.citi.webatm.txn;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.citi.utils.Misc;
import com.citi.webatm.rmi.TTandemLog;
import com.citi.webatm.rmi.TTandemLogMgr;
import com.citi.webatm.rmi.TTmlInfo;
import com.citi.webatm.rmi.TTmlInfoMgr;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl;
import com.iisigroup.colabase.webatm.common.CCConstants;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapSystemConfig;

import tw.com.citi.elf.TxnC31077;
import tw.com.citi.elf.TxnC4101;
import tw.com.citi.elf.TxnC4105;
import tw.com.citi.elf.TxnC4801;
import tw.com.citi.elf.TxnC4802;
import tw.com.citi.elf.TxnConnect;
import tw.com.citi.elf.TxnMain;
import tw.com.citi.elf.TxnP4113;
import tw.com.citi.elf.TxnP4118;
import tw.com.citi.elf.TxnP4404;
import tw.com.citi.elf.TxnP4411;
import tw.com.citi.elf.TxnP4412;
import tw.com.citi.elf.TxnP4501;
import tw.com.citi.webatm.webap.signaler.TcpClient;
import tw.com.citi.ws.hsm.WSSecurity;

/**
 * include each main transaction interactive with TcpClient
 * 
 */
public class TxnTerminal {
    private static APSystemService APSystem = CapAppContext.getApplicationContext().getBean(APSystemServiceImpl.class);
    // log4J category
    private static Logger LOG = LoggerFactory.getLogger(TxnTerminal.class);
    @Autowired
    private CapSystemConfig sysProp = CapAppContext.getBean("systemConfig");

    private final static String SP_Cmd_01 = "SP_TmlInfo_upd_msgseqno";
    private final static String SP_Cmd_02 = "SP_TandemLog_ins";
    private final static String SP_Cmd_03 = "SP_TmlInfo_sel_next_msgseqno";

    // follow 6 param are setting in TrnsStatus for TxnProcess to log
    public final static String TrnsStatus_Success = "S"; // Success
    public final static String TrnsStatus_Timeout = "T"; // Timeout
    public final static String TrnsStatus_StopTrns = "ST"; // Connect Stop Trns
    public final static String TrnsStatus_RespError = "RE"; // Check Response Error
    public final static String TrnsStatus_MsgnoReset = "MR"; // Check ResponseCode = 17210 Error
    public final static String TrnsStatus_ConnError = "CE"; // Connect Server Error

    public final static int iMaxTrnsMsgSeqNo = 20000 + 1; // this value = Max + 1
    public final static int iMinTrnsMsgSeqNo = 1;

    private static boolean isInitHandShaking = true; // 是否完成 Initial HandShaking程序

    private static final String MaxMsgSeqNo = String.valueOf(Long.parseLong("FFFFFFFF", 16));
    private String TmlID = null;
    private String MsgSeqNo = null;
    private String IssuerBankCode = null;
    private String IssuerAccount = null;
    private String PCode = null;

    private String TrnsStatus = TrnsStatus_Success;
    private Hashtable hmSend = null;
    private Hashtable hmReceive = null;
    private TcpClient TC = null;
    private boolean isFree = true;
    private boolean isInit = true; // 是否完成 Initial 程序 -->目前只有Reset MsgSeqno 作業
    private int iReceiveTimeout = Integer.parseInt((String) APSystem.getSYS_PRAM_MAP().get("MsqTimeoutSEC")) * 1000;// 60 * 1000;
    private int iWaitSleep = 300; // TxnTml 接受不到Receive MsQ時Sleep間隔
    private int iRetryTimer = iReceiveTimeout / iWaitSleep;
    private static String KeyGeneration = null;

    public TxnTerminal(String tmlId) {
        TmlID = tmlId;
    }

    public TxnTerminal(String tmlId, TcpClient tc) {
        TmlID = tmlId;
        TC = tc;
    }

    public TxnTerminal(String tmlId, TcpClient tc, Hashtable send, Hashtable receive) {
        TmlID = tmlId;
        TC = tc;
        hmSend = send;
        hmReceive = receive;
    }

    /**
     * @param tc
     *            The tc to set.
     */
    public void setTC(TcpClient tc) {
        this.TC = tc;
    }

    /**
     * @return Returns the msgSeqNo.
     */
    public String getMsgSeqNo() {
        return MsgSeqNo;
    }

    /**
     * @param msgSeqNo
     *            The msgSeqNo to set.
     */
    public void setMsgSeqNo(String msgSeqNo) {
        MsgSeqNo = msgSeqNo;
    }

    /**
     * @return Returns the tmlID.
     */
    public String getTmlID() {
        return TmlID;
    }

    /**
     * @param tmlID
     *            The tmlID to set.
     */
    public void setTmlID(String tmlID) {
        TmlID = tmlID;
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
     * @param code
     *            The pCode to set.
     */
    public void setPCode(String code) {
        PCode = code;
    }

    /**
     * @return Returns the isFree.
     */
    public boolean isFree() {
        return isFree;
    }

    public void LockTml() {
        isFree = false;
    }

    public void FreeTml() {
        isFree = true;
    }

    /**
     * @return Returns the isInitHandShaking.
     */
    public static boolean isInitHandShaking() {
        return isInitHandShaking;
    }

    /**
     * @return Returns the isFree.
     */
    public boolean isInit() {
        return isInit;
    }

    public void FinishInit() {
        isInit = true;
    }

    public boolean getTC_isConnect() {
        return TC.getConnectStatus();
    }

    public boolean getTC_isReConnectFlag() {
        return TC.isReConnectFlag();
    }

    public void setTC_isReConnectFlag(boolean reconnectflag) {
        TC.setReConnectFlag(reconnectflag);
    }

    public int getTC_iKeepAliveCounter() {
        return TC.getKeepAliveCounter();
    }

    /**
     * @return Returns the trnStatus.
     */
    public String getTrnsStatus() {
        return TrnsStatus;
    }

    /**
     * @param isStopTrns
     *            The isStopTrns to set.
     */
    public void setStopTrns(boolean isStopTrns) {
        TC.setStopTrns(isStopTrns);
    }

    /**
     * @return Returns the keyGeneration.
     */
    public static String getKeyGeneration() {
        if (KeyGeneration == null)
            return "";
        return KeyGeneration;
    }

    /**
     * 1.Sign On Connect 2.Txn31077 3.get the BAFES timestamp 4.update OS DateTime
     **/
    public boolean doTimeSync() {
        int rtnCheckRsp;
        isInitHandShaking = false;
        try {
            int iSleep = 200;
            // Server Sign On Connect
            if (!TC.getConnectStatus())
                throw new Exception("Connect Server Error");

            TxnConnect txnConn = new TxnConnect("0", Misc.genDate(Misc.DT_DATETIME).substring(4));
            if (txnConn.ConstructBBMsg() == null)
                throw new Exception("ConstructMsg Error");
            hmSend.put(txnConn.getTmlID() + "-0-" + txnConn.getMsgSeqNo(), txnConn);

            Thread.sleep(iSleep);
            TC.doSend();

            TxnConnect txnConn_receive = null;
            String receive_key = txnConn.getTmlID() + "-0-" + txnConn.getMsgSeqNo();
            for (int i = iRetryTimer; i > 0; i--) {
                Thread.sleep(iWaitSleep);
                txnConn_receive = (TxnConnect) hmReceive.get(receive_key);
                LOG.debug("Waitting Counter :" + i);
                if (txnConn_receive != null) {
                    hmReceive.remove(receive_key);
                    break;
                }
                if (!TC.getConnectStatus())
                    throw new Exception("Connect Server Error");
                TC.doReceive();
                if (i == 1)
                    throw new Exception("Receive Msg Timeout");
            }

            if (txnConn_receive.doCheckRspMsg() != TxnMain.SUCCESS)
                throw new Exception("CheckRspMsg Error TxnConnect");

            // Get Host Time-31077
            LOG.debug("==========Run 31077-Get Host Time==========");

            // 因為怕在送31077、4101 時的MsgSeqNo == 20000 所以在啟動時就先將SeqNo Reset 為 1
            // updateTmlSeqNo(TmlID, "-1");
            // MsgSeqNo = "0";
            MsgSeqNo = getTml_SeqNo(TmlID);
            TxnC31077 txn = new TxnC31077(TmlID, MsgSeqNo);
            if (txn.ConstructBBMsg() == null)
                throw new Exception("ConstructMsg Error");
            insertTandemLog(txn, null, null, null, TTandemLog.TandemType_U);
            hmSend.put(txn.getTmlID() + "-" + txn.getMsgSeqNo() + "-31077", txn);

            TC.doSend();

            TxnC31077 txn_receive = null;
            receive_key = txn.getTmlID() + "-" + txn.getMsgSeqNo() + "-31077";
            for (int i = iRetryTimer; i > 0; i--) {
                Thread.sleep(iWaitSleep);
                txn_receive = (TxnC31077) hmReceive.get(receive_key);
                LOG.debug("Waitting Counter :" + i);
                if (txn_receive != null) {
                    hmReceive.remove(receive_key);
                    break;
                }
                if (!TC.getConnectStatus())
                    throw new Exception("Connect Server Error");
                TC.doReceive();
                if (i == 1)
                    throw new Exception("Receive Msg Timeout");
            }

            txn_receive.Deconstruct();

            if ((rtnCheckRsp = txn_receive.doCheckRspMsg()) != TxnMain.SUCCESS)
                if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                    insertTandemLog(txn_receive, null, null, null, TTandemLog.TandemType_G);
                    throw new Exception("CheckRspMsg Error 31077");
                } else
                    throw new Exception("CheckRspMsg Error 31077");

            insertTandemLog(txn_receive, null, null, null, TTandemLog.TandemType_D);
            updateTmlSeqNo(TmlID, String.valueOf(Integer.parseInt(MsgSeqNo) - 1)); // for test 2008.10.13

            String datetime = txn_receive.getTimestamp_R();
            // System.out.println(txn_receive.getTimestamp_R());

            // 執行 Runtime.getRuntime().exec() 校時

            // BAFES時間為格林威治時間，所以要+8小時
            Calendar now = Calendar.getInstance();

            now.set(Calendar.YEAR, Integer.parseInt(datetime.substring(0, 4)));
            now.set(Calendar.MONTH, Integer.parseInt(datetime.substring(4, 6)) - 1);
            now.set(Calendar.DATE, Integer.parseInt(datetime.substring(6, 8)));
            now.set(Calendar.HOUR_OF_DAY, Integer.parseInt(datetime.substring(8, 10)));
            now.set(Calendar.MINUTE, Integer.parseInt(datetime.substring(10, 12)));
            now.set(Calendar.SECOND, Integer.parseInt(datetime.substring(12)));
            now.add(Calendar.HOUR_OF_DAY, 8);

            LOG.debug("Server Timestamp + 8 Hours = " + now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE) + " " + now.get(Calendar.HOUR_OF_DAY) + ":"
                    + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND));

            LOG.debug("Server Timestamp = " + datetime.substring(0, 4) + "-" + datetime.substring(4, 6) + "-" + datetime.substring(6, 8) + " " + datetime.substring(8, 10) + ":"
                    + datetime.substring(10, 12) + ":" + datetime.substring(12));

            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec("cmd /c date " +
            // datetime.substring(0,4) + "-" +
            // datetime.substring(4,6) + "-" +
            // datetime.substring(6,8));
                    now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE));

            process = rt.exec("cmd /c time " +
            // datetime.substring(8,10) + ":" +
            // datetime.substring(10,12) + ":" +
            // datetime.substring(12));
                    now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND));

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 1.Txn4101 2.Receive 4801,Send 4801 Response 3.Receive 4802,Send 4802 Response 4.update Working Key
     **/
    public boolean initKeyExchange() {
        try {
            int rtnCheckRsp;
            int iSleep = 200;
            if (!TC.getConnectStatus())
                throw new Exception("Connect Server Error");
            LOG.debug("==========Run 4101-Host Introductory Message==========");
            // ------------------4101--------------------------
            // updateTmlSeqNo(TmlID, String.valueOf(-1));
            MsgSeqNo = getTml_SeqNo(TmlID);
            TxnC4101 txn4101 = new TxnC4101(TmlID, MsgSeqNo);
            txn4101.ConstructBBMsg();
            insertTandemLog(txn4101, null, null, null, TTandemLog.TandemType_U);
            // hmSend.put(txn4101.getTmlID()+"-"+txn4101.getMsgSeqNo()+"-4101", txn4101);

            hmSend.put(txn4101.getTmlID() + "-4101", txn4101); // for test
            Thread.sleep(iSleep);
            TC.doSend();

            TxnC4101 txn4101_receive = null;
            String receive_key = txn4101.getTmlID() + "-4101";
            // String receive_key = txn4101.getTmlID()+"-"+txn4101.getMsgSeqNo()+"-4101"; // for test
            // String receive_key = txn4101.getTmlID()+"-"+ (Integer.parseInt(txn4101.getMsgSeqNo())+1)+"-4101";
            for (int i = iRetryTimer; i > 0; i--) {
                Thread.sleep(iWaitSleep);
                txn4101_receive = (TxnC4101) hmReceive.get(receive_key);
                LOG.debug("Waitting Counter :" + i + "--" + receive_key);
                if (txn4101_receive != null) {
                    hmReceive.remove(receive_key);
                    break;
                }
                if (!TC.getConnectStatus())
                    throw new Exception("Connect Server Error");
                TC.doReceive();
                if (i == 1)
                    throw new Exception("Receive Msg Timeout");
            }

            txn4101_receive.Deconstruct();

            // if (txn4101_receive.doCheckRspMsg() != TxnMain.SUCCESS)
            if ((rtnCheckRsp = txn4101.doCheckRspMsg(txn4101_receive)) != TxnMain.SUCCESS)
                if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                    insertTandemLog(txn4101_receive, null, null, null, TTandemLog.TandemType_G);
                    throw new Exception("CheckRspMsg Error 4101");
                } else
                    throw new Exception("CheckRspMsg Error 4101");

            if (txn4101_receive.getAcqRespCode_R().equals(TxnMain.AcqRespCode_R_ResetSeq)) {
                insertTandemLog(txn4101_receive, null, null, null, TTandemLog.TandemType_D);
                if (!doTmlSeqReset()) {
                    LOG.error("Reset Tml MsgSeqNo 發生錯誤！");
                }
                TrnsStatus = TrnsStatus_MsgnoReset;
                // throw new Exception("RspCode = 17210 need to Reset MsgSeqNo");
            }

            insertTandemLog(txn4101_receive, null, null, null, TTandemLog.TandemType_D);
            LOG.debug("Update MsgSeqNo -->" + txn4101_receive.getMsgSeqNo_R());
            // updateTmlSeqNo(TmlID, txn4101_receive.getMsgSeqNo_R());
            // ------------------4101--------------------------

            LOG.debug("==========Run 4801-Host Encryption Key Load==========");
            boolean isFinish = false;
            int iLastMsgSeqNo = Integer.parseInt(txn4101.getMsgSeqNo()) + 1;
            int counter = iRetryTimer;
            String tmlID = txn4101.getTmlID();
            TxnC4801 txn4801_receive = null;
            TxnC4802 txn4802_receive = null;
            String keytype = "B2";
            String key_generation = null;
            WSSecurity wss = null;
            while (!isFinish) {
                if (counter == 1)
                    throw new Exception("Receive Msg Timeout");
                Thread.sleep(iWaitSleep);
                txn4801_receive = (TxnC4801) hmReceive.get(tmlID + "-0-4801");

                LOG.debug("Waitting Counter :" + counter);
                if (txn4801_receive != null) {
                    hmReceive.remove(tmlID + "-0-4801");
                    if ((rtnCheckRsp = txn4801_receive.doCheckRspMsg()) != TxnMain.SUCCESS)
                        if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                            insertTandemLog(txn4801_receive, null, null, null, TTandemLog.TandemType_G);
                            throw new Exception("CheckRspMsg Error 4801-1");
                        } else
                            throw new Exception("CheckRspMsg Error 4801-1");

                    // -----------txn4801 special Check
                    /*
                     * if (iLastMsgSeqNo != Integer.parseInt(txn4801_receive.getMsgSeqNo_R())) throw new Exception("4801-1.MsgSeqNo != LastMsgSeqNo "
                     * +txn4801_receive.getMsgSeqNo_R()+"-"+iLastMsgSeqNo); else iLastMsgSeqNo ++;
                     */ // -----------txn4801 special Check

                    insertTandemLog(txn4801_receive, null, null, null, TTandemLog.TandemType_D);
                    LOG.debug("Update MsgSeqNo -->" + txn4801_receive.getMsgSeqNo_R());

                    int Modifier = Integer.parseInt(txn4801_receive.getEkeyModifier_R());
                    if (wss == null) {
                        wss = new WSSecurity((String) APSystem.getSYS_PRAM_MAP().get("WS_ADDR"), (String) APSystem.getSYS_PRAM_MAP().get("EC_SLOT"), CCConstants.WSS_SYS_ID, CCConstants.WSS_CHK_CODE,
                                CCConstants.WSS_CHK_USR, (String) APSystem.getSYS_PRAM_MAP().get("EC_PWD"));
                    }

                    switch (Modifier) {
                    case 1:
                        LOG.debug("update A2");
                        break;
                    case 2:
                        LOG.debug("update B1");
                        break;
                    case 3:
                        String chkDigits = null;
                        key_generation = txn4801_receive.getKEYGeneration_R();
                        LOG.debug("update B2_" + key_generation);

                        if (keytype.equals("B2")) {
                            boolean result = wss.importKey(APSystem.getCOMM_KEY_TYPE(), APSystem.getWorkB_KEY() + "_" + key_generation, APSystem.getCOMM_KEY_TYPE(),
                                    APSystem.getWorkB_KEY() + "_" + key_generation, txn4801_receive.getEkeyDLenData_R(), "0000000000000000");
                            if (result)
                                chkDigits = wss.do3DESEncrypt(APSystem.getCOMM_KEY_TYPE(), APSystem.getWorkB_KEY() + "_" + key_generation, "00000000000000000000000000000000");
                        } else if (keytype.equals("B1")) {
                            boolean result = wss.importKey(APSystem.getCOMM_KEY_TYPE(), APSystem.getWorkB_KEY() + "_" + key_generation, APSystem.getCOMM_KEY_TYPE(), APSystem.getInitB_KEY(),
                                    txn4801_receive.getEkeyDLenData_R(), "0000000000000000");
                            if (result)
                                chkDigits = wss.do3DESEncrypt(APSystem.getCOMM_KEY_TYPE(), APSystem.getWorkB_KEY() + "_" + key_generation, "00000000000000000000000000000000");
                        }

                        LOG.debug("checkDigits=[" + chkDigits + "]" + key_generation);

                        TxnC4801 txn4801_2 = new TxnC4801(TmlID, txn4801_receive.getMsgSeqNo_R());
                        txn4801_2.setKeyChkDigits((chkDigits == null) ? "0000" : chkDigits.substring(0, 4));
                        txn4801_2.ConstructBBMsg();
                        insertTandemLog(txn4801_2, null, null, null, TTandemLog.TandemType_U);
                        hmSend.put(txn4801_2.getTmlID() + "-" + txn4801_receive.getMsgSeqNo() + "-4801", txn4801_2);
                        Thread.sleep(iSleep);
                        TC.doSend();
                        break;
                    case 4:
                        LOG.debug("set key = B1");

                        chkDigits = wss.do3DESEncrypt(APSystem.getCOMM_KEY_TYPE(), APSystem.getInitB_KEY(), "00000000000000000000000000000000");
                        keytype = "B1";

                        LOG.debug("checkDigits=[" + chkDigits + "]");

                        TxnC4801 txn4801_1 = new TxnC4801(TmlID, txn4801_receive.getMsgSeqNo_R());
                        txn4801_1.setKeyChkDigits((chkDigits == null) ? "0000" : chkDigits.substring(0, 4));
                        txn4801_1.ConstructBBMsg();
                        insertTandemLog(txn4801_1, null, null, null, TTandemLog.TandemType_U);
                        hmSend.put(txn4801_1.getTmlID() + "-" + txn4801_1.getMsgSeqNo() + "-4801", txn4801_1);
                        Thread.sleep(iSleep);
                        TC.doSend();
                        break;
                    case 5:
                        LOG.debug("set key = A1");
                        break;
                    default:
                        throw new Exception("no this kind Modifier");
                    }
                    txn4801_receive = null;
                    counter = iRetryTimer;
                } else {
                    LOG.debug("==========Run 4802-Host End Key Load==========");
                    txn4802_receive = (TxnC4802) hmReceive.get(tmlID + "-0-4802");
                    if (txn4802_receive != null) {
                        hmReceive.remove(tmlID + "-0-4802");
                        if ((rtnCheckRsp = txn4802_receive.doCheckRspMsg()) != TxnMain.SUCCESS)
                            if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                                insertTandemLog(txn4802_receive, null, null, null, TTandemLog.TandemType_G);
                                throw new Exception("CheckRspMsg Error 4802");
                            } else
                                throw new Exception("CheckRspMsg Error 4802");

                        // -----------txn4802 special Check
                        /*
                         * if (iLastMsgSeqNo != Integer.parseInt(txn4802_receive.getMsgSeqNo_R())) throw new Exception("4802.MsgSeqNo != LastMsgSeqNo "
                         * +txn4802_receive.getMsgSeqNo_R()+"-"+iLastMsgSeqNo);
                         */ // -----------txn4802 special Check

                        insertTandemLog(txn4802_receive, null, null, null, TTandemLog.TandemType_D);
                        LOG.debug("Update MsgSeqNo -->" + txn4802_receive.getMsgSeqNo_R());
                        // updateTmlSeqNo(TmlID, txn4802_receive.getMsgSeqNo_R()); for test 20081014
                        // System.out.println(txn4802_receive.getVersNumber_R());

                        TxnC4802 txn4802 = new TxnC4802(TmlID, txn4802_receive.getMsgSeqNo_R());
                        txn4802.setRespCode("00");
                        txn4802.ConstructBBMsg();
                        insertTandemLog(txn4802, null, null, null, TTandemLog.TandemType_U);
                        hmSend.put(txn4802.getTmlID() + "-" + txn4802.getMsgSeqNo() + "-4802", txn4802);
                        Thread.sleep(iSleep);
                        TC.doSend();
                        isFinish = true;
                        KeyGeneration = key_generation;
                    }
                }
                if (!TC.getConnectStatus())
                    throw new Exception("Connect Server Error");
                TC.doReceive();
                counter--;
            }

            Thread.sleep(iSleep);
            TC.dumpPairMapping();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            LOG.error(e.getMessage());
            return false;
        }

        isInitHandShaking = true;
        return true;
    }

    /**
     * doKeyExchange for daily working key change 1.Txn4105 2.Receive 4801,Send 4801 Response 3.Receive 4802,Send 4802 Response 4.update Working Key
     **/
    public boolean doKeyExchange() {
        int rtnCheckRsp;
        int iSleep = 900;
        try {
            // ------------------4105--------------------------
            LOG.debug("==========Run 4105-Encryption Key Request==========");
            if (!TC.getConnectStatus())
                throw new Exception("Connect Server Error");
            MsgSeqNo = getTml_SeqNo(TmlID);
            TxnC4105 txn4105 = new TxnC4105(TmlID, MsgSeqNo);
            if (txn4105.ConstructBBMsg() == null)
                throw new Exception("ConstructMsg Error");
            insertTandemLog(txn4105, null, null, null, TTandemLog.TandemType_U);
            hmSend.put(txn4105.getTmlID() + "-" + txn4105.getMsgSeqNo() + "-4105", txn4105);

            TC.doSend();

            TxnC4105 txn4105_receive = null;
            for (int i = iRetryTimer; i > 0; i--) {
                Thread.sleep(iWaitSleep);
                txn4105_receive = (TxnC4105) hmReceive.get(txn4105.getTmlID() + "-" + txn4105.getMsgSeqNo() + "-4105");
                LOG.debug("Waitting Counter :" + i);
                if (txn4105_receive != null)
                    break;
                if (!TC.getConnectStatus())
                    throw new Exception("Connect Server Error");
                TC.doReceive();
            }
            txn4105_receive.Deconstruct();
            if ((rtnCheckRsp = txn4105.doCheckRspMsg(txn4105_receive)) != TxnMain.SUCCESS)
                if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                    insertTandemLog(txn4105_receive, null, null, null, TTandemLog.TandemType_G);
                    throw new Exception("CheckRspMsg Error 4105");
                } else
                    throw new Exception("CheckRspMsg Error 4105");

            insertTandemLog(txn4105_receive, null, null, null, TTandemLog.TandemType_D);
            LOG.debug("Update MsgSeqNo -->" + txn4105_receive.getMsgSeqNo_R());
            updateTmlSeqNo(TmlID, txn4105_receive.getMsgSeqNo_R());
            // ------------------4105--------------------------

            LOG.debug("==========Run 4801-Host Encryption Key Load==========");
            boolean isFinish = false;
            int iLastMsgSeqNo = Integer.parseInt(txn4105.getMsgSeqNo()) + 1;
            int counter = iRetryTimer;
            String tmlID = txn4105.getTmlID();
            TxnC4801 txn4801_receive = null;
            TxnC4802 txn4802_receive = null;
            String keytype = "B2";
            String key_generation = null;
            WSSecurity wss = null;
            while (!isFinish) {
                if (counter == 1)
                    throw new Exception("Receive Msg Timeout");
                Thread.sleep(iWaitSleep);
                txn4801_receive = (TxnC4801) hmReceive.get(tmlID + "-0-4801");

                LOG.debug("Waitting Counter :" + counter);
                if (txn4801_receive != null) {
                    hmReceive.remove(tmlID + "-0-4801");
                    if ((rtnCheckRsp = txn4801_receive.doCheckRspMsg()) != TxnMain.SUCCESS)
                        if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                            insertTandemLog(txn4801_receive, null, null, null, TTandemLog.TandemType_G);
                            throw new Exception("CheckRspMsg Error 4801-1");
                        } else
                            throw new Exception("CheckRspMsg Error 4801-1");

                    // -----------txn4801 special Check
                    /*
                     * if (iLastMsgSeqNo != Integer.parseInt(txn4801_receive.getMsgSeqNo_R())) throw new Exception("4801-1.MsgSeqNo != LastMsgSeqNo "
                     * +txn4801_receive.getMsgSeqNo_R()+"-"+iLastMsgSeqNo); else iLastMsgSeqNo ++;
                     */ // -----------txn4801 special Check

                    insertTandemLog(txn4801_receive, null, null, null, TTandemLog.TandemType_D);
                    LOG.debug("Update MsgSeqNo -->" + txn4801_receive.getMsgSeqNo_R());

                    int Modifier = Integer.parseInt(txn4801_receive.getEkeyModifier_R());
                    if (wss == null) {
                        wss = new WSSecurity((String) APSystem.getSYS_PRAM_MAP().get("WS_ADDR"), (String) APSystem.getSYS_PRAM_MAP().get("EC_SLOT"), CCConstants.WSS_SYS_ID, CCConstants.WSS_CHK_CODE,
                                CCConstants.WSS_CHK_USR, (String) APSystem.getSYS_PRAM_MAP().get("EC_PWD"));
                    }

                    switch (Modifier) {
                    case 1:
                        LOG.debug("update A2");
                        break;
                    case 2:
                        LOG.debug("update B1");
                        break;
                    case 3:
                        String chkDigits = null;
                        key_generation = txn4801_receive.getKEYGeneration_R();
                        LOG.debug("update B2_" + key_generation);

                        if (keytype.equals("B2")) {
                            boolean result = wss.importKey(APSystem.getCOMM_KEY_TYPE(), APSystem.getWorkB_KEY() + "_" + key_generation, APSystem.getCOMM_KEY_TYPE(),
                                    APSystem.getWorkB_KEY() + "_" + key_generation, txn4801_receive.getEkeyDLenData_R(), "0000000000000000");

                            int i = 9;
                            while (!result && i >= 0) {
                                LOG.debug("Try importKey countdown:" + i);
                                i--;
                                Thread.sleep(iSleep);
                                result = wss.importKey(APSystem.getCOMM_KEY_TYPE(), APSystem.getWorkB_KEY() + "_" + key_generation, APSystem.getCOMM_KEY_TYPE(),
                                        APSystem.getWorkB_KEY() + "_" + key_generation, txn4801_receive.getEkeyDLenData_R(), "0000000000000000");
                            }
                            Thread.sleep(iSleep * 3);

                            if (result)
                                chkDigits = wss.do3DESEncrypt(APSystem.getCOMM_KEY_TYPE(), APSystem.getWorkB_KEY() + "_" + key_generation, "00000000000000000000000000000000");
                        } else if (keytype.equals("B1")) {
                            boolean result = wss.importKey(APSystem.getCOMM_KEY_TYPE(), APSystem.getWorkB_KEY() + "_" + key_generation, APSystem.getCOMM_KEY_TYPE(), APSystem.getInitB_KEY(),
                                    txn4801_receive.getEkeyDLenData_R(), "0000000000000000");
                            if (result)
                                chkDigits = wss.do3DESEncrypt(APSystem.getCOMM_KEY_TYPE(), APSystem.getWorkB_KEY() + "_" + key_generation, "00000000000000000000000000000000");
                        }

                        LOG.debug("checkDigits=[" + chkDigits + "]" + key_generation);

                        TxnC4801 txn4801_2 = new TxnC4801(TmlID, txn4801_receive.getMsgSeqNo_R());
                        txn4801_2.setKeyChkDigits((chkDigits == null) ? "0000" : chkDigits.substring(0, 4));
                        txn4801_2.ConstructBBMsg();
                        insertTandemLog(txn4801_2, null, null, null, TTandemLog.TandemType_U);
                        hmSend.put(txn4801_2.getTmlID() + "-" + txn4801_receive.getMsgSeqNo() + "-4801", txn4801_2);
                        Thread.sleep(iSleep);
                        TC.doSend();
                        break;
                    case 4:
                        LOG.debug("set key = B1");
                        wss = null;
                        wss = new WSSecurity((String) APSystem.getSYS_PRAM_MAP().get("WS_ADDR"), (String) APSystem.getSYS_PRAM_MAP().get("EC_SLOT"), CCConstants.WSS_SYS_ID, CCConstants.WSS_CHK_CODE,
                                CCConstants.WSS_CHK_USR, (String) APSystem.getSYS_PRAM_MAP().get("EC_PWD"));

                        chkDigits = wss.do3DESEncrypt(APSystem.getCOMM_KEY_TYPE(), APSystem.getInitB_KEY(), "00000000000000000000000000000000");
                        keytype = "B1";
                        LOG.debug("checkDigits=[" + chkDigits + "]");

                        TxnC4801 txn4801_1 = new TxnC4801(TmlID, txn4801_receive.getMsgSeqNo_R());
                        txn4801_1.setKeyChkDigits((chkDigits == null || chkDigits.equals("")) ? "0000" : chkDigits.substring(0, 4));
                        txn4801_1.ConstructBBMsg();
                        insertTandemLog(txn4801_1, null, null, null, TTandemLog.TandemType_U);
                        hmSend.put(txn4801_1.getTmlID() + "-" + txn4801_1.getMsgSeqNo() + "-4801", txn4801_1);
                        Thread.sleep(iSleep);
                        TC.doSend();
                        break;
                    case 5:
                        LOG.debug("set key = A1");
                        break;
                    default:
                        throw new Exception("no this kind Modifier");
                    }
                    txn4801_receive = null;
                    counter = iRetryTimer;
                } else {
                    LOG.debug("==========Run 4802-Host End Key Load==========");
                    txn4802_receive = (TxnC4802) hmReceive.get(tmlID + "-0-4802");
                    if (txn4802_receive != null) {
                        hmReceive.remove(tmlID + "-0-4802");
                        if ((rtnCheckRsp = txn4802_receive.doCheckRspMsg()) != TxnMain.SUCCESS)
                            if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                                insertTandemLog(txn4802_receive, null, null, null, TTandemLog.TandemType_G);
                                throw new Exception("CheckRspMsg Error 4802");
                            } else
                                throw new Exception("CheckRspMsg Error 4802");

                        // -----------txn4802 special Check
                        /*
                         * if (iLastMsgSeqNo != Integer.parseInt(txn4802_receive.getMsgSeqNo_R())) throw new Exception("4802.MsgSeqNo != LastMsgSeqNo "
                         * +txn4802_receive.getMsgSeqNo_R()+"-"+iLastMsgSeqNo);
                         */ // -----------txn4802 special Check

                        insertTandemLog(txn4802_receive, null, null, null, TTandemLog.TandemType_D);
                        LOG.debug("Update MsgSeqNo -->" + txn4802_receive.getMsgSeqNo_R());
                        // updateTmlSeqNo(TmlID, txn4802_receive.getMsgSeqNo_R());
                        // System.out.println(txn4802_receive.getVersNumber_R());

                        TxnC4802 txn4802 = new TxnC4802(TmlID, txn4802_receive.getMsgSeqNo_R());
                        txn4802.setRespCode("00");
                        txn4802.ConstructBBMsg();
                        insertTandemLog(txn4802, null, null, null, TTandemLog.TandemType_U);
                        hmSend.put(txn4802.getTmlID() + "-" + txn4802.getMsgSeqNo() + "-4802", txn4802);
                        Thread.sleep(iSleep);
                        TC.doSend();
                        isFinish = true;
                        KeyGeneration = key_generation;
                    }
                }
                if (!TC.getConnectStatus())
                    throw new Exception("Connect Server Error");
                TC.doReceive();
                counter--;
            }

            Thread.sleep(iSleep);
            TC.dumpPairMapping();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            LOG.error(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * doTmlSeqReset when Receive BAFES 17210 ERROR 1.Txn4101
     **/
    public boolean doTmlSeqReset() {
        int rtnCheckRsp;
        isInit = false;
        int iSleep = 300;
        try {
            LOG.debug("==========Run 4101-Host Introductory Message==========");
            // ------------------4101--------------------------
            if (!TC.getConnectStatus())
                throw new Exception("Connect Server Error");
            TxnC4101 txn4101 = new TxnC4101(TmlID, "0");
            txn4101.ConstructBBMsg();
            insertTandemLog(txn4101, null, null, null, TTandemLog.TandemType_U);
            hmSend.put(txn4101.getTmlID() + "-4101", txn4101); // for test

            // hmSend.put(txn4101.getTmlID()+"-"+txn4101.getMsgSeqNo()+"-4101", txn4101);

            Thread.sleep(iSleep);
            TC.doSend();

            TxnC4101 txn4101_receive = null;
            String receive_key = txn4101.getTmlID() + "-4101";
            // String receive_key = txn4101.getTmlID()+"-"+txn4101.getMsgSeqNo()+"-4101";
            for (int i = iRetryTimer; i > 0; i--) {
                Thread.sleep(iWaitSleep);
                txn4101_receive = (TxnC4101) hmReceive.get(receive_key);
                LOG.debug("Waitting Counter :" + i);
                if (txn4101_receive != null) {
                    hmReceive.remove(receive_key);
                    break;
                }
                if (!TC.getConnectStatus())
                    throw new Exception("Connect Server Error");
                TC.doReceive();
                if (i == 1)
                    throw new Exception("Receive Msg Timeout");
            }

            txn4101_receive.Deconstruct();
            if ((rtnCheckRsp = txn4101.doCheckRspMsg(txn4101_receive)) != TxnMain.SUCCESS)
                if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                    insertTandemLog(txn4101_receive, null, null, null, TTandemLog.TandemType_G);
                    throw new Exception("CheckRspMsg Error 4101");
                } else
                    throw new Exception("CheckRspMsg Error 4101");

            // TmlSeqReset AcqRespCode must equal 00000;
            if (!txn4101_receive.getAcqRespCode_R().equals(TxnMain.AcqRespCode_R_Success)) {
                insertTandemLog(txn4101_receive, null, null, null, TTandemLog.TandemType_G);
                throw new Exception("RspCode = " + txn4101_receive.getAcqRespCode_R() + " != 00000");
            }

            insertTandemLog(txn4101_receive, null, null, null, TTandemLog.TandemType_D);
            LOG.debug("Update MsgSeqNo -->" + txn4101_receive.getMsgSeqNo_R());
            updateTmlSeqNo(TmlID, txn4101_receive.getMsgSeqNo_R());
            // ------------------4101--------------------------

            TC.dumpPairMapping();
            // Thread.sleep(1000);
            // tc.dumpPairMapping();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            LOG.error(e.getMessage());
            isInit = false;
            return false;
        }
        isInit = true;
        return true;
    }

    /**
     * Send Transaction to BAFES 1.Txn4501
     **/
    public TxnP4501 doTxnP4501(TxnP4501 txn4501) {
        int rtnCheckRsp;
        int iSleep = 200;
        TrnsStatus = TrnsStatus_Success;
        TxnP4501 txn4501_receive = null;
        try {
            LOG.debug("==========Run 4501-Balance Inquiry==========");
            // ------------------4501--------------------------
            if (!TC.getConnectStatus()) {
                TrnsStatus = TrnsStatus_ConnError;
                throw new Exception("Connect Server Error");
            }
            if (TC.isStopTrns()) {
                TrnsStatus = TrnsStatus_StopTrns;
                throw new Exception("StopTrns only for handshaking");
            }
            // MsgSeqNo = getTml_SeqNo(TmlID);
            // TxnP4501 txn4501 = new TxnP4501(TmlID, MsgSeqNo);
            txn4501.ConstructBBMsg();
            insertTandemLog(txn4501, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_U);
            hmSend.put(txn4501.getTmlID() + "-" + txn4501.getMsgSeqNo() + "-4501", txn4501);

            Thread.sleep(iSleep);
            TC.doSend();

            String receive_key = txn4501.getTmlID() + "-" + txn4501.getMsgSeqNo() + "-4501";
            for (int i = iRetryTimer; i > 0; i--) {
                Thread.sleep(iWaitSleep);
                txn4501_receive = (TxnP4501) hmReceive.get(receive_key);
                LOG.debug("Waitting Counter :" + i + "-" + txn4501.getTmlID() + "-" + txn4501.getMsgSeqNo());
                if (txn4501_receive != null) {
                    hmReceive.remove(receive_key);
                    break;
                }
                if (!TC.getConnectStatus()) {
                    TrnsStatus = TrnsStatus_ConnError;
                    throw new Exception("Connect Server Error");
                }
                TC.doReceive();
                if (i == 1) {
                    TrnsStatus = TrnsStatus_Timeout;
                    throw new Exception("Receive Msg Timeout");
                }
            }

            txn4501_receive.Deconstruct();
            if (txn4501.doCheckRspMsg(txn4501_receive) != TxnMain.SUCCESS) {

                throw new Exception("CheckRspMsg Error 4501");
            }
            if ((rtnCheckRsp = txn4501.doCheckRspMsg(txn4501_receive)) != TxnMain.SUCCESS) {
                if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                    TrnsStatus = TrnsStatus_RespError;
                    insertTandemLog(txn4501_receive, null, null, null, TTandemLog.TandemType_G);
                    throw new Exception("CheckRspMsg Error 4501");
                } else {
                    TrnsStatus = TrnsStatus_RespError;
                    throw new Exception("CheckRspMsg Error 4501");
                }
            }

            if (txn4501_receive.getAcqRespCode_R().equals(TxnMain.AcqRespCode_R_ResetSeq)) {
                insertTandemLog(txn4501_receive, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_D);
                if (!doTmlSeqReset()) {
                    LOG.error("Reset Tml MsgSeqNo 發生錯誤！");
                }
                TrnsStatus = TrnsStatus_MsgnoReset;
                throw new Exception("RspCode = 17210 need to Reset MsgSeqNo");
            }

            insertTandemLog(txn4501_receive, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_D);
            LOG.debug("Update MsgSeqNo -->" + txn4501_receive.getMsgSeqNo_R());
            // updateTmlSeqNo(TmlID, txn4501_receive.getMsgSeqNo_R());
            // ------------------4501--------------------------
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            LOG.error(e.getMessage());
            if (TrnsStatus.equals(TrnsStatus_MsgnoReset)) {
                return txn4501_receive;
            }

            return null;
        }
        return txn4501_receive;
    }

    /**
     * Send Transaction to BAFES 1.Txn4411
     **/
    public TxnP4411 doTxnP4411(TxnP4411 txn4411) {
        int rtnCheckRsp;
        int iSleep = 200;
        TrnsStatus = TrnsStatus_Success;
        TxnP4411 txn4411_receive = null;
        try {
            LOG.debug("==========Run 4411-InterCiti Payment/Transfer==========");
            // ------------------4411--------------------------
            if (!TC.getConnectStatus()) {
                TrnsStatus = TrnsStatus_ConnError;
                throw new Exception("Connect Server Error");
            }
            if (TC.isStopTrns()) {
                TrnsStatus = TrnsStatus_StopTrns;
                throw new Exception("StopTrns only for handshaking");
            }
            // MsgSeqNo = getTml_SeqNo(TmlID);
            // TxnP4411 txn4411 = new TxnP4411(TmlID, MsgSeqNo);
            txn4411.ConstructBBMsg();
            insertTandemLog(txn4411, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_U);
            hmSend.put(txn4411.getTmlID() + "-" + txn4411.getMsgSeqNo() + "-4411", txn4411);

            Thread.sleep(iSleep);
            TC.doSend();

            String receive_key = txn4411.getTmlID() + "-" + txn4411.getMsgSeqNo() + "-4411";
            for (int i = iRetryTimer; i > 0; i--) {
                Thread.sleep(iWaitSleep);
                txn4411_receive = (TxnP4411) hmReceive.get(receive_key);
                LOG.debug("Waitting Counter :" + i + "-" + txn4411.getTmlID() + "-" + txn4411.getMsgSeqNo());
                if (txn4411_receive != null) {
                    hmReceive.remove(receive_key);
                    break;
                }
                if (!TC.getConnectStatus()) {
                    TrnsStatus = TrnsStatus_ConnError;
                    throw new Exception("Connect Server Error");
                }
                TC.doReceive();
                if (i == 1) {
                    TrnsStatus = TrnsStatus_Timeout;
                    throw new Exception("Receive Msg Timeout");
                }
            }

            txn4411_receive.Deconstruct();
            if ((rtnCheckRsp = txn4411.doCheckRspMsg(txn4411_receive)) != TxnMain.SUCCESS) {
                if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                    TrnsStatus = TrnsStatus_RespError;
                    insertTandemLog(txn4411_receive, null, null, null, TTandemLog.TandemType_G);
                    throw new Exception("CheckRspMsg Error 4411");
                } else {
                    TrnsStatus = TrnsStatus_RespError;
                    throw new Exception("CheckRspMsg Error 4411");
                }
            }

            if (txn4411_receive.getAcqRespCode_R().equals(TxnMain.AcqRespCode_R_ResetSeq)) {
                insertTandemLog(txn4411_receive, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_D);
                if (!doTmlSeqReset()) {
                    LOG.error("Reset Tml MsgSeqNo 發生錯誤！");
                }
                TrnsStatus = TrnsStatus_MsgnoReset;
                throw new Exception("RspCode = 17210 need to Reset MsgSeqNo");
            }

            insertTandemLog(txn4411_receive, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_D);
            LOG.debug("Update MsgSeqNo -->" + txn4411_receive.getMsgSeqNo_R());
            // updateTmlSeqNo(TmlID, txn4411_receive.getMsgSeqNo_R());
            // ------------------4411--------------------------

            // System.out.println(txn4411_receive.getAcqRespCode_R());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            LOG.error(e.getMessage());
            // 交易序號小於BAFES預期值，雖然有Exception 不過還是要Return Receive Data..
            if (TrnsStatus.equals(TrnsStatus_MsgnoReset)) {
                return txn4411_receive;
            }
            return null;
        }
        return txn4411_receive;
    }

    /**
     * Send Transaction to BAFES 1.Txn4412
     **/
    public TxnP4412 doTxnP4412(TxnP4412 txn4412) {
        int rtnCheckRsp;
        int iSleep = 200;
        TrnsStatus = TrnsStatus_Success;
        TxnP4412 txn4412_receive = null;
        try {
            LOG.debug("==========Run 4412-InterCiti Payment/Transfer Reversal==========");
            // ------------------4412--------------------------
            if (!TC.getConnectStatus()) {
                TrnsStatus = TrnsStatus_ConnError;
                throw new Exception("Connect Server Error");
            }
            if (TC.isStopTrns()) {
                TrnsStatus = TrnsStatus_StopTrns;
                throw new Exception("StopTrns only for handshaking");
            }
            // MsgSeqNo = getTml_SeqNo(TmlID);
            // TxnP4412 txn4412 = new TxnP4412(TmlID, MsgSeqNo);
            txn4412.ConstructBBMsg();
            insertTandemLog(txn4412, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_U);
            hmSend.put(txn4412.getTmlID() + "-" + txn4412.getMsgSeqNo() + "-4412", txn4412);

            Thread.sleep(iSleep);
            TC.doSend();

            String receive_key = txn4412.getTmlID() + "-" + txn4412.getMsgSeqNo() + "-4412";
            for (int i = iRetryTimer; i > 0; i--) {
                Thread.sleep(iWaitSleep);
                txn4412_receive = (TxnP4412) hmReceive.get(receive_key);
                LOG.debug("Waitting Counter :" + i + "-" + txn4412.getTmlID() + "-" + txn4412.getMsgSeqNo());
                if (txn4412_receive != null) {
                    hmReceive.remove(receive_key);
                    break;
                }
                if (!TC.getConnectStatus()) {
                    TrnsStatus = TrnsStatus_ConnError;
                    throw new Exception("Connect Server Error");
                }
                TC.doReceive();
                if (i == 1) {
                    TrnsStatus = TrnsStatus_Timeout;
                    throw new Exception("Receive Msg Timeout");
                }
            }

            txn4412_receive.Deconstruct();
            if ((rtnCheckRsp = txn4412.doCheckRspMsg(txn4412_receive)) != TxnMain.SUCCESS) {
                if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                    TrnsStatus = TrnsStatus_RespError;
                    insertTandemLog(txn4412_receive, null, null, null, TTandemLog.TandemType_G);
                    throw new Exception("CheckRspMsg Error 4412");
                } else {
                    TrnsStatus = TrnsStatus_RespError;
                    throw new Exception("CheckRspMsg Error 4412");
                }
            }

            if (txn4412_receive.getAcqRespCode_R().equals(TxnMain.AcqRespCode_R_ResetSeq)) {
                insertTandemLog(txn4412_receive, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_D);
                if (!doTmlSeqReset()) {
                    LOG.error("Reset Tml MsgSeqNo 發生錯誤！");
                }
                TrnsStatus = TrnsStatus_MsgnoReset;
                throw new Exception("RspCode = 17210 need to Reset MsgSeqNo");
            }

            insertTandemLog(txn4412_receive, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_D);
            LOG.debug("Update MsgSeqNo -->" + txn4412_receive.getMsgSeqNo_R());
            // updateTmlSeqNo(TmlID, txn4412_receive.getMsgSeqNo_R());
            // ------------------4412--------------------------

            // System.out.println(txn4412_receive.getAcqRespCode_R());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            LOG.error(e.getMessage());
            // 交易序號小於BAFES預期值，雖然有Exception 不過還是要Return Receive Data..
            if (TrnsStatus.equals(TrnsStatus_MsgnoReset)) {
                return txn4412_receive;
            }
            return null;
        }
        return txn4412_receive;
    }

    /**
     * Send Transaction to BAFES 1.Txn4404
     **/
    public TxnP4404 doTxnP4404(TxnP4404 txn4404) {
        int rtnCheckRsp;
        int iSleep = 200;
        TrnsStatus = TrnsStatus_Success;
        TxnP4404 txn4404_receive = null;
        try {
            LOG.debug("==========Run 4404-Payment/External Transfer-Transfer to Account in Other Banks/Bill Payment==========");
            // ------------------4404--------------------------
            if (!TC.getConnectStatus()) {
                TrnsStatus = TrnsStatus_ConnError;
                throw new Exception("Connect Server Error");
            }
            if (TC.isStopTrns()) {
                TrnsStatus = TrnsStatus_StopTrns;
                throw new Exception("StopTrns only for handshaking");
            }
            // MsgSeqNo = getTml_SeqNo(TmlID);
            // TxnP4404 txn4404 = new TxnP4404(TmlID, MsgSeqNo);
            txn4404.ConstructBBMsg();
            insertTandemLog(txn4404, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_U);
            hmSend.put(txn4404.getTmlID() + "-" + txn4404.getMsgSeqNo() + "-4404", txn4404);

            Thread.sleep(iSleep);
            TC.doSend();

            String receive_key = txn4404.getTmlID() + "-" + txn4404.getMsgSeqNo() + "-4404";
            for (int i = iRetryTimer; i > 0; i--) {
                Thread.sleep(iWaitSleep);
                txn4404_receive = (TxnP4404) hmReceive.get(receive_key);
                LOG.debug("Waitting Counter :" + i + "-" + txn4404.getTmlID() + "-" + txn4404.getMsgSeqNo());
                if (txn4404_receive != null) {
                    hmReceive.remove(receive_key);
                    break;
                }
                if (!TC.getConnectStatus()) {
                    TrnsStatus = TrnsStatus_ConnError;
                    throw new Exception("Connect Server Error");
                }
                TC.doReceive();
                if (i == 1) {
                    TrnsStatus = TrnsStatus_Timeout;
                    throw new Exception("Receive Msg Timeout");
                }
            }

            txn4404_receive.Deconstruct();
            if ((rtnCheckRsp = txn4404.doCheckRspMsg(txn4404_receive)) != TxnMain.SUCCESS) {
                if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                    TrnsStatus = TrnsStatus_RespError;
                    insertTandemLog(txn4404_receive, null, null, null, TTandemLog.TandemType_G);
                    throw new Exception("CheckRspMsg Error 4404");
                } else {
                    TrnsStatus = TrnsStatus_RespError;
                    throw new Exception("CheckRspMsg Error 4404");
                }
            }

            if (txn4404_receive.getAcqRespCode_R().equals(TxnMain.AcqRespCode_R_ResetSeq)) {
                insertTandemLog(txn4404_receive, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_D);
                if (!doTmlSeqReset()) {
                    LOG.error("Reset Tml MsgSeqNo 發生錯誤！");
                }
                TrnsStatus = TrnsStatus_MsgnoReset;
                throw new Exception("RspCode = 17210 need to Reset MsgSeqNo");
            }

            insertTandemLog(txn4404_receive, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_D);
            LOG.debug("Update MsgSeqNo -->" + txn4404_receive.getMsgSeqNo_R());
            // updateTmlSeqNo(TmlID, txn4404_receive.getMsgSeqNo_R());
            // ------------------4404--------------------------

            // System.out.println(txn4404_receive.getAcqRespCode_R());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            LOG.error(e.getMessage());
            // 交易序號小於BAFES預期值，雖然有Exception 不過還是要Return Receive Data..
            if (TrnsStatus.equals(TrnsStatus_MsgnoReset)) {
                return txn4404_receive;
            }
            return null;
        }
        return txn4404_receive;
    }

    /**
     * Send Transaction to BAFES 1.Txn4113
     **/
    public TxnP4113 doTxnP4113(TxnP4113 txn4113) {
        int rtnCheckRsp;
        int iSleep = 200;
        TrnsStatus = TrnsStatus_Success;
        TxnP4113 txn4113_receive = null;
        try {
            LOG.debug("==========Run 4113-PIN Change==========");
            // ------------------4113--------------------------
            if (!TC.getConnectStatus()) {
                TrnsStatus = TrnsStatus_ConnError;
                throw new Exception("Connect Server Error");
            }
            if (TC.isStopTrns()) {
                TrnsStatus = TrnsStatus_StopTrns;
                throw new Exception("StopTrns only for handshaking");
            }
            // MsgSeqNo = getTml_SeqNo(TmlID);
            // TxnP4113 txn4113 = new TxnP4113(TmlID, MsgSeqNo);
            txn4113.ConstructBBMsg();
            insertTandemLog(txn4113, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_U);
            hmSend.put(txn4113.getTmlID() + "-" + txn4113.getMsgSeqNo() + "-4113", txn4113);

            Thread.sleep(iSleep);
            TC.doSend();

            String receive_key = txn4113.getTmlID() + "-" + txn4113.getMsgSeqNo() + "-4113";
            for (int i = iRetryTimer; i > 0; i--) {
                Thread.sleep(iWaitSleep);
                txn4113_receive = (TxnP4113) hmReceive.get(receive_key);
                LOG.debug("Waitting Counter :" + i + "-" + txn4113.getTmlID() + "-" + txn4113.getMsgSeqNo());
                if (txn4113_receive != null) {
                    hmReceive.remove(receive_key);
                    break;
                }
                if (!TC.getConnectStatus()) {
                    TrnsStatus = TrnsStatus_ConnError;
                    throw new Exception("Connect Server Error");
                }
                TC.doReceive();
                if (i == 1) {
                    TrnsStatus = TrnsStatus_Timeout;
                    throw new Exception("Receive Msg Timeout");
                }
            }

            txn4113_receive.Deconstruct();
            if ((rtnCheckRsp = txn4113.doCheckRspMsg(txn4113_receive)) != TxnMain.SUCCESS) {
                if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                    TrnsStatus = TrnsStatus_RespError;
                    insertTandemLog(txn4113_receive, null, null, null, TTandemLog.TandemType_G);
                    throw new Exception("CheckRspMsg Error 4113");
                } else {
                    TrnsStatus = TrnsStatus_RespError;
                    throw new Exception("CheckRspMsg Error 4113");
                }
            }

            if (txn4113_receive.getAcqRespCode_R().equals(TxnMain.AcqRespCode_R_ResetSeq)) {
                insertTandemLog(txn4113_receive, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_D);
                if (!doTmlSeqReset()) {
                    LOG.error("Reset Tml MsgSeqNo 發生錯誤！");
                }
                TrnsStatus = TrnsStatus_MsgnoReset;
                throw new Exception("RspCode = 17210 need to Reset MsgSeqNo");
            }

            insertTandemLog(txn4113_receive, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_D);
            LOG.debug("Update MsgSeqNo -->" + txn4113_receive.getMsgSeqNo_R());
            // updateTmlSeqNo(TmlID, txn4113_receive.getMsgSeqNo_R());
            // ------------------4113--------------------------

            // System.out.println(txn4113_receive.getAcqRespCode_R());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            LOG.error(e.getMessage());
            // 交易序號小於BAFES預期值，雖然有Exception 不過還是要Return Receive Data..
            if (TrnsStatus.equals(TrnsStatus_MsgnoReset)) {
                return txn4113_receive;
            }
            return null;
        }
        return txn4113_receive;
    }

    /**
     * Send Transaction to BAFES 1.Txn4118
     **/
    public TxnP4118 doTxnP4118(TxnP4118 txn4118) {
        int rtnCheckRsp;
        int iSleep = 200;
        TrnsStatus = TrnsStatus_Success;
        TxnP4118 txn4118_receive = null;
        try {
            LOG.debug("==========Run 4118-PIN Change Reversal==========");
            // ------------------4118--------------------------
            if (!TC.getConnectStatus()) {
                TrnsStatus = TrnsStatus_ConnError;
                throw new Exception("Connect Server Error");
            }
            if (TC.isStopTrns()) {
                TrnsStatus = TrnsStatus_StopTrns;
                throw new Exception("StopTrns only for handshaking");
            }
            // MsgSeqNo = getTml_SeqNo(TmlID);
            // TxnP4118 txn4118 = new TxnP4118(TmlID, MsgSeqNo);
            txn4118.ConstructBBMsg();
            insertTandemLog(txn4118, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_U);
            hmSend.put(txn4118.getTmlID() + "-" + txn4118.getMsgSeqNo() + "-4118", txn4118);

            Thread.sleep(iSleep);
            TC.doSend();

            String receive_key = txn4118.getTmlID() + "-" + txn4118.getMsgSeqNo() + "-4118";
            for (int i = iRetryTimer; i > 0; i--) {
                Thread.sleep(iWaitSleep);
                txn4118_receive = (TxnP4118) hmReceive.get(receive_key);
                LOG.debug("Waitting Counter :" + i + "-" + txn4118.getTmlID() + "-" + txn4118.getMsgSeqNo());
                if (txn4118_receive != null) {
                    hmReceive.remove(receive_key);
                    break;
                }
                if (!TC.getConnectStatus()) {
                    TrnsStatus = TrnsStatus_ConnError;
                    throw new Exception("Connect Server Error");
                }
                TC.doReceive();
                if (i == 1) {
                    TrnsStatus = TrnsStatus_Timeout;
                    throw new Exception("Receive Msg Timeout");
                }
            }

            txn4118_receive.Deconstruct();
            if ((rtnCheckRsp = txn4118.doCheckRspMsg(txn4118_receive)) != TxnMain.SUCCESS) {
                if (rtnCheckRsp == TxnMain.ERR_AllowTandemLog) {
                    TrnsStatus = TrnsStatus_RespError;
                    insertTandemLog(txn4118_receive, null, null, null, TTandemLog.TandemType_G);
                    throw new Exception("CheckRspMsg Error 4118");
                } else {
                    TrnsStatus = TrnsStatus_RespError;
                    throw new Exception("CheckRspMsg Error 4118");
                }
            }

            if (txn4118_receive.getAcqRespCode_R().equals(TxnMain.AcqRespCode_R_ResetSeq)) {
                insertTandemLog(txn4118_receive, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_D);
                if (!doTmlSeqReset()) {
                    LOG.error("Reset Tml MsgSeqNo 發生錯誤！");
                }
                TrnsStatus = TrnsStatus_MsgnoReset;
                throw new Exception("RspCode = 17210 need to Reset MsgSeqNo");
            }

            insertTandemLog(txn4118_receive, IssuerBankCode, IssuerAccount, PCode, TTandemLog.TandemType_D);
            LOG.debug("Update MsgSeqNo -->" + txn4118_receive.getMsgSeqNo_R());
            // updateTmlSeqNo(TmlID, txn4118_receive.getMsgSeqNo_R());
            // ------------------4118--------------------------

            // System.out.println(txn4118_receive.getAcqRespCode_R());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            LOG.error(e.getMessage());
            // 交易序號小於BAFES預期值，雖然有Exception 不過還是要Return Receive Data..
            if (TrnsStatus.equals(TrnsStatus_MsgnoReset)) {
                return txn4118_receive;
            }
            return null;
        }
        return txn4118_receive;
    }

    /**
     * 更新TmlSeqno資料
     * <p>
     * 使用 SP_TmlInfo_upd_msgseqno
     * 
     * @param sTmlID
     * @param sMsgSeqNo
     */
    private final boolean updateTmlSeqNo(String sTmlID, String sMsgSeqNo) {
        TTmlInfoMgr TmlInfoMgr;
        HashMap hm = null;
        LOG.info("method: - Start updateTmlSeqNo(" + sTmlID + ")");
        sMsgSeqNo = (sMsgSeqNo.equals("")) ? "0" : sMsgSeqNo;

        hm = new HashMap();
        hm.put("_ServerID", sysProp.getProperty("systemidno"));
        hm.put("_TmlID", sTmlID);
        hm.put("MsgSeqNo", String.valueOf(Integer.parseInt(sMsgSeqNo) + 1));
        // hm.put("MsgSeqNo",sMsgSeqNo.equals("0") ? "0" :String.valueOf(Integer.parseInt(sMsgSeqNo)+1));
        try {
            TmlInfoMgr = (TTmlInfoMgr) Naming.lookup("rmi://" + APSystem.getRmiSrvName() + ":" + APSystem.getRmiSrvPort() + "/TTmlInfo");
            int iRtn = TmlInfoMgr.update_SP_TmlInfo(SP_Cmd_01, hm);
            if (iRtn == 0) {
                return false;
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("updateTmlSeqNo: 存取遠端 RMI 錯誤！");
            return false;
        } catch (Exception ex) {
            LOG.error("updateTmlSeqNo: Other Exception:" + ex.getMessage());
            return false;
        } // try 1
        return true;
    }

    /**
     * 新增交易資料到TandemLog
     * <p>
     * 使用 SP_TandemLog_ins
     * 
     * @param TxnMain
     *            txn
     * @param IssuerBankCode
     * @param IssuerAccount
     * @param PCode
     * @param TandemType
     */
    public static final boolean insertTandemLog(TxnMain txn, String IssuerBankCode, String IssuerAccount, String PCode, String TandemType) {
        TTandemLogMgr TandemLogMgr;
        ArrayList alField = new ArrayList();

        LOG.info("method: - Start doInsertTandemLog()");
        try {
            String txnDateTime = Misc.genDate(Misc.DT_DATETIME);

            TandemLogMgr = (TTandemLogMgr) Naming.lookup("rmi://" + APSystem.getRmiSrvName() + ":" + APSystem.getRmiSrvPort() + "/TTandemLog");
            alField.add(txnDateTime.subSequence(0, 8));
            alField.add(txn.getTmlID());
            // equal 4294967295 = oxFFFFFFFF ,mean run 31077
            alField.add((txn.getMsgSeqNo().equals("4294967295")) ? "-1" : txn.getMsgSeqNo());
            alField.add(String.valueOf(txn.getMsgFunCode()));
            alField.add((IssuerBankCode == null) ? "" : IssuerBankCode);
            alField.add((IssuerAccount == null) ? "" : IssuerAccount);
            alField.add((PCode == null) ? "" : PCode);
            alField.add(txnDateTime);
            alField.add(TandemType);
            if (TandemType.equals(TTandemLog.TandemType_U))
                alField.add(txn.getSendMSG_HEX());
            else
                alField.add(txn.getReceiveMSG_HEX());
            int iRtn = TandemLogMgr.insert_SP_TandemLog(SP_Cmd_02, alField);
            if (iRtn == 0) {
                LOG.warn("doInsertTandemLog: SP_TandemLog_ins error happened");
                return false;
            }
            return true;
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("doInsertTandemLog: 存取遠端 RMI 錯誤！");
            return false;
        } catch (Exception ex) {
            LOG.error("doInsertTandemLog: Other Exception:" + ex.getMessage());
            return false;
        } // try 1
    }

    /**
     * 取得端末交易序號資料
     * 
     * @param tmlID
     *            使用 SP_TmlInfo_sel_all
     * @return 回傳 TmlInfo 的 ArrayList 資料
     */
    public String getTml_SeqNo(String tmlID) {
        TTmlInfoMgr TmlInfoMgr;
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        alCondition.add(sysProp.getProperty("systemidno"));
        alCondition.add(tmlID);
        LOG.info("method: - Start getTml_SeqNo()");
        try {
            TmlInfoMgr = (TTmlInfoMgr) Naming.lookup("rmi://" + APSystem.getRmiSrvName() + ":" + APSystem.getRmiSrvPort() + "/TTmlInfo");
            alRtnData = TmlInfoMgr.getArrayList_SP_TmlInfo(SP_Cmd_03, alCondition);
            if (alRtnData.size() != 0) {
                TTmlInfo ti = (TTmlInfo) alRtnData.get(0);
                if (Integer.parseInt(ti.getMsgSeqNo()) >= iMaxTrnsMsgSeqNo) {
                    LOG.info("Tml MsgSeqNo = " + iMaxTrnsMsgSeqNo + " ,Must Run MSQ Reset");
                    // if (doTmlSeqReset()){
                    updateTmlSeqNo(tmlID, String.valueOf(iMinTrnsMsgSeqNo));
                    setMsgSeqNo(String.valueOf(iMinTrnsMsgSeqNo));
                    return String.valueOf(iMinTrnsMsgSeqNo);
                    /*
                     * }else{ LOG.error("Reset Tml MsgSeqNo 發生錯誤！"); return null; }
                     */
                }
                setMsgSeqNo(ti.getMsgSeqNo());
                return ti.getMsgSeqNo();
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("getTml_SeqNo: 存取遠端 RMI 錯誤！");
            return null;
        } catch (Exception ex) {
            LOG.error("getTml_SeqNo: Other Exception:" + ex.getMessage());
            return null;
        } // try 1
        return null;
    }

    /**
     * @param args
     *            Creation date:(2008/7/25 上午 10:04:33)
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Hashtable hmSend = new Hashtable();
        Hashtable hmReceive = new Hashtable();

        TcpClient tc = new TcpClient("172.18.92.35", 9000, hmSend, hmReceive);
        tc.starClientThread();
        TxnTerminal txnTml = new TxnTerminal("8614", tc, hmSend, hmReceive);
        // txnTml.doTxnP4501();
        // txnTml.initHandShaking();
        /*
         * try { Thread.sleep(10000); } catch (Exception e) { // TODO: handle exception }
         */
        txnTml.doKeyExchange();
        /*
         * try { Thread.sleep(10000); } catch (Exception e) { // TODO: handle exception }
         */
        // txnTml.doTmlSeqReset();

    }
}
