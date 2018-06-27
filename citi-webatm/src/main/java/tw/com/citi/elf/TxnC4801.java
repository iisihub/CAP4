/*
 * @(#)TxnC4801.java
 *
 * Copyright (c) 2008 Citi Group Incorporated. All rights reserved.
 *
 * Modify History:
 *  v1.00, 2008/07/07, Kevin Chung
 *   1) First release
 *
 */
package tw.com.citi.elf;

import static com.iisigroup.colabase.webatm.common.CCConstants.HostEncryptionKeyLoad_MsgCode;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iisigroup.colabase.webatm.toolkit.Misc;

/**
 * implementation 4801... to build ByteBuffer for RequestData and deconstruct ResponseData from TcpClient include main function...ConstructBBMsg, Deconstruct
 */
public class TxnC4801 extends TxnMain {
    // log4J category
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    // R--> ID for ReceiveMsg From BAFES
    // private static final String sEkeyData_R_ID = "7902"; //TxnC4801
    private static final String sEkeyModifier_R_ID = "7901"; // TxnC4801
    private static final String sEkeyDLenData_R_ID = "10103"; // TxnC4801
    // private static final String sEncryTimer_R_ID= "7933"; //TxnC4801

    // S--> ID for SendMsg From BAFES
    private static final String sKeyChkDigits_S_ID = Integer.toHexString(7904);// "7904"; //TxnC4801

    private static final String GatewayHeader_FunCode = "00"; // request Function Code(B1)
    private static final String GatewayHeader = GatewayHeader_FunCode + "0000"; // request
    private static final String GatewayHeader_S1 = GatewayHeader_FunCode + "1000"; // normal response to BAFES
    private static final String GatewayHeader_S2 = GatewayHeader_FunCode + "1200"; // error response to BAFES
    private static final String MsgFunCode = HostEncryptionKeyLoad_MsgCode;
    private String TmlID = null;
    private String MsgSeqNo = null;
    private String RetryFlag = new String(Misc.bin2Hex("0".getBytes())); // "0";
    private String RespCode = "00"; // 00=Normal CAT response code, 09=Command rejected by the CAT
    private String KeyChkDigits = "0000"; // �� 00000000�@3DES�[�K�A���e4�X

    // ReceiveMsg From BAFES
    private String MsgFunCode_R = null;
    private String TmlID_R = null;
    private String MsgSeqNo_R = null;
    private String RetryFlag_R = null; // must be 0
    // private String EkeyData_R = null;
    private String EkeyModifier_R = null;
    private String EkeyDLenData_R = null;
    private String KEYGeneration_R = null;
    // private String EncryCounter_R = null;
    // private String EncryTimer_R = null;

    public TxnC4801(String TerminalID, String msgSeq) {
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
        int iKeepPoint3 = 0; // for normal Element Keep Position Point
        int iTempLen1 = 0; // for ELF Element Count Len
        int iTempLen2 = 0; // for Text Element Count Len
        int iTempLen3 = 0; // for normal Element Count Len
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
        sbSendMsg.append(GatewayHeader_S1).append(NodeID).append(TmlID).append(MsgSeqNo);
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

                iElementLen = 4 + 2;
                sbSendMsg.append(sRespCode_S_ID);
                sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                sbSendMsg.append(new String(Misc.bin2Hex(RespCode.getBytes())));
                iTempLen1 += iElementLen;
                iTempLen2 += iElementLen;
                iTempLen3 += iElementLen;

                iElementLen = 4 + 4;
                if (KeyChkDigits == null || KeyChkDigits.equals(""))
                    KeyChkDigits = "----";
                // KeyChkDigits = "----";
                sbSendMsg.append(sKeyChkDigits_S_ID);
                sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
                sbSendMsg.append(new String(Misc.bin2Hex(KeyChkDigits.getBytes())));
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

            case 4801:
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

            case 7901:
                iPoint = getELFData(iPoint, msg, hmData);
                EkeyModifier_R = (String) hmData.get(HMDataKEY);
                break;

            case 10103:
                iPoint = getELFData(iPoint, msg, hmData);
                EkeyDLenData_R = (String) hmData.get(HMDataKEY);
                break;

            case 15004:
                iPoint = getELFData(iPoint, msg, hmData);
                KEYGeneration_R = (String) hmData.get(HMDataKEY);
                break;

            default:
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
        return doCheckRspMsg(this);
    }

    /**
     * @return Creation date:(2008/7/9 �U�� 04:12:35)
     */
    public int doCheckRspMsg(TxnMain txnMain) {
        TxnC4801 txn = (TxnC4801) txnMain;
        int RtnResult = SUCCESS;
        try {
            if (!doCheckReqHeader(txn)) {
                RtnResult = ERROR;
                throw new Exception("CheckRespHeader Error");
            }
            // Check EKEY Modifier
            String ekey = txn.getEkeyModifier_R();
            if (!CheckNumeric(ekey, 2)) {
                RtnResult = ERR_AllowTandemLog;
                throw new Exception("Check EKEY Modifier Error = " + ekey);
            }
            if (!ekey.equals("03") && !ekey.equals("04")) {
                RtnResult = ERR_AllowTandemLog;
                throw new Exception("Check EKEY Modifier != 03 or 04 " + ekey);
            }

            // Check EKEY Double Length Data
            /*
             * if (!CheckNumeric(txn.getEkeyDLenData_R(), 3)) throw new Exception("Check EKEY Error = " + txn.getEkeyDLenData_R());
             */

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return RtnResult;
        }

        return RtnResult;
    }

    /**
     *
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
     * @param keyChkDigits
     *            The keyChkDigits to set.
     */
    public void setKeyChkDigits(String keyChkDigits) {
        KeyChkDigits = keyChkDigits;
    }

    /**
     * @return Returns the ekeyDLenData_R.
     */
    public String getEkeyDLenData_R() {
        return new String(Misc.hex2Bin(EkeyDLenData_R.getBytes()));
    }

    /**
     * @return Returns the ekeyModifier_R.
     */
    public String getEkeyModifier_R() {
        return new String(Misc.hex2Bin(EkeyModifier_R.getBytes()));
    }

    /**
     * @return Returns the kEYGeneration_R.
     */
    public String getKEYGeneration_R() {
        return new String(Misc.hex2Bin(KEYGeneration_R.getBytes()));
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
        return String.valueOf(Long.parseLong(MsgSeqNo_R, 16));
        // return MsgSeqNo_R;
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
        return String.valueOf(Integer.parseInt(TmlID_R, 16));
    }

    /**
     * @param args
     *            Creation date:(2008/7/9 �U�� 03:26:37)
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        TxnC4801 txn = new TxnC4801("8614", "1");
        String receiveMsg = "00010050000D001364000030303030303121A6000000010005003912C100351EF0000612C11F42000621A61F450008000000011F410005301EDD00063030277700123230303830373037313230303030";
        ByteBuffer input = (ByteBuffer) ByteBuffer.allocate(receiveMsg.length() / 2);
        input.put(Misc.hex2Bin(receiveMsg.getBytes()));
        txn.setReceiveMSG(input);
        txn.Deconstruct();
        System.out.println(txn.getEkeyModifier_R());
        System.out.println(txn.getEkeyDLenData_R());

        txn.ConstructBBMsg();
    }

}
