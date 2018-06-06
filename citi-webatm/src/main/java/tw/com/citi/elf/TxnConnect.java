/*
 * @(#)TxnConnect.java
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

import com.citi.utils.Misc;

/**
 * implementation init Connect... to build ByteBuffer for RequestData and deconstruct ResponseData from TcpClient include main function...ConstructBBMsg, Deconstruct
 */
public class TxnConnect extends TxnMain {
    // log4J category
    private Logger LOG = LoggerFactory.getLogger(getClass());

    // R--> ID for ReceiveMsg From BAFES

    private static final String GatewayHeader_FunCode = "01"; // request Function Code(B1)
    private static final String GatewayHeader = GatewayHeader_FunCode + "0100"; // request
    // private static final String GatewayHeader_R1 = GatewayHeader_FunCode+"1101"; //normal response
    // private static final String GatewayHeader_R2 = GatewayHeader_FunCode+"1301"; //error response
    private String TmlID = "0000";
    private String MsgSeqNo = null;

    // ReceiveMsg From BAFES
    private String TmlID_R = null;
    private String MsgSeqNo_R = null;

    public TxnConnect(String TerminalID, String msgSeq) {
        TmlID = Misc.padZero(Integer.toHexString(Integer.parseInt(TerminalID)), 4);
        TmlID_Decimal8Digit = TerminalID;
        MsgSeqNo = Misc.padZero(Long.toHexString(Long.parseLong(msgSeq)), 8);
        super.LOG = LOG;
    }

    /**
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
        sbSendMsg.append(sElfElement_ID);
        iKeepPoint1 = sbSendMsg.length();

        iElementLen = 4 + 15;
        sbSendMsg.append(sGatewayHeader_ID);
        sbSendMsg.append(Misc.padZero(Integer.toHexString(iElementLen), iPadZeroLen));
        sbSendMsg.append(GatewayHeader).append(NodeID).append(TmlID).append(MsgSeqNo);
        iTempLen1 += iElementLen;

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
     * 
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

            case 13:
                iPoint = getELFData(iPoint, msg, hmData);
                GatewayHeader_R = (String) hmData.get(HMDataKEY);
                GatewayHeader_FunCode_R = GatewayHeader_R.substring(0, 2);
                GatewayHeader_Flags_R = GatewayHeader_R.substring(2, 4);
                GatewayHeader_SysRespCode_R = GatewayHeader_R.substring(4, 6);
                GatewayHeader_NodeID_R = GatewayHeader_R.substring(6, 18);

                TmlID_R = GatewayHeader_R.substring(18, 22);
                MsgSeqNo_R = GatewayHeader_R.substring(22); // 借用MsgID當成MsgSeqNo
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
        TxnConnect txn = (TxnConnect) txnMain;
        try {
            /*
             * char [] caFlags = Misc.padZero(Integer.toString(Integer.parseInt(txn.getGatewayHeader_Flags_R(),16),2),8).toCharArray(); if (caFlags[0] != '0') throw new Exception("Flags Error " + new
             * String(caFlags)); if (caFlags[1] != '0') throw new Exception("Flags Error " + new String(caFlags)); if (caFlags[2] != '0') throw new Exception("Flags Error " + new String(caFlags)); if
             * (caFlags[3] != '1') throw new Exception("Flags Error " + new String(caFlags)); if (caFlags[4] != '0') throw new Exception("Flags Error " + new String(caFlags)); if (caFlags[5] != '0')
             * throw new Exception("Flags Error " + new String(caFlags)); if (caFlags[6] != '0') throw new Exception("Flags Error " + new String(caFlags)); if (caFlags[7] != '1') throw new Exception(
             * "Flags Error " + new String(caFlags));
             * 
             * System.out.println(new String(caFlags));
             */

            // Check Flags
            String sFlags = Misc.padZero(Integer.toString(Integer.parseInt(txn.getGatewayHeader_Flags_R(), 16), 2), 8);
            if (!sFlags.equals("00010001")) {
                throw new Exception("Flags Error " + sFlags);
            }

            // Check System Response Code
            if (txn.getGatewayHeader_SysRespCode_R().equals(TxnMain.GatewayHeader_SysRespCode_SN)) {
                throw new Exception("System Response Code = System NAK");
            } else if (!txn.getGatewayHeader_SysRespCode_R().equals(TxnMain.GatewayHeader_SysRespCode_SA)) {
                throw new Exception("System Response Code != System NAK &  System ACK");
            }

            // Check Node ID
            if (!txn.getGatewayHeader_NodeID_R().equals(TxnMain.NodeID)) {
                throw new Exception("Node ID Error " + txn.getGatewayHeader_NodeID_R() + " != " + TxnMain.NodeID);
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return ERROR;
        }

        return SUCCESS;
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
        return 0;
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
     * @return Returns the msgSeqNo_R.
     */
    public String getMsgSeqNo_R() {
        return String.valueOf(Long.parseLong(MsgSeqNo_R, 16));
        // return MsgSeqNo_R;
    }

    /**
     * @return Returns the tmlID_R.
     */
    public String getTmlID_R() {
        return String.valueOf(Integer.parseInt(TmlID_R, 16));
    }

    /**
     * @param args
     *            Creation date:(2008/7/9 下午 03:26:37)
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        // 使用 MMDDhhmmss --做為msg-id
        TxnConnect txn = new TxnConnect("0", Misc.genDate(Misc.DT_DATETIME).substring(4));
        txn.ConstructBBMsg();

        System.out.println(txn.getTmlID());
        System.out.println(txn.getMsgSeqNo());

        String receiveMsg = "00010017000D00130111013030303030310000300CCDFF";
        ByteBuffer input = (ByteBuffer) ByteBuffer.allocate(receiveMsg.length() / 2);
        input.put(Misc.hex2Bin(receiveMsg.getBytes()));
        txn.setReceiveMSG(input);
        txn.Deconstruct();

        // 先取出Head三欄位，以利後續處理
        System.out.println(txn.getGatewayHeader_R());
        System.out.println(txn.getMsgSeqNo_R());
        System.out.println(txn.getTmlID_R());
        System.out.println(txn.doCheckRspMsg(txn));
    }
}
