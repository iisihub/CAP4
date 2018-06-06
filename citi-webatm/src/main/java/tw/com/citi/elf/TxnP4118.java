/*
 * @(#)TxnP4118.java
 *
 * Copyright (c) 2008 Citi Group Incorporated. All rights reserved.
 *
 * Modify History:
 *  v1.00, 2008/07/07, Kevin Chung
 *   1) First release
 *
 */
package tw.com.citi.elf;

import static com.iisigroup.colabase.webatm.common.CCConstants.InterCitiPinChangeReversal_MsgCode;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iisigroup.colabase.webatm.toolkit.Misc;

/**
 * implementation 4118... to build ByteBuffer for RequestData and deconstruct ResponseData from TcpClient include main function...ConstructBBMsg, Deconstruct
 */
public class TxnP4118 extends TxnMain {
    // log4J category
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final String sAuthIndicator_ID = Integer.toHexString(18965); // "18965"; //���w�q TxnP4501

    private static final String GatewayHeader_FunCode = "00"; // request Function Code(B1)
    private static final String GatewayHeader = GatewayHeader_FunCode + "0000"; // request
    // private static final String GatewayHeader_R1 = GatewayHeader_FunCode+"1000"; //normal response
    // private static final String GatewayHeader_R2 = GatewayHeader_FunCode+"1200"; //error response
    private static final String MsgFunCode = InterCitiPinChangeReversal_MsgCode;
    private String TmlID = null;
    private String MsgSeqNo = null;
    private static final String RetryFlag = new String(Misc.bin2Hex("0".getBytes())); // "0";
    private static final String AuthTknType1 = new String(Misc.bin2Hex("03".getBytes())); // "03";
    private String AuthTkn1 = null;
    private static final String AuthTknType2 = new String(Misc.bin2Hex("20".getBytes())); // "20";
    private String AuthTkn2 = null;
    private static final String AuthTknType3 = new String(Misc.bin2Hex("06".getBytes())); // "06";
    private String AuthTkn3 = null;
    private String CATCardCC = "01"; // 01=Citicard, 27=any other bank card
    private String StartSessDT = null; // YYYYMMDDhhmmss
    private String NewPinBlk = null;
    private String OrigMsgSeqNo = null; // Original Message Sequence Number
    private String RevReasonCode = null; // Reversal Reason Code

    // ReceiveMsg From BAFES
    private String MsgFunCode_R = null;
    private String TmlID_R = null;
    private String MsgSeqNo_R = null;
    private String RetryFlag_R = null;
    private String AcqRespCode_R = null;
    private String ActionCode_R = null;

    public TxnP4118(String TerminalID, String msgSeq) {
        TmlID = Misc.padZero(Integer.toHexString(Integer.parseInt(TerminalID)), 4);
        TmlID_Decimal8Digit = TerminalID;
        MsgSeqNo = Misc.padZero(Long.toHexString(Long.parseLong(msgSeq)), 8);
        super.LOG = LOG;
    }

