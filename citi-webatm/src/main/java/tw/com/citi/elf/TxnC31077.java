/*
 * @(#)TxnC31077.java
 *
 * Copyright (c) 2008 Citi Group Incorporated. All rights reserved.
 *
 * Modify History:
 *  v1.00, 2008/07/07, Kevin Chung
 *   1) First release
 *
 */
package tw.com.citi.elf;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.citi.utils.Misc;
import com.iisigroup.colabase.webatm.common.CCConstants;

/**
 * implementation 31077... to build ByteBuffer for RequestData and deconstruct ResponseData from TcpClient include main function...ConstructBBMsg, Deconstruct
 */
@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TxnC31077 extends TxnMain {
    // log4J category
    static Logger LOG = LoggerFactory.getLogger("TxnC31077");

    // R--> ID for ReceiveMsg From BAFES
    private static final String sTimestamp_R_ID = "8000"; // TxnC31077
    // private static final String sRespText_R_ID = "3992"; //TxnC31077

    private static final String GatewayHeader_FunCode = "00"; // request Function Code(B1)
    private static final String GatewayHeader = GatewayHeader_FunCode + "0000"; // request
    // private static final String GatewayHeader_R1 = GatewayHeader_FunCode+"1000"; //normal response
    // private static final String GatewayHeader_R2 = GatewayHeader_FunCode+"1200"; //error response
    private static final String MsgFunCode = CCConstants.GetHostTime_MsgCode;
    private static final String MaxMsgSeqNo = "FFFFFFFF";
    private String TmlID = null;
    private String MsgSeqNo = "FFFFFFFF";
    private static final String RetryFlag = new String(Misc.bin2Hex("0".getBytes())); // "0";

    // ReceiveMsg From BAFES
    private String MsgFunCode_R = null;
    private String TmlID_R = null;
    private String MsgSeqNo_R = null;
    private String RetryFlag_R = null;
    private String RespCode_R = null;
    private String Timestamp_R = null;
    private String RespText_R = null;

    public TxnC31077(String TerminalID, String msgSeq) {
        TmlID = Misc.padZero(Integer.toHexString(Integer.parseInt(TerminalID)), 4);
        TmlID_Decimal8Digit = TerminalID;
        MsgSeqNo = Misc.padZero(Long.toHexString(Long.parseLong(msgSeq)), 8);
        super.LOG = LOG;
    }

    /**
     * 此處因效能因素，所以不使用 recursion 方式。 笨笨的加比較快 直接定義3層 iKeepPoint(n) 來設定目前定位點 直接定義3層 iTempLen(n) 來設定目前各層長度 先append ID 再來是Lenght 再來是Data or Element 如果有空可以改成int array 的方式。擴充性比較佳 (31077這一個Method的註解比較詳細)，
     * 其餘ELF class請先參考這個class說明
     * 
     * @return Creation date:(2008/7/9 下午 03:50:47)
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
        sbSendMsg.append(sElfElement_ID); // set level 1 append Application Message Header ID 0x00001
        iKeepPoint1 = sbSendMsg.length(); // set level 1 Length Point

        iElementLen = 4 + 15; // set this ElementLen (Application Message Header) always be 4 + 15
        sbSendMsg.append(sGatewayHeader_ID); // append Application Message Header ID 0x00013
        sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen)); // append Application Message Header Length
        sbSendMsg.append(GatewayHeader).append(NodeID).append(TmlID).append(MsgSeqNo); // append Application Message Header Data
        iTempLen1 += iElementLen;
        {
            iElementLen = 4;
            sbSendMsg.append(sTextElement_ID); // set level 2 append Application Message Header ID 0x00005
            iKeepPoint2 = sbSendMsg.length(); // set level 2 Length Point
            iTempLen1 += iElementLen; // because level 1 include level 2 data so need add length again

            iElementLen = 4;
            sbSendMsg.append(MsgFunCode); // set level 3 append MsgFunCode ID
            iKeepPoint3 = sbSendMsg.length(); // set level 3 Length Point
            iTempLen1 += iElementLen; // because level 1 include level 3 data so need add length again
            iTempLen2 += iElementLen; // because level 2 include level 3 data so need add length again
            {
                iElementLen = 4 + 2;
                sbSendMsg.append(sMsgFunCode_ID); // append pure element's ID
                sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen)); // append pure element's Length
                sbSendMsg.append(MsgFunCode); // append pure element's Data
                iTempLen1 += iElementLen; // because level 1 include pure element data so need add length again
                iTempLen2 += iElementLen; // because level 2 include pure element data so need add length again
                iTempLen3 += iElementLen; // because level 3 include pure element data so need add length again

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
                // sbSendMsg.append(MsgSeqNo);
                sbSendMsg.append(MaxMsgSeqNo);
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
            }
            // 從level 3 之前assign的定位去加上後續新增的長度值
            sbSendMsg.insert(iKeepPoint3, Misc.padZero(Integer.toHexString(4 + iTempLen3), iPadZeroLen));
            iTempLen3 = 0;

            // 從level 2 之前assign的定位去加上後續新增的長度值
            sbSendMsg.insert(iKeepPoint2, Misc.padZero(Integer.toHexString(4 + iTempLen2), iPadZeroLen));
            iTempLen2 = 0;
        }
        // 從level 1 之前assign的定位去加上後續新增的長度值
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
     * By ID for key to extract data or length if element just has sub-element then use getELFLen to get sub-element Length or pure element use getELFData to get Data
     * 
     * Creation date:(2008/7/9 下午 04:12:24)
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

            case 31077:
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

            case 8007:
                iPoint = getELFData(iPoint, msg, hmData);
                RespCode_R = (String) hmData.get(HMDataKEY);
                break;

            case 8000:
                iPoint = getELFData(iPoint, msg, hmData);
                Timestamp_R = (String) hmData.get(HMDataKEY);
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
     * 
     * @return Creation date:(2008/7/9 下午 04:12:35)
     */
    public int doCheckRspMsg() {
        return doCheckRspMsg(this);
    }

    /**
     * 
     * @return Creation date:(2008/7/9 下午 04:12:35)
     */
    public int doCheckRspMsg(TxnMain txnMain) {
        TxnC31077 txn = (TxnC31077) txnMain;
        int RtnResult = SUCCESS;
        try {
            /*
             * //Check Flags String sFlags = Misc.padZero(Integer.toString(Integer.parseInt(txn.getGatewayHeader_Flags_R(),16),2),8); if (!sFlags.equals("00010000")){ throw new Exception(
             * "Flags Error " + sFlags); }
             * 
             * //Check System Response Code if (!txn.getGatewayHeader_SysRespCode_R().equals(TxnMain.GatewayHeader_FunCode_AM)){ throw new Exception("System Response Code != Application ACK"); }
             * 
             * //Check Node ID if (!txn.getGatewayHeader_NodeID_R().equals(TxnMain.NodeID)){ throw new Exception("Node ID Error " + txn.getGatewayHeader_NodeID_R() + " != " + TxnMain.NodeID); }
             */
            if (!doCheckRespHeader(txn)) {
                RtnResult = ERROR;
                throw new Exception("CheckRespHeader Error");
            }
            // Check Response Code
            String respcode = txn.getRespCode_R();
            if (!CheckNumeric(respcode, 5)) {
                RtnResult = ERR_AllowTandemLog;
                throw new Exception("Check Response Code Error = " + txn.getRespCode_R());
            }
            if (!respcode.equals("00000")) {
                RtnResult = ERR_AllowTandemLog;
                throw new Exception("Check Response Code != 00000 " + txn.getRespCode_R());
            }
            // Check Timestamp
            if (!CheckNumeric(txn.getTimestamp_R(), 14)) {
                RtnResult = ERR_AllowTandemLog;
                throw new Exception("Check Timestamp Error = " + txn.getTimestamp_R());
            }
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
        return String.valueOf(Long.parseLong(MaxMsgSeqNo, 16));
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
     * @return Returns the respCode_R.
     */
    public String getRespCode_R() {
        return new String(Misc.hex2Bin(RespCode_R.getBytes()));
    }

    /**
     * @return Returns the respText_R.
     */
    public String getRespText_R() {
        return RespText_R;
    }

    /**
     * @return Returns the retryFlag_R.
     */
    public String getRetryFlag_R() {
        return RetryFlag_R;
    }

    /**
     * @return Returns the timestamp_R.
     */
    public String getTimestamp_R() {
        return new String(Misc.hex2Bin(Timestamp_R.getBytes()));
    }

    /**
     * @return Returns the tmlID_R.
     */
    public String getTmlID_R() {
        return TmlID_R;
    }

    /**
     * @param args
     *            Creation date:(2008/7/9 下午 03:26:37)
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        // System.out.println(Integer.toHexString(4501));
        // System.out.println(MsgFunCode);
        // System.out.println(Integer.parseInt("1F42",16));
        System.out.println(Long.toHexString(4294967295L));

        TxnC31077 txn = new TxnC31077("8614", "4294967295");
        txn.ConstructBBMsg();

        String receiveMsg = "00010053000D001364000030303030303121A6FFFFFFFF0005003C796500381EF0000679651F42000621A61F450008FFFFFFFF1F410005301F47000930303030301F4000123230303830373037313230303030";
        ByteBuffer input = (ByteBuffer) ByteBuffer.allocate(receiveMsg.length() / 2);
        input.put(Misc.hex2Bin(receiveMsg.getBytes()));
        txn.setReceiveMSG(input);
        txn.Deconstruct();
        System.out.println(txn.getTimestamp_R());
        System.out.println(txn.getRespCode_R());
        LOG.debug(txn.getRespCode_R());

        // 先取出Head三欄位，以利後續處理
        System.out.println("MsgFunCode=" + TxnMain.DeconstructHead(input).get(TxnMain.MsgFunCode_Name));
    }
}
