/*
 * @(#)TxnMain.java
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

import com.citi.utils.Misc;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl;
import com.iisigroup.cap.utils.CapAppContext;

/**
 * Define some ELF ID static value to define abstract method for sub-class to implementation and deconstruct Head ResponseData for TcpClient to get some index_key(msgseqno,tmlid,funcode) *
 */
public abstract class TxnMain {
    private static APSystemService APSystem = CapAppContext.getApplicationContext().getBean(APSystemServiceImpl.class);

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int ERR_AllowTandemLog = 2;

    // 要搬到GLOBAL去
    protected static final String sElfElement_ID = Misc.padZero(Integer.toHexString(1), 4);// "00001";
    protected static final String sGatewayHeader_ID = Misc.padZero(Integer.toHexString(13), 4);// "00013";
    protected static final String sTextElement_ID = Misc.padZero(Integer.toHexString(5), 4);// "00005";

    protected static final String sMsgFunCode_ID = Integer.toHexString(7920);// "7920";
    protected static final String sTmlID_ID = Integer.toHexString(8002);// "8002";
    protected static final String sMsgSeqNo_ID = Integer.toHexString(8005);// "8005";
    protected static final String sRetryFlag_ID = Integer.toHexString(8001);// "8001";
    protected static final String sVersNumber_ID = Integer.toHexString(7900);// "7900";
    protected static final String sReleaseVer_ID = Integer.toHexString(7361);// "7361";
    // protected static final String sEncryCounter_ID = Integer.toHexString(7932);//"7932";
    protected static final String sMsgVerNumber_ID = Integer.toHexString(20509);// "20509";

    protected static final String sCurrencyCode_ID = Integer.toHexString(7215);// "7215";
    protected static final String sTransferNumber_ID = Integer.toHexString(7767);// "7767";

    // R--> for ReceiveMsg ID
    protected static final String sRespCode_R_ID = Integer.toHexString(8007);// "8007";
    protected static final String sAcqRespCode_R_ID = Integer.toHexString(7300);// "7300";
    protected static final String sActionCode_R_ID = Integer.toHexString(7302);// "7302";

    protected static final String sSrcBalanceTable_R_ID = Integer.toHexString(7506);// "7506";
    protected static final String sCurBalance_R_ID = Integer.toHexString(7106);// "7106";
    protected static final String sAvaBalance_R_ID = Integer.toHexString(7104);// "7104";
    protected static final String sNetworkDT_R_ID = Integer.toHexString(7430);// "7430";
    protected static final String sSTAN_R_ID = Integer.toHexString(7234);// "7234";

    // S--> ID for SendMsg To BAFES
    protected static final String sRespCode_S_ID = Integer.toHexString(7306);// "7306";

    protected static final String sAuthTknTable_S_ID = Integer.toHexString(18900);// "18900";
    protected static final String sAuthTknType_S_ID = Integer.toHexString(18901);// "18901";
    protected static final String sAuthTkn_S_ID = Integer.toHexString(18902);// "18902";
    protected static final String sCATCardCC_S_ID = Integer.toHexString(8246);// "8246";
    protected static final String sStartSessDT_S_ID = Integer.toHexString(7420);// "7420";
    protected static final String sTranSerialNO_S_ID = Integer.toHexString(40092);// "40092";
    protected static final String sChipInputBlk_S_ID = Integer.toHexString(40095);// "40095";
    protected static final String sIssuerBankID_S_ID = Integer.toHexString(40096);// "40096";
    protected static final String sAcctIdGroup_S_ID = Integer.toHexString(20516);// "20516";
    protected static final String sAccountNO_S_ID = Integer.toHexString(7000);// "7000";
    protected static final String sSessInfoTable_S_ID = Integer.toHexString(40518);// "40518";
    protected static final String sSessInfoType_S_ID = Integer.toHexString(40519);// "40519";
    protected static final String sSessInfoValue_S_ID = Integer.toHexString(40520);// "40520";
    protected static final String sTmlType_S_ID = Integer.toHexString(31050);// "未定義";
    protected static final String sTransAmount_S_ID = Integer.toHexString(7100);// 7100;
    protected static final String sTransCurCode_S_ID = Integer.toHexString(7235);// 7235;
    protected static final String sTTC_S_ID = Integer.toHexString(18965);// 18965;
    protected static final String sSourceActIden_S_ID = Integer.toHexString(7504);// 7504;
    protected static final String sDestActIden_S_ID = Integer.toHexString(7505);// 7505;
    protected static final String sOrigMsgSeqNo_S_ID = Integer.toHexString(8403);// 8403;
    protected static final String sRevReasonCode_S_ID = Integer.toHexString(7305);// 7305;
    protected static final String sNewPinBlk_S_ID = Integer.toHexString(7204);// "40095";