    /**
     * @return Creation date:(2008/7/9 �U�� 03:50:47)
     */
    public ByteBuffer ConstructBBMsg() {
        int iKeepPoint1 = 0; // for ELF Element Keep Position Point
        int iKeepPoint2 = 0; // for Text Element Keep Position Point
        int iKeepPoint3 = 0; // for MsgID Element Keep Position Point
        int iKeepPoint4 = 0; // for normal Element Keep Position Point
        int iTempLen1 = 0; // for ELF Element Count Len
        int iTempLen2 = 0; // for Text Element Count Len
        int iTempLen3 = 0; // for MsgID Element Count Len
        int iTempLen4 = 0; // for normal Element Count Len
        int iElementLen = 0;

        StringBuffer sbSendMsg = new StringBuffer();

        if (!doCheckOrgData()) {
            return null;
        }

        // -------Start to construct-------
        sbSendMsg.append(sElfElement_ID);
        iKeepPoint1 = sbSendMsg.length();

        iElementLen = 4 + 15;
        sbSendMsg.append(sGatewayHeader_ID);
        sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
        sbSendMsg.append(GatewayHeader).append(NodeID).append(TmlID).append(MsgSeqNo);
        iTempLen1 += iElementLen;
        {
            iElementLen = 4;
            sbSendMsg.append(sTextElement_ID);
            iKeepPoint2 = sbSendMsg.length();
            iTempLen1 += iElementLen;

            iElementLen = 4;
            sbSendMsg.append(MsgFunCode);
            iKeepPoint3 = sbSendMsg.length();
            iTempLen1 += iElementLen;
            iTempLen2 += iElementLen;
            {
                iElementLen = 4 + 2;
                sbSendMsg.append(sMsgFunCode_ID);
                sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                sbSendMsg.append(MsgFunCode);
                iTempLen1 += iElementLen;
                iTempLen2 += iElementLen;
                iTempLen3 += iElementLen;

                iElementLen = 4 + 2;
                sbSendMsg.append(sTmlID_ID);
                sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                sbSendMsg.append(TmlID);
                iTempLen1 += iElementLen;
                iTempLen2 += iElementLen;
                iTempLen3 += iElementLen;

                iElementLen = 4 + 4;
                sbSendMsg.append(sMsgSeqNo_ID);
                sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                sbSendMsg.append(MsgSeqNo);
                iTempLen1 += iElementLen;
                iTempLen2 += iElementLen;
                iTempLen3 += iElementLen;

                iElementLen = 4 + 1;
                sbSendMsg.append(sRetryFlag_ID);
                sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                sbSendMsg.append(RetryFlag);
                iTempLen1 += iElementLen;
                iTempLen2 += iElementLen;
                iTempLen3 += iElementLen;

                iElementLen = 4;
                sbSendMsg.append(sAuthTknTable_S_ID);
                iKeepPoint4 = sbSendMsg.length();
                iTempLen1 += iElementLen;
                iTempLen2 += iElementLen;
                iTempLen3 += iElementLen;
                {
                    iElementLen = 4 + 2;
                    sbSendMsg.append(sAuthTknType_S_ID);
                    sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                    sbSendMsg.append(AuthTknType1);
                    iTempLen1 += iElementLen;
                    iTempLen2 += iElementLen;
                    iTempLen3 += iElementLen;
                    iTempLen4 += iElementLen;

                    // AuthTkn1 = "<19=406<4471<>?7";
                    iElementLen = 4 + (AuthTkn1.length() / 2);
                    sbSendMsg.append(sAuthTkn_S_ID);
                    sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                    // sbSendMsg.append(new String(Misc.bin2Hex(AuthTkn1.getBytes())));
                    sbSendMsg.append(AuthTkn1);
                    iTempLen1 += iElementLen;
                    iTempLen2 += iElementLen;
                    iTempLen3 += iElementLen;
                    iTempLen4 += iElementLen;
                }
                sbSendMsg.insert(iKeepPoint4, Misc.padZero(Integer.toHexString(4 + iTempLen4), iPadZeroLen));
                iTempLen4 = 0;

                iElementLen = 4;
                sbSendMsg.append(sAuthTknTable_S_ID);
                iKeepPoint4 = sbSendMsg.length();
                iTempLen1 += iElementLen;
                iTempLen2 += iElementLen;
                iTempLen3 += iElementLen;
                {
                    iElementLen = 4 + 2;
                    sbSendMsg.append(sAuthTknType_S_ID);
                    sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                    sbSendMsg.append(AuthTknType2);
                    iTempLen1 += iElementLen;
                    iTempLen2 += iElementLen;
                    iTempLen3 += iElementLen;
                    iTempLen4 += iElementLen;

                    // AuthTkn2 = "303130303032353030363136343730313145313232313133373136313220";
                    iElementLen = 4 + (AuthTkn2.length() / 2);
                    sbSendMsg.append(sAuthTkn_S_ID);
                    sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                    // sbSendMsg.append(new String(Misc.bin2Hex(AuthTkn2.getBytes())));
                    sbSendMsg.append(AuthTkn2);
                    iTempLen1 += iElementLen;
                    iTempLen2 += iElementLen;
                    iTempLen3 += iElementLen;
                    iTempLen4 += iElementLen;
                }
                sbSendMsg.insert(iKeepPoint4, Misc.padZero(Integer.toHexString(4 + iTempLen4), iPadZeroLen));
                iTempLen4 = 0;
                /*
                 * iElementLen = 4; sbSendMsg.append(sAuthTknTable_S_ID); iKeepPoint4 = sbSendMsg.length(); iTempLen1 += iElementLen; iTempLen2 += iElementLen; iTempLen3 += iElementLen; { iElementLen
                 * = 4 + 2; sbSendMsg.append(sAuthTknType_S_ID); sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen),iPadZeroLen)); sbSendMsg.append(AuthTknType3); iTempLen1 += iElementLen;
                 * iTempLen2 += iElementLen; iTempLen3 += iElementLen; iTempLen4 += iElementLen;
                 * 
                 * //AuthTkn3 = "303130303032353030363136343730313145313232313133373136313220"; iElementLen = 4 + (AuthTkn3.length()/2); sbSendMsg.append(sAuthTkn_S_ID);
                 * sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen),iPadZeroLen)); //sbSendMsg.append(new String(Misc.bin2Hex(AuthTkn2.getBytes()))); sbSendMsg.append(AuthTkn3);
                 * iTempLen1 += iElementLen; iTempLen2 += iElementLen; iTempLen3 += iElementLen; iTempLen4 += iElementLen; } sbSendMsg.insert(iKeepPoint4, Misc.padZero(Integer.toHexString(4 +
                 * iTempLen4),iPadZeroLen)); iTempLen4 = 0;
                 */
                iElementLen = 4 + 2;
                sbSendMsg.append(sCATCardCC_S_ID);
                sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                sbSendMsg.append(new String(Misc.bin2Hex(CATCardCC.getBytes())));
                iTempLen1 += iElementLen;
                iTempLen2 += iElementLen;
                iTempLen3 += iElementLen;

                // StartSessDT = "20080707120000";
                iElementLen = 4 + 14;
                sbSendMsg.append(sStartSessDT_S_ID);
                sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                sbSendMsg.append(new String(Misc.bin2Hex(StartSessDT.getBytes())));
                iTempLen1 += iElementLen;
                iTempLen2 += iElementLen;
                iTempLen3 += iElementLen;

                // NewPinBlk = "25800000000000010000902002020000401234123412341234200809090101010090200202000040";
                iElementLen = 4 + (NewPinBlk.length() / 2);
                sbSendMsg.append(sNewPinBlk_S_ID);
                sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                sbSendMsg.append(NewPinBlk);
                iTempLen1 += iElementLen;
                iTempLen2 += iElementLen;
                iTempLen3 += iElementLen;

                // OrigMsgSeqNo = "0001";
                iElementLen = 4 + 4;
                sbSendMsg.append(sOrigMsgSeqNo_S_ID);
                sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                sbSendMsg.append(OrigMsgSeqNo);
                iTempLen1 += iElementLen;
                iTempLen2 += iElementLen;
                iTempLen3 += iElementLen;

                // RevReasonCode = "00";
                iElementLen = 4 + 2;
                sbSendMsg.append(sRevReasonCode_S_ID);
                sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                sbSendMsg.append(new String(Misc.bin2Hex(RevReasonCode.getBytes())));
                iTempLen1 += iElementLen;
                iTempLen2 += iElementLen;
                iTempLen3 += iElementLen;
            }
            sbSendMsg.insert(iKeepPoint3, Misc.padZero(Integer.toHexString(4 + iTempLen3), iPadZeroLen));
            iTempLen3 = 0;

            sbSendMsg.insert(iKeepPoint2, Misc.padZero(Integer.toHexString(4 + iTempLen2), iPadZeroLen));
            iTempLen2 = 0;
        }
        sbSendMsg.insert(iKeepPoint1, Misc.padZero(Integer.toHexString(4 + iTempLen1), iPadZeroLen));
        iTempLen1 = 0;

        // System.out.println(sbSendMsg.toString().toUpperCase());
        // -------End of construct-------

        sbSendMsg = addTcpMsgFormatCode(sbSendMsg);

        String sTemp = sbSendMsg.toString().toUpperCase();
        LOG.debug(sTemp);

        ByteBuffer input = (ByteBuffer) ByteBuffer.allocate(sbSendMsg.length() / 2);
        input.put(Misc.hex2Bin(sTemp.getBytes()));
        setSendMSG(input);
        setSendMSG_HEX(sTemp);
        return input;
    }

    /**
     * Creation date:(2008/7/9 �U�� 04:12:24)
     */
    public void Deconstruct() {
        String msg = new String(Misc.bin2Hex(ReceiveMSG.array()));
        setReceiveMSG_HEX(msg);
        int iPoint = 0;
        int key = 0;
        int msgLen = msg.length();
        HashMap hmData = new HashMap(); // for data return
        while (msgLen > iPoint) {
            key = Integer.parseInt(msg.substring(iPoint, iPoint + iELF_Len), 16);
            LOG.debug("key - " + key);
            iPoint += 4;
            switch (key) {
            case 1:
                ElfElement_LEN = getELFLen(iPoint, msg);
                iPoint += 4;
                break;

            case 5:
                TextElement_LEN = getELFLen(iPoint, msg);
                iPoint += 4;
                break;

            case 13:
                iPoint = getELFData(iPoint, msg, hmData);
                GatewayHeader_R = (String) hmData.get(HMDataKEY);
                GatewayHeader_FunCode_R = GatewayHeader_R.substring(0, 2);
                GatewayHeader_Flags_R = GatewayHeader_R.substring(2, 4);
                GatewayHeader_SysRespCode_R = GatewayHeader_R.substring(4, 6);
                GatewayHeader_NodeID_R = GatewayHeader_R.substring(6, 18);
                break;
            case 4118:
                getELFLen(iPoint, msg);
                iPoint += 4;
                break;

            case 7920:
                iPoint = getELFData(iPoint, msg, hmData);
                MsgFunCode_R = (String) hmData.get(HMDataKEY);
                break;

            case 8002:
                iPoint = getELFData(iPoint, msg, hmData);
                TmlID_R = (String) hmData.get(HMDataKEY);
                break;

            case 8005:
                iPoint = getELFData(iPoint, msg, hmData);
                MsgSeqNo_R = (String) hmData.get(HMDataKEY);
                break;

            case 8001:
                iPoint = getELFData(iPoint, msg, hmData);
                RetryFlag_R = (String) hmData.get(HMDataKEY);
                break;

            case 7300:
                iPoint = getELFData(iPoint, msg, hmData);
                AcqRespCode_R = (String) hmData.get(HMDataKEY);
                break;

            case 7302:
                iPoint = getELFData(iPoint, msg, hmData);
                ActionCode_R = (String) hmData.get(HMDataKEY);
                break;

            default:
                iPoint = getELFData(iPoint, msg, hmData);
                // hmData.get(HMDataKEY);
                break;
            }
            key = 0;
            hmData.put(HMDataKEY, "");
        }
        hmData = null;
        return;
    }