    public static final String CATCardCC_Citi = "01"; // 8246- 01=Citicard
    public static final String CATCardCC_Other = "27"; // 8246- 27=any other bank card

    public static final String TTC_Transfer = "19";
    public static final String TTC_Payment = "23";

    public static final String TQC_Transfer = "19";
    public static final String TQC_Payment = "23";

    public static final String TmlType_TYPE1 = "6534"; // 31050- 6534=不具密碼輸入器之讀卡機
    public static final String TmlType_TYPE2 = "6536"; // 31050- 6536=具密碼輸入器之確認型讀卡機

    public static final String RespCode_S_N = "00";// 7306- 00=Normal CAT response code
    public static final String RespCode_S_R = "09";// 7306- 09=Command rejected by the CAT
    public static final String AcqRespCode_R_Success = "00000";
    public static final String AcqRespCode_R_ResetSeq = "17210";// Error code 17210 need Reset SeqNo

    public static final String GatewayHeader_FunCode_AM = "00"; // request Function Code(B1) Application Message
    public static final String GatewayHeader_FunCode_CM = "01"; // request Function Code(B1) Connect Message
    public static final String GatewayHeader_FunCode_HM = "64"; // request Function Code(B1) Hand-Shaking Message

    public static final String GatewayHeader_SysRespCode_AA = "00"; // request System Response Code (B1) Application ACK
    public static final String GatewayHeader_SysRespCode_SA = "01"; // request System Response Code (B1) System ACK
    public static final String GatewayHeader_SysRespCode_SN = "10"; // request System Response Code (B1) System NAK

    protected static final String NodeID = new String(Misc.bin2Hex(((String) APSystem.getSYS_PRAM_MAP().get("NodeID")).getBytes()));// "303030303031";

    protected abstract boolean doCheckOrgData(); // 檢核設定等待組成電文的資料

    protected abstract int doCheckRspMsg(); // 單一內容檢核 ex: 31077 基本檢核

    protected abstract int doCheckRspMsg(TxnMain txn); // 對應內容檢核 ex: P4501(request)-->P4501(response)

    public abstract int getMsgFunCode();

    public abstract String getTmlID();

    public abstract String getMsgSeqNo();

    public abstract String getGatewayHeader_FunCode();

    protected String TmlID_Decimal8Digit = null;

    protected ByteBuffer ReceiveMSG = null;
    protected ByteBuffer SendMSG = null;
    protected String SendMSG_HEX = null;
    protected String ReceiveMSG_HEX = null;

    protected static int iELF_Len = 4;
    protected static int iPadZeroLen = 4;
    protected static boolean isEnableDataMsgFC = true; // enabled Data Message Format code
    protected static String sDataMsgFC = "80"; // Data Message Format code

    protected static String HMDataKEY = "data";

    public static String ElfElement_LEN_Name = "ElfElement_LEN";
    public static String TextElement_LEN_Name = "TextElement_LEN";
    public static String GatewayHeader_Name = "GatewayHeader";

    public static String MsgFunCode_Name = "MsgFunCode";
    public static String TmlID_Name = "TmlID";
    public static String MsgSeqNo_Name = "MsgSeqNo";

    protected int ElfElement_LEN = 0;
    protected int TextElement_LEN = 0;
    public String GatewayHeader_R = null; // Receive GatewayHeader from BAFES
    public String GatewayHeader_FunCode_R = null;
    public String GatewayHeader_Flags_R = null;
    public String GatewayHeader_SysRespCode_R = null;
    public String GatewayHeader_NodeID_R = null;

    protected static Logger LOG = null;

    /**
     * @return Returns the elfElement_LEN.
     */
    public int getElfElement_LEN() {
        return ElfElement_LEN;
    }

    /**
     * @return Returns the textElement_LEN.
     */
    public int getTextElement_LEN() {
        return TextElement_LEN;
    }

    /**
     * @return Returns the gatewayHeader_R.
     */
    public String getGatewayHeader_R() {
        return GatewayHeader_R;
    }

    /**
     * @return Returns the gatewayHeader_Flags_R.
     */
    public String getGatewayHeader_Flags_R() {
        return GatewayHeader_Flags_R;
    }

    /**
     * @return Returns the gatewayHeader_FunCode_R.
     */
    public String getGatewayHeader_FunCode_R() {
        return GatewayHeader_FunCode_R;
    }

    /**
     * @return Returns the gatewayHeader_NodeID_R.
     */
    public String getGatewayHeader_NodeID_R() {
        return GatewayHeader_NodeID_R;
    }

    /**
     * @return Returns the gatewayHeader_SysRespCode_R.
     */
    public String getGatewayHeader_SysRespCode_R() {
        return GatewayHeader_SysRespCode_R;
    }

    /**
     * @param receiveMSG
     *            The receiveMSG to set.
     */
    public void setReceiveMSG(ByteBuffer receiveMSG) {
        ReceiveMSG = receiveMSG;
    }

    /**
     * @return Returns the sendMSG.
     */
    public ByteBuffer getSendMSG() {
        return SendMSG;
    }

    /**
     * @param sendMSG
     *            The sendMSG to set.
     */
    public void setSendMSG(ByteBuffer sendMSG) {
        SendMSG = sendMSG;
    }

    /**
     * @return Returns the sendMSG_HEX.
     */
    public String getSendMSG_HEX() {
        return SendMSG_HEX.substring(6);
    }

    /**
     * @param sendMSG_HEX
     *            The sendMSG_HEX to set.
     */
    public void setSendMSG_HEX(String sendMSG_HEX) {
        SendMSG_HEX = sendMSG_HEX;
    }

    /**
     * @return Returns the receiveMSG_HEX.
     */
    public String getReceiveMSG_HEX() {
        return ReceiveMSG_HEX;
    }

    /**
     * @param receiveMSG_HEX
     *            The receiveMSG_HEX to set.
     */
    public void setReceiveMSG_HEX(String receiveMSG_HEX) {
        ReceiveMSG_HEX = receiveMSG_HEX;
    }

    /**
     * get element from ByteBuffer do while get data then put in to DataMap why return is HashMap? because use HashMap can bear many of values... in other way, The HashMap can replace with Bean
     **/
    public static HashMap DeconstructHead(ByteBuffer ReceiveMSG) {
        String msg = new String(Misc.bin2Hex(ReceiveMSG.array()));
        int iPoint = 0;
        int key = 0;
        int iStopCount = 0;
        final int HeadStopCount = 7; // 共7個Element 包含(1,5,13,7920,8002,8005,MsgID)請參考 Application Message Header
        int msgLen = msg.length();
        HashMap hmData = new HashMap(); // for data return
        LOG.debug("DeconstructMsgHead.....");
        while (msgLen > iPoint) {
            key = Integer.parseInt(msg.substring(iPoint, iPoint + iELF_Len), 16);
            LOG.debug("key - " + key);
            iPoint += 4;
            switch (key) {
            case 1:
                Integer elfElement_LEN = new Integer(getELFLen(iPoint, msg));
                hmData.put(TxnMain.ElfElement_LEN_Name, hmData.get(HMDataKEY));
                iPoint += 4;
                iStopCount++;
                break;

            case 5:
                Integer textElement_LEN = new Integer(getELFLen(iPoint, msg));
                hmData.put(TxnMain.TextElement_LEN_Name, hmData.get(HMDataKEY));
                iPoint += 4;
                iStopCount++;
                break;

            case 13:
                iPoint = getELFData(iPoint, msg, hmData);
                hmData.put(TxnMain.GatewayHeader_Name, hmData.get(HMDataKEY));
                iStopCount++;
                break;

            case 7920:
                iPoint = getELFData(iPoint, msg, hmData);
                hmData.put(TxnMain.MsgFunCode_Name, hmData.get(HMDataKEY));
                iStopCount++;
                break;

            case 8002:
                iPoint = getELFData(iPoint, msg, hmData);
                hmData.put(TxnMain.TmlID_Name, Misc.padZero(String.valueOf(Integer.parseInt((String) hmData.get(HMDataKEY), 16)), 8));
                iStopCount++;
                break;

            case 8005:
                iPoint = getELFData(iPoint, msg, hmData);
                hmData.put(TxnMain.MsgSeqNo_Name, String.valueOf(Long.parseLong((String) hmData.get(HMDataKEY), 16)));
                iStopCount++;
                break;

            // 下列是For MSGID 那一欄在用的，如有新增交易記得加上對應的MSGID。
            case 4101:
                getELFLen(iPoint, msg);
                iPoint += 4;
                iStopCount++;
                break;

            case 4105:
                getELFLen(iPoint, msg);
                iPoint += 4;
                iStopCount++;
                break;

            case 4113:
                getELFLen(iPoint, msg);
                iPoint += 4;
                iStopCount++;
                break;

            case 4118:
                getELFLen(iPoint, msg);
                iPoint += 4;
                iStopCount++;
                break;

            case 4404:
                getELFLen(iPoint, msg);
                iPoint += 4;
                iStopCount++;
                break;

            case 4411:
                getELFLen(iPoint, msg);
                iPoint += 4;
                iStopCount++;
                break;

            case 4412:
                getELFLen(iPoint, msg);
                iPoint += 4;
                iStopCount++;
                break;

            case 4501:
                getELFLen(iPoint, msg);
                iPoint += 4;
                iStopCount++;
                break;

            case 4801:
                getELFLen(iPoint, msg);
                iPoint += 4;
                iStopCount++;
                break;

            case 4802:
                getELFLen(iPoint, msg);
                iPoint += 4;
                iStopCount++;
                break;

            case 31077:
                getELFLen(iPoint, msg);
                iPoint += 4;
                iStopCount++;
                break;

            default:
                iPoint = getELFData(iPoint, msg, hmData);
                break;
            }
            key = 0;
            hmData.put(HMDataKEY, "");
            if (iStopCount == HeadStopCount) {
                return hmData;
            }
        }
        return hmData;
    }