    /**
     * @return Creation date:(2008/7/9 �U�� 04:12:35)
     */
    public int doCheckRspMsg() {
        return ERROR;
    }

    /**
     * �ˮ֦^�Ъ����
     *
     * @return Creation date:(2008/7/9 �U�� 04:12:35)
     */
    public int doCheckRspMsg(TxnMain txnMain) {
        TxnP4118 txn = (TxnP4118) txnMain;
        int RtnResult = SUCCESS;
        try {
            if (!doCheckRespHeader(txn)) {
                RtnResult = ERROR;
                throw new Exception("CheckRespHeader Error");
            }
            // Check Message Sequence Number
            if (!txn.getMsgSeqNo_R().equals(this.getMsgSeqNo())) {
                RtnResult = ERROR;
                throw new Exception("Check Resp MsgSeqNo " + txn.getMsgSeqNo_R() + " != " + getMsgSeqNo());
            }
            // Check Acquirer Response Code
            if (!CheckNumeric(txn.getAcqRespCode_R(), 5)) {
                RtnResult = ERR_AllowTandemLog;
                throw new Exception("Check Acquirer Response Code Error = " + txn.getAcqRespCode_R());
            }
            // Check Action Code
            if (!CheckNumeric(txn.getActionCode_R())) {
                RtnResult = ERR_AllowTandemLog;
                throw new Exception("Check Action Code Error = " + txn.getActionCode_R());
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return RtnResult;
        }
        return RtnResult;
    }

    /**
     * �ˮֳ]�w���ݲզ��q�媺���
     */
    protected boolean doCheckOrgData() {
        return true;
    }

    /***
     * @return MsgFunCode as Integer
     */
    public int getMsgFunCode() {
        return Integer.parseInt(MsgFunCode, 16);
    }

    /**
     * @return Returns the msgSeqNo.
     */
    public String getMsgSeqNo() {
        return String.valueOf(Long.parseLong(MsgSeqNo, 16));
    }

    /**
     * @return Returns the tmlID.
     */
    public String getTmlID() {
        return TmlID_Decimal8Digit;
        // return String.valueOf(Integer.parseInt(TmlID,16));
    }

    public String getGatewayHeader_FunCode() {
        return GatewayHeader_FunCode;
    }

    /**
     * @param authTkn1
     *            The authTkn1 to set.
     */
    public void setAuthTkn1(String authTkn1) {
        AuthTkn1 = authTkn1;
    }

    /**
     * @param authTkn2
     *            The authTkn2 to set.
     */
    public void setAuthTkn2(String authTkn2) {
        AuthTkn2 = authTkn2;
    }

    /**
     * @param authTkn3
     *            The authTkn2 to set.
     */
    public void setAuthTkn3(String authTkn3) {
        AuthTkn3 = authTkn3;
    }

    /**
     * @param pinPinBlk
     *            The chipInputBlk to set.
     */
    public void setNewPinBlk(String newPinBlk) {
        NewPinBlk = newPinBlk;
    }

    /**
     * @param startSessDT
     *            The startSessDT to set.
     */
    public void setStartSessDT(String startSessDT) {
        StartSessDT = startSessDT;
    }

    /**
     * @param origMsgSeqNo
     *            The origMsgSeqNo to set.
     */
    public void setOrigMsgSeqNo(String origMsgSeqNo) {
        OrigMsgSeqNo = origMsgSeqNo;
    }

    /**
     * @param revReasonCode
     *            The revReasonCode to set.
     */
    public void setRevReasonCode(String revReasonCode) {
        RevReasonCode = revReasonCode;
    }

    /**
     * @return Returns the acqRespCode_R.
     */
    public String getAcqRespCode_R() {
        return new String(Misc.hex2Bin(AcqRespCode_R.getBytes()));
    }

    /**
     * @return Returns the actionCode_R.
     */
    public String getActionCode_R() {
        return new String(Misc.hex2Bin(ActionCode_R.getBytes()));
    }

    /**
     * @return Returns the msgFunCode_R.
     */
    public String getMsgFunCode_R() {
        return MsgFunCode_R;
    }

    /**
     * @return Returns the msgSeqNo_R.
     */
    public String getMsgSeqNo_R() {
        return String.valueOf(Integer.parseInt(MsgSeqNo_R, 16));
    }

    /**
     * @return Returns the retryFlag_R.
     */
    public String getRetryFlag_R() {
        return RetryFlag_R;
    }

    /**
     * @return Returns the tmlID_R.
     */
    public String getTmlID_R() {
        return TmlID_R;
    }

    /**
     * @param args
     *            Creation date:(2008/7/9 �U�� 03:26:37)
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        TxnP4118 txn = new TxnP4118("8614", "00000001");
        txn.ConstructBBMsg();

        String receiveMsg = "00010048000D001364000030303030303121A60000000100050031119500671EF0000611951F42000621A61F450008000000011F410005301C84000930303030301C860007303030";
        ByteBuffer input = (ByteBuffer) ByteBuffer.allocate(receiveMsg.length() / 2);
        input.put(Misc.hex2Bin(receiveMsg.getBytes()));
        txn.setReceiveMSG(input);
        txn.Deconstruct();

        System.out.println(txn.getActionCode_R());
    }

}