    /**
     * Use HashMap set Data value as call by reference then DeconstructHead can got it and set into corresponding element
     **/
    protected static int getELFData(int iPoint, String msg, HashMap hmData) {
        int len;
        int msgLen = msg.length();
        len = Integer.parseInt(msg.substring(iPoint, iPoint + iELF_Len), 16);
        iPoint += 4;
        LOG.debug("len - " + (len - 4));
        if ((iPoint + ((len - 4) * 2)) > msgLen) {
            LOG.error("iPoint exceed msgLen");
            return msgLen; // 結束
        }

        String data = msg.substring(iPoint, iPoint += ((len - 4) * 2));
        // iPoint += ((len-4)*2);
        LOG.debug("data - " + data + "\n");
        hmData.put(HMDataKEY, data);
        return iPoint;
    }

    protected static int getELFLen(int iPoint, String msg) {
        int CollectLen = Integer.parseInt(msg.substring(iPoint, iPoint + iELF_Len), 16);
        iPoint += 4;
        LOG.debug("len - " + (CollectLen - 4) + "\n");
        return CollectLen;
    }

    /**
     * 將訊息加上TCP Message FormateCode 與長度。
     **/
    public static StringBuffer addTcpMsgFormatCode(StringBuffer sbSendMsg) {
        if (isEnableDataMsgFC) {
            // Insert TCP Transport-Level Message
            // int iMsgLen = (sbSendMsg.toString().toUpperCase().length() / 2) + 2;
            int iMsgLen = (sbSendMsg.toString().toUpperCase().length() / 2);
            String TLMsg = sDataMsgFC + Misc.padZero(Integer.toHexString(iMsgLen), iPadZeroLen);
            sbSendMsg.insert(0, TLMsg);
        }
        return sbSendMsg;
    }

    /**
     * 基本的檢核Response 之Header資料。
     **/
    public static boolean doCheckRespHeader(TxnMain txn) {
        try {
            // Check Flags
            String sFlags = Misc.padZero(Integer.toString(Integer.parseInt(txn.getGatewayHeader_Flags_R(), 16), 2), 8);
            if (!sFlags.equals("00010000")) {
                throw new Exception("Flags Error " + sFlags);
            }

            // Check System Response Code
            if (!txn.getGatewayHeader_SysRespCode_R().equals(TxnMain.GatewayHeader_FunCode_AM)) {
                throw new Exception("System Response Code != Application ACK");
            }

            // Check Node ID
            if (!txn.getGatewayHeader_NodeID_R().equals(TxnMain.NodeID)) {
                throw new Exception("Node ID Error " + txn.getGatewayHeader_NodeID_R() + " != " + TxnMain.NodeID);
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 基本的檢核Request 之Header資料。
     **/
    public static boolean doCheckReqHeader(TxnMain txn) {
        try {
            // Check Flags
            String sFlags = Misc.padZero(Integer.toString(Integer.parseInt(txn.getGatewayHeader_Flags_R(), 16), 2), 8);
            if (!sFlags.equals("00000000")) {
                throw new Exception("Flags Error " + sFlags);
            }

            // Check System Response Code
            if (!txn.getGatewayHeader_SysRespCode_R().equals(TxnMain.GatewayHeader_FunCode_AM)) {
                throw new Exception("System Response Code != Application ACK");
            }

            // Check Node ID
            if (!txn.getGatewayHeader_NodeID_R().equals(TxnMain.NodeID)) {
                throw new Exception("Node ID Error " + txn.getGatewayHeader_NodeID_R() + " != " + TxnMain.NodeID);
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 檢核是否為數字，並限定檢核的長度 如果檢核字串超過限定的長度 則後面不理會
     */
    protected boolean CheckNumeric(String s, int len) {
        try {
            byte[] ba = s.getBytes();

            for (int i = 0; i < len; i++) {
                if (ba[i] < 0x30 || ba[i] > 0x39) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 檢核是否為數字
     */
    protected boolean CheckNumeric(String s) {
        try {
            byte[] ba = s.getBytes();
            int len = ba.length;
            for (int i = 0; i < len; i++) {
                if (ba[i] < 0x30 || ba[i] > 0x39) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param args
     *            Creation date:(2008/7/9 下午 06:02:34)
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
