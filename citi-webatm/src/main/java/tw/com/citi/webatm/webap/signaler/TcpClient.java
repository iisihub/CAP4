/*
 * @(#)TcpClient
 *
 * Copyright (c) 2008 Citi Group Incorporated. All rights reserved.
 *
 * Modify History:
 *  v1.00, 2008/07/07, Kevin Chung
 *   1) First release
 *
 */
package tw.com.citi.webatm.webap.signaler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Category;

import com.citi.utils.Misc;
import com.citi.webatm.rmi.TTandemLog;

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
import tw.com.citi.webatm.txn.TxnTerminal;

/**
 * This class handle the TcpConnection to BAFES use java NIO API with selector implement Runnable won't block main process call data exchange with foreign class through (htSend & htReceive)
 * doConnect() & doSend() & doReceive() are used by foeign class to info the Selector to do something.
 **/
public class TcpClient implements Runnable {

    // log4J category
    static Category LOG = Category.getInstance("TcpClient");
    protected static String DataMsgFC = "80"; // Data Message Format code
    protected static String KeepAliveFC = "8F"; // Data Message Format code
    private static boolean isStopTrns = false; // check if for transaction resquest
    private static boolean isShutdown = false;
    private static boolean isConnect = false;
    private static boolean isReConnectFlag = true;
    private final static int MaxConnectCounter = 5; // see WebATM_DB_DS
    private final static int MaxKeepAliveCounter = 3; // see WebATM_DB_DS
    private final static int iSleepInterval = 50;
    private final static int iKeepAliveTimeMillis = 5 * 60 * 1000; // 5min = 300000
    private static int iRequestTimeOut = 100 * 1000;
    private static long lLastTranTimeMillis = 0;
    private static int iKeepAliveCounter = MaxKeepAliveCounter; // for Count Send KeepAlive if = n then disconnect,reconnect
    private static int iConnectCounter = -1;// MaxConnectCounter; //for Retry Connect counter if = 0 then isConnect = false; isShutdown = true;

    private Hashtable htSend = null; // for TxnTerminal send date to Host
    private Hashtable htReceive = null; // for keep receive object for TxnTerminal use
    private Hashtable htPairMapping = null; // for send or receive mapping msg whether exist

    private String server_ip = "172.18.92.35"; // BAFES ip
    private int server_port = 9000; // BAFES port
    private SocketChannel client;
    private Selector selector;
    private SelectionKey key;

    private final static Object mutex = new Object();
    private static Thread ThreadObj = null;

    public TcpClient() {

    }

    public TcpClient(Hashtable send, Hashtable receive) {
        htSend = send;
        htReceive = receive;
        htPairMapping = new Hashtable();
    }

    public TcpClient(String srv_ip, int srv_port, Hashtable send, Hashtable receive) {
        server_ip = srv_ip;
        server_port = srv_port;
        htSend = send;
        htReceive = receive;
        htPairMapping = new Hashtable();
    }

    /**
     * @return Returns the isShutdown.
     */
    public boolean isShutdown() {
        return isShutdown;
    }

    /**
     * @param isShutdown
     *            The isShutdown to set.
     */
    public void setShutdown(boolean isShutdown) {
        this.isShutdown = isShutdown;
    }

    /**
     * @return Returns the isStopTrns.
     */
    public boolean isStopTrns() {
        return isStopTrns;
    }

    /**
     * When want to stop AccountTrnsaction set true
     * 
     * @param isStopTrns
     *            The isStopTrns to set.
     */
    public void setStopTrns(boolean isStopTrns) {
        synchronized (mutex) {
            this.isStopTrns = isStopTrns;
        }
    }

    /**
     * @return Returns the isReConnect.
     */
    public boolean isReConnectFlag() {
        return isReConnectFlag;
    }

    /**
     * foreign object(TerminalPool) may update statusFlag so use mutex object to luck status
     * 
     * @param isReConnect
     *            The isReConnect to set.
     */
    public void setReConnectFlag(boolean isReConnectFlag) {
        synchronized (mutex) {
            TcpClient.isReConnectFlag = isReConnectFlag;
        }
    }

    /**
     * foreign object(TerminalPool) may get Counter so use mutex object to luck status
     */
    public int getKeepAliveCounter() {
        synchronized (mutex) {
            return iKeepAliveCounter;
        }
    }

    /**
     * foreign object(TerminalPool) may update statusFlag so use mutex object to luck status
     */
    public void setKeepAliveCounter(int val) {
        synchronized (mutex) {
            iKeepAliveCounter = val;
        }
    }

    /**
     * create connect to server
     */
    public boolean doConnect() {
        boolean isConn = true;
        try {
            SocketAddress rama = new InetSocketAddress(server_ip, server_port);
            client = SocketChannel.open();
            selector = Selector.open();
            client.configureBlocking(false);
            // if setting Blocking connect will not
            // occur Connection refused at connect(rama);
            client.connect(rama);
            client.register(selector, SelectionKey.OP_CONNECT);
            isConn = client.isConnected();
        } catch (ClosedChannelException cce) {
            cce.printStackTrace();
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return isConn;
    }

    /**
     * TcpClient Thread 1.initial param 2.into loop to handle socket issue 3.get htSend HashTable data if have any stuff 3.1.GatewayHeader_FunCode to deal with each type Txn 3.2.Set ByteBuffer from
     * each ELF object 3.3.Remove this ELF object from htSend 4.Process PairMapping table, if surpass the limit time(iRequestTimeOut) 5.Process KeepAlive issue, when socket idle surpass limit
     * time(iKeepAliveTimeMillis) 6.enter NIO selector API 6.1.by key isConnectable - connect Socket 6.2.by key isWritable - write data to Socket and set lLastTranTimeMillis for nowTime 6.3.by key
     * isReadable - read data from Socket and deal with each txn integrity parse data get MsgFunCode to do corresponding process then put into htReceive and set lLastTranTimeMillis for nowTime
     */
    public void run() {
        // isConnect = doConnect();
        isConnect = true;
        doConnect();
        ByteBuffer bbSend = null;
        ByteBuffer bbReceive_FC = null;
        ByteBuffer bbReceive = null;
        int iRemain_len = 0;

        boolean isFinishMSG = true;
        boolean isFinishHEADER = true;
        lLastTranTimeMillis = System.currentTimeMillis();
        // while (isConnect && !isShutdown) {
        while (!isShutdown) {
            try {
                Thread.sleep(iSleepInterval);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }

            // get Send HashMap TxnData=======================================

            if (htSend.size() > 0) {
                LOG.info("htSend size = " + htSend.size() + "-" + isConnect);
                Enumeration enumer = htSend.keys();
                while (enumer.hasMoreElements() && bbSend == null) {
                    String key = (String) enumer.nextElement();
                    TxnMain txn = (TxnMain) htSend.get(key);

                    // Server Sign On Message
                    if (txn.getGatewayHeader_FunCode().equals(TxnMain.GatewayHeader_FunCode_CM)) {
                        bbSend = txn.getSendMSG();
                        htPairMapping.put(txn.getTmlID() + "-0-" + txn.getMsgSeqNo(), getTimeMillis());
                        htSend.remove(txn.getTmlID() + "-0-" + txn.getMsgSeqNo());
                        break;
                    }

                    int funcode = txn.getMsgFunCode();
                    String tmlid = null;
                    String msgseqno = null;
                    StringBuffer temp = new StringBuffer();
                    LOG.info("funcode=" + funcode);
                    LOG.info("GatewayHeader_FunCode=" + txn.getGatewayHeader_FunCode());
                    switch (funcode) {
                    case 4101: // 因為此交易BAFES Response之MsgSeqNo 不會和Request一樣所以，Mapping到Terminal+FunCode
                        // bbSend =((TxnC4101)txn).ConstructBBMsg();
                        bbSend = txn.getSendMSG();
                        tmlid = ((TxnC4101) txn).getTmlID();
                        msgseqno = ((TxnC4101) txn).getMsgSeqNo();
                        temp.append(tmlid)
                                // .append("-") //for test
                                // .append(msgseqno)
                                .append("-4101");
                        htPairMapping.put(temp.toString(), getTimeMillis());
                        htSend.remove(temp.toString());

                        // htSend.remove("8614-1-4101");
                        break;

                    case 4105:
                        // bbSend =((TxnC4105)txn).ConstructBBMsg();
                        bbSend = txn.getSendMSG();
                        tmlid = ((TxnC4105) txn).getTmlID();
                        msgseqno = ((TxnC4105) txn).getMsgSeqNo();
                        temp.append(tmlid).append("-").append(msgseqno).append("-4105");
                        htPairMapping.put(temp.toString(), getTimeMillis());
                        htSend.remove(temp.toString());

                        break;

                    case 4404:
                        tmlid = ((TxnP4404) txn).getTmlID();
                        msgseqno = ((TxnP4404) txn).getMsgSeqNo();
                        temp.append(tmlid).append("-").append(msgseqno).append("-4404");
                        if (!isStopTrns) {
                            // bbSend =((TxnP4404)txn).ConstructBBMsg();
                            bbSend = txn.getSendMSG();
                            htPairMapping.put(temp.toString(), getTimeMillis());
                            htSend.remove(temp.toString());
                        } else {
                            bbSend = null;
                            htSend.remove(temp.toString());
                            LOG.debug("帳務、查詢性交易暫停使用");
                        }
                        break;

                    case 4113:
                        tmlid = ((TxnP4113) txn).getTmlID();
                        msgseqno = ((TxnP4113) txn).getMsgSeqNo();
                        temp.append(tmlid).append("-").append(msgseqno).append("-4113");
                        if (!isStopTrns) {
                            // bbSend =((TxnP4113)txn).ConstructBBMsg();
                            bbSend = txn.getSendMSG();
                            htPairMapping.put(temp.toString(), getTimeMillis());
                            htSend.remove(temp.toString());
                        } else {
                            bbSend = null;
                            htSend.remove(temp.toString());
                            LOG.debug("帳務、查詢性交易暫停使用");
                        }
                        break;

                    case 4118:
                        tmlid = ((TxnP4118) txn).getTmlID();
                        msgseqno = ((TxnP4118) txn).getMsgSeqNo();
                        temp.append(tmlid).append("-").append(msgseqno).append("-4118");
                        if (!isStopTrns) {
                            // bbSend =((TxnP4118)txn).ConstructBBMsg();
                            bbSend = txn.getSendMSG();
                            htPairMapping.put(temp.toString(), getTimeMillis());
                            htSend.remove(temp.toString());
                        } else {
                            bbSend = null;
                            htSend.remove(temp.toString());
                            LOG.debug("帳務、查詢性交易暫停使用");
                        }
                        break;

                    case 4411:
                        tmlid = ((TxnP4411) txn).getTmlID();
                        msgseqno = ((TxnP4411) txn).getMsgSeqNo();
                        temp.append(tmlid).append("-").append(msgseqno).append("-4411");
                        if (!isStopTrns) {
                            // bbSend =((TxnP4411)txn).ConstructBBMsg();
                            bbSend = txn.getSendMSG();
                            htPairMapping.put(temp.toString(), getTimeMillis());
                            htSend.remove(temp.toString());
                        } else {
                            bbSend = null;
                            htSend.remove(temp.toString());
                            LOG.debug("帳務、查詢性交易暫停使用");
                        }
                        break;

                    case 4412:
                        tmlid = ((TxnP4412) txn).getTmlID();
                        msgseqno = ((TxnP4412) txn).getMsgSeqNo();
                        temp.append(tmlid).append("-").append(msgseqno).append("-4412");
                        if (!isStopTrns) {
                            // bbSend =((TxnP4412)txn).ConstructBBMsg();
                            bbSend = txn.getSendMSG();
                            htPairMapping.put(temp.toString(), getTimeMillis());
                            htSend.remove(temp.toString());
                        } else {
                            bbSend = null;
                            htSend.remove(temp.toString());
                            LOG.debug("帳務、查詢性交易暫停使用");
                        }
                        break;

                    case 4501:
                        tmlid = ((TxnP4501) txn).getTmlID();
                        msgseqno = ((TxnP4501) txn).getMsgSeqNo();
                        temp.append(tmlid).append("-").append(msgseqno).append("-4501");
                        if (!isStopTrns) {
                            // bbSend =((TxnP4501)txn).ConstructBBMsg();
                            bbSend = txn.getSendMSG();
                            htPairMapping.put(temp.toString(), getTimeMillis());
                            htSend.remove(temp.toString());
                        } else {
                            bbSend = null;
                            htSend.remove(temp.toString());
                            LOG.debug("帳務、查詢性交易暫停使用");
                        }
                        break;

                    case 4801:
                        // bbSend =((TxnC4801)txn).ConstructBBMsg();
                        bbSend = txn.getSendMSG();
                        tmlid = ((TxnC4801) txn).getTmlID();
                        msgseqno = ((TxnC4801) txn).getMsgSeqNo();
                        temp.append(tmlid).append("-").append(msgseqno).append("-4801");
                        // htPairMapping.put(temp.toString(), getTimeMillis());
                        htSend.remove(temp.toString());
                        break;

                    case 4802:
                        // bbSend =((TxnC4802)txn).ConstructBBMsg();
                        bbSend = txn.getSendMSG();
                        tmlid = ((TxnC4802) txn).getTmlID();
                        msgseqno = ((TxnC4802) txn).getMsgSeqNo();
                        temp.append(tmlid).append("-").append(msgseqno).append("-4802");
                        // htPairMapping.put(temp.toString(), getTimeMillis());
                        htSend.remove(temp.toString());
                        break;

                    case 31077:
                        // bbSend =((TxnC31077)txn).ConstructBBMsg();
                        bbSend = txn.getSendMSG();
                        tmlid = ((TxnC31077) txn).getTmlID();
                        msgseqno = ((TxnC31077) txn).getMsgSeqNo();
                        temp.append(tmlid).append("-").append(msgseqno).append("-31077");
                        htPairMapping.put(temp.toString(), getTimeMillis());
                        htSend.remove(temp.toString());
                        break;
                    }
                    // System.out.println(temp.toString());
                    temp = null;
                }
                // client.register(selector, SelectionKey.OP_WRITE);
            }
            // get Send HashMap TxnData=======================================

            // process PairMapping HashMap Data=======================================

            Enumeration enumer = htPairMapping.keys();
            while (enumer.hasMoreElements()) {
                try {
                    Object key = enumer.nextElement();
                    Object value = htPairMapping.get(key);

                    if ((System.currentTimeMillis() - Long.parseLong((String) value)) > iRequestTimeOut) {
                        LOG.debug("Time Gap (毫秒)= " + (System.currentTimeMillis() - Long.parseLong((String) value)));
                        LOG.debug(key + " = " + htPairMapping.remove(key));
                        LOG.info("清掉超過時限的 HostSend Msg 【4801】或【4802】  -" + htReceive.remove(key));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            }

            // process PairMapping HashMap Data=======================================

            // process KeepAlive =======================================
            if (iKeepAliveCounter == 0) {
                // 如果KeepAlive倒數到0時，則Disconnect 再重建Connection
                isConnect = false;
                bbSend = null;
                key.cancel();
                try {
                    client = (SocketChannel) key.channel();
                    selector.close();
                    client.close();
                } catch (IOException cex) {
                    cex.printStackTrace();
                }
                isConnect = doConnect();
                // retry doConnect() must to sleep for a while, or will make more then one connection
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                }
                // iKeepAliveCounter = MaxKeepAliveCounter;
            } else {
                // 當沒有要發送的資料時，才可再發KeepAlive，有資料時就Trigger Send Event.
                if ((System.currentTimeMillis() - lLastTranTimeMillis) > iKeepAliveTimeMillis) {
                    if (bbSend == null) {
                        bbSend = (ByteBuffer) ByteBuffer.allocate(1);
                        bbSend.put(Misc.hex2Bin(KeepAliveFC.getBytes()));
                        this.doSend();
                        setKeepAliveCounter(iKeepAliveCounter - 1);
                        // iKeepAliveCounter--;
                    } else {
                        this.doSend();
                        setKeepAliveCounter(iKeepAliveCounter - 1);
                        // iKeepAliveCounter--;
                    }
                }
            }
            // process KeepAlive =======================================
            try {
                selector.select(150);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // System.out.println("selector.select=" + selector.select(100));
            Set readyKeys = selector.selectedKeys();
            Iterator iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                key = (SelectionKey) iterator.next();
                iterator.remove();
                try {
                    if (key.isWritable() && bbSend != null) {
                        client = (SocketChannel) key.channel();
                        bbSend.flip();
                        LOG.debug("Write data, length = " + client.write(bbSend));
                        LOG.debug("Write \n\n" + new String(Misc.bin2Hex(bbSend.array())));
                        key = client.register(selector, SelectionKey.OP_READ);
                        bbSend = null;
                        lLastTranTimeMillis = System.currentTimeMillis();
                    } else if (key.isReadable()) {
                        int header_len = 0;
                        LOG.debug("Read data");
                        if (isFinishMSG) {
                            if (isFinishHEADER) {
                                bbReceive_FC = (ByteBuffer) ByteBuffer.allocate(3);
                            }
                            LOG.debug("read length = " + (header_len = client.read(bbReceive_FC)));
                            if (header_len == -1)
                                break;

                            String sTemp = new String(Misc.bin2Hex(bbReceive_FC.array()));
                            LOG.debug("TCP Format code = 0x" + sTemp.substring(0, 2));
                            String sTempFC = sTemp.substring(0, 2);
                            if (sTempFC.equals(KeepAliveFC)) { // 如果是KeeyAlive訊息就不需讀滿3Byte
                                isFinishHEADER = true;
                            } else if (bbReceive_FC.position() != 3) {
                                isFinishHEADER = false;
                                break; // 跳出再重收，直到收滿3Byte
                            } else {
                                isFinishHEADER = true;
                            }
                        }

                        String sTemp = new String(Misc.bin2Hex(bbReceive_FC.array()));
                        LOG.debug("TCP Format code = 0x" + sTemp.substring(0, 2));
                        String sTempFC = sTemp.substring(0, 2);

                        if (sTempFC.equals(DataMsgFC)) {
                            int DataMsg_len = Integer.parseInt(sTemp.substring(2), 16); // DataMsg Define length
                            int read_len = 0; // exactly read Length from buffer
                            if (!isFinishMSG) {
                                ByteBuffer bbTemp = (ByteBuffer) ByteBuffer.allocate(iRemain_len);
                                LOG.debug("Read data, length = " + (read_len = client.read(bbTemp)));
                                if ((read_len - iRemain_len) != 0) {
                                    // 長度不夠，再收
                                    LOG.debug("DataMsg Define length Remain_len:" + iRemain_len + " Read_len:" + read_len);
                                    ByteBuffer bbTemp1 = (ByteBuffer) ByteBuffer.allocate(read_len);
                                    bbTemp1.put(bbTemp.array(), 0, read_len);
                                    bbReceive.put(bbTemp1.array());
                                    iRemain_len = iRemain_len - read_len;
                                    break; // 跳出再重收，直到收滿定義的長度
                                } else {
                                    // 剛好收完
                                    bbReceive.put(bbTemp.array());
                                    isFinishMSG = true;
                                }
                            } else {
                                bbReceive = (ByteBuffer) ByteBuffer.allocate(DataMsg_len);
                                LOG.debug("Read data, length = " + (read_len = client.read(bbReceive)));
                                if (DataMsg_len != read_len) {
                                    LOG.error("DataMsg Define length != exactly read Length");
                                    isFinishMSG = false;
                                    iRemain_len = DataMsg_len - read_len;
                                    break;
                                }
                            }

                            LOG.debug("Read \n\n" + new String(Misc.bin2Hex(bbReceive.array())));
                            HashMap hmHead = TxnMain.DeconstructHead(bbReceive);
                            String MsgFunCode = (String) hmHead.get(TxnMain.MsgFunCode_Name);
                            String TmlID = (String) hmHead.get(TxnMain.TmlID_Name);
                            String MsgSeqNo = (String) hmHead.get(TxnMain.MsgSeqNo_Name);
                            String GatewayHeader = (String) hmHead.get(TxnMain.GatewayHeader_Name);
                            // Server Sign On MSG
                            if (GatewayHeader.substring(0, 2).equals(TxnMain.GatewayHeader_FunCode_CM)) {
                                TxnConnect txnConn = new TxnConnect("0", "0"); // 隨便先 new 一個
                                txnConn.setReceiveMSG(bbReceive);
                                txnConn.Deconstruct();
                                String key = txnConn.getTmlID_R() + "-0-" + txnConn.getMsgSeqNo_R();
                                if (htPairMapping.containsKey(key)) {
                                    if ((System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(key))) <= iRequestTimeOut) {
                                        htReceive.put(key, txnConn);
                                    }
                                    LOG.debug("Time Gap (毫秒)= " + (System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(key))));
                                    htPairMapping.remove(key);
                                }
                                break;
                            }

                            int funcode = Integer.parseInt(MsgFunCode, 16);
                            LOG.debug("MsgFunCode=" + MsgFunCode);
                            StringBuffer sbTemp = new StringBuffer();
                            switch (funcode) {
                            case 4101:
                                TxnC4101 txn4101 = new TxnC4101(TmlID, MsgSeqNo);
                                txn4101.setReceiveMSG(bbReceive);
                                // txn4101.Deconstruct();
                                // 判斷回來之訊息是否在htPairMapping內，並判斷是否已超過回應時間。

                                sbTemp.append(TmlID)// .append("-").append(MsgSeqNo) //for test 不論什麼SeqNo 都收
                                        // 因4101交易會回傳下一個使用之序號，所以要Mapping需減1
                                        // .append(Integer.parseInt(MsgSeqNo,16)-1)
                                        .append("-4101");
                                if (htPairMapping.containsKey(sbTemp.toString())) {
                                    if ((System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))) <= iRequestTimeOut) {
                                        htReceive.put(sbTemp.toString(), txn4101);
                                    }
                                    LOG.debug("Time Gap (毫秒)= " + (System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))));
                                    htPairMapping.remove(sbTemp.toString());
                                } else {
                                    txn4101.Deconstruct();
                                    TxnTerminal.insertTandemLog(txn4101, null, null, null, TTandemLog.TandemType_G);
                                }

                                break;

                            case 4105:
                                TxnC4105 txn4105 = new TxnC4105(TmlID, MsgSeqNo);
                                txn4105.setReceiveMSG(bbReceive);
                                txn4105.Deconstruct();
                                sbTemp.append(TmlID).append("-").append(MsgSeqNo).append("-4105");
                                htReceive.put(sbTemp.toString(), txn4105);
                                if (htPairMapping.containsKey(sbTemp.toString())) {
                                    if ((System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))) <= iRequestTimeOut) {
                                        htReceive.put(sbTemp.toString(), txn4105);
                                    }
                                    LOG.debug("Time Gap (毫秒)= " + (System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))));
                                    htPairMapping.remove(sbTemp.toString());
                                } else {
                                    txn4105.Deconstruct();
                                    TxnTerminal.insertTandemLog(txn4105, null, null, null, TTandemLog.TandemType_G);
                                }

                                break;

                            case 4404:
                                TxnP4404 txn4404 = new TxnP4404(TmlID, MsgSeqNo);
                                txn4404.setReceiveMSG(bbReceive);
                                // txn4404.Deconstruct();
                                sbTemp.append(TmlID).append("-").append(MsgSeqNo).append("-4404");
                                htReceive.put(sbTemp.toString(), txn4404);
                                if (htPairMapping.containsKey(sbTemp.toString())) {
                                    if ((System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))) <= iRequestTimeOut) {
                                        htReceive.put(sbTemp.toString(), txn4404);
                                    }
                                    LOG.debug("Time Gap (毫秒)= " + (System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))));
                                    htPairMapping.remove(sbTemp.toString());
                                } else {
                                    txn4404.Deconstruct();
                                    TxnTerminal.insertTandemLog(txn4404, null, null, null, TTandemLog.TandemType_G);
                                }

                                break;

                            case 4113:
                                TxnP4113 txn4113 = new TxnP4113(TmlID, MsgSeqNo);
                                txn4113.setReceiveMSG(bbReceive);
                                // txn4113.Deconstruct();
                                sbTemp.append(TmlID).append("-").append(MsgSeqNo).append("-4113");
                                htReceive.put(sbTemp.toString(), txn4113);
                                if (htPairMapping.containsKey(sbTemp.toString())) {
                                    if ((System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))) <= iRequestTimeOut) {
                                        htReceive.put(sbTemp.toString(), txn4113);
                                    }
                                    LOG.debug("Time Gap (毫秒)= " + (System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))));
                                    htPairMapping.remove(sbTemp.toString());
                                } else {
                                    txn4113.Deconstruct();
                                    TxnTerminal.insertTandemLog(txn4113, null, null, null, TTandemLog.TandemType_G);
                                }

                                break;

                            case 4118:
                                TxnP4118 txn4118 = new TxnP4118(TmlID, MsgSeqNo);
                                txn4118.setReceiveMSG(bbReceive);
                                // txn4118.Deconstruct();
                                sbTemp.append(TmlID).append("-").append(MsgSeqNo).append("-4118");
                                htReceive.put(sbTemp.toString(), txn4118);
                                if (htPairMapping.containsKey(sbTemp.toString())) {
                                    if ((System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))) <= iRequestTimeOut) {
                                        htReceive.put(sbTemp.toString(), txn4118);
                                    }
                                    LOG.debug("Time Gap (毫秒)= " + (System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))));
                                    htPairMapping.remove(sbTemp.toString());
                                } else {
                                    txn4118.Deconstruct();
                                    TxnTerminal.insertTandemLog(txn4118, null, null, null, TTandemLog.TandemType_G);
                                }

                                break;

                            case 4411:
                                TxnP4411 txn4411 = new TxnP4411(TmlID, MsgSeqNo);
                                txn4411.setReceiveMSG(bbReceive);
                                // txn4411.Deconstruct();
                                sbTemp.append(TmlID).append("-").append(MsgSeqNo).append("-4411");
                                htReceive.put(sbTemp.toString(), txn4411);
                                if (htPairMapping.containsKey(sbTemp.toString())) {
                                    if ((System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))) <= iRequestTimeOut) {
                                        htReceive.put(sbTemp.toString(), txn4411);
                                    }
                                    LOG.debug("Time Gap (毫秒)= " + (System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))));
                                    htPairMapping.remove(sbTemp.toString());
                                } else {
                                    txn4411.Deconstruct();
                                    TxnTerminal.insertTandemLog(txn4411, null, null, null, TTandemLog.TandemType_G);
                                }

                                break;

                            case 4412:
                                TxnP4412 txn4412 = new TxnP4412(TmlID, MsgSeqNo);
                                txn4412.setReceiveMSG(bbReceive);
                                // txn4412.Deconstruct();
                                sbTemp.append(TmlID).append("-").append(MsgSeqNo).append("-4412");
                                htReceive.put(sbTemp.toString(), txn4412);
                                if (htPairMapping.containsKey(sbTemp.toString())) {
                                    if ((System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))) <= iRequestTimeOut) {
                                        htReceive.put(sbTemp.toString(), txn4412);
                                    }
                                    LOG.debug("Time Gap (毫秒)= " + (System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))));
                                    htPairMapping.remove(sbTemp.toString());
                                } else {
                                    txn4412.Deconstruct();
                                    TxnTerminal.insertTandemLog(txn4412, null, null, null, TTandemLog.TandemType_G);
                                }

                                break;

                            case 4501:
                                TxnP4501 txn4501 = new TxnP4501(TmlID, MsgSeqNo);
                                txn4501.setReceiveMSG(bbReceive);
                                // txn4501.Deconstruct();
                                sbTemp.append(TmlID).append("-").append(MsgSeqNo).append("-4501");
                                htReceive.put(sbTemp.toString(), txn4501);
                                if (htPairMapping.containsKey(sbTemp.toString())) {
                                    if ((System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))) <= iRequestTimeOut) {
                                        htReceive.put(sbTemp.toString(), txn4501);
                                    }
                                    LOG.debug("Time Gap (毫秒)= " + (System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))));
                                    htPairMapping.remove(sbTemp.toString());
                                } else {
                                    txn4501.Deconstruct();
                                    TxnTerminal.insertTandemLog(txn4501, null, null, null, TTandemLog.TandemType_G);
                                }

                                break;

                            case 4801:
                                TxnC4801 txn4801 = new TxnC4801(TmlID, MsgSeqNo);
                                txn4801.setReceiveMSG(bbReceive);
                                txn4801.Deconstruct();
                                // sbTemp.append(TmlID).append("-").append(MsgSeqNo).append("-4801");
                                // 因Terminal不知HOST的MsgSeqNo，所以用TmlID + EkeyModifier_R + 4801 當做Receive的KEY;
                                // sbTemp.append(TmlID).append("-").append(txn4801.getEkeyModifier_R()).append("-4801");
                                sbTemp.append(TmlID).append("-0-4801");
                                if (!htReceive.containsKey(sbTemp.toString())) {
                                    htReceive.put(sbTemp.toString(), txn4801);
                                    htPairMapping.put(sbTemp.toString(), getTimeMillis());
                                } else {
                                    // 睡一下，再給Txn模組一些時間，等它把上一個4801收完並處理。
                                    try {
                                        Thread.sleep(500);
                                    } catch (Exception e) {
                                    }
                                    if (!htReceive.containsKey(sbTemp.toString())) {
                                        htReceive.put(sbTemp.toString(), txn4801);
                                        htPairMapping.put(sbTemp.toString(), getTimeMillis());
                                    } else
                                        LOG.error("Previously Receive 4801 haven't complete");
                                }

                                break;

                            case 4802:
                                TxnC4802 txn4802 = new TxnC4802(TmlID, MsgSeqNo);
                                txn4802.setReceiveMSG(bbReceive);
                                txn4802.Deconstruct();
                                // sbTemp.append(TmlID).append("-").append(MsgSeqNo).append("-4802");
                                // 因Terminal不知HOST的MsgSeqNo，所以用TmlID + 4802 當做Receive的KEY;
                                sbTemp.append(TmlID).append("-0-4802");
                                if (!htReceive.containsKey(sbTemp.toString())) {
                                    htReceive.put(sbTemp.toString(), txn4802);
                                    htPairMapping.put(sbTemp.toString(), getTimeMillis());
                                } else {
                                    // 睡一下，再給Txn模組一些時間，等它把上一個4802收完並處理。
                                    try {
                                        Thread.sleep(500);
                                    } catch (Exception e) {
                                    }
                                    if (!htReceive.containsKey(sbTemp.toString())) {
                                        htReceive.put(sbTemp.toString(), txn4802);
                                        htPairMapping.put(sbTemp.toString(), getTimeMillis());
                                    } else
                                        LOG.error("Previously Receive 4802 haven't complete");
                                }

                                break;

                            case 31077:
                                TxnC31077 txn31077 = new TxnC31077(TmlID, MsgSeqNo);
                                txn31077.setReceiveMSG(bbReceive);
                                // txn31077.setReceiveMSG_HEX(new String (Misc.bin2Hex(bbReceive.array())));
                                // System.out.println("31077-1-"+new String (Misc.bin2Hex(bbReceive.array())));
                                // txn31077.Deconstruct();
                                sbTemp.append(TmlID).append("-").append(MsgSeqNo).append("-31077");
                                if (htPairMapping.containsKey(sbTemp.toString())) {
                                    if ((System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))) <= iRequestTimeOut) {
                                        htReceive.put(sbTemp.toString(), txn31077);
                                    }
                                    LOG.debug("Time Gap (毫秒)= " + (System.currentTimeMillis() - Long.parseLong((String) htPairMapping.get(sbTemp.toString()))));
                                    htPairMapping.remove(sbTemp.toString());
                                } else {
                                    txn31077.Deconstruct();
                                    TxnTerminal.insertTandemLog(txn31077, null, null, null, TTandemLog.TandemType_G);
                                }
                                break;
                            }
                            sbTemp = null;
                        } else if (sTempFC.equals(KeepAliveFC)) {
                            LOG.info("Host KeepAlive");
                            setKeepAliveCounter(MaxKeepAliveCounter);
                            // iKeepAliveCounter = MaxKeepAliveCounter;
                        } else {
                            LOG.error("unKnown Data Message Format code");
                            LOG.error("must to disConnect and reConnect");
                        }

                        lLastTranTimeMillis = System.currentTimeMillis();
                    } else if (key.isConnectable()) {
                        client = (SocketChannel) key.channel();
                        if (client.isConnectionPending()) {
                            try {
                                isConnect = client.finishConnect();
                                // System.out.println("finishConnect="+client.finishConnect());
                                LOG.debug(client.toString());
                                LOG.info("connectable");
                                client.register(selector, SelectionKey.OP_WRITE);
                                lLastTranTimeMillis = System.currentTimeMillis();
                                Thread.sleep(2000);
                                setKeepAliveCounter(MaxKeepAliveCounter);
                                // iKeepAliveCounter = MaxKeepAliveCounter;
                                if (isReConnectFlag) {
                                    isReConnectFlag = false;
                                } else {
                                    isReConnectFlag = true;
                                }
                            } catch (ConnectException ce) {
                                LOG.debug("Connection refused");
                                bbSend = null;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        // System.out.println(client.toString());
                        // System.out.println("connectable");
                        // client.register(selector, SelectionKey.OP_WRITE);
                        // lLastTranTimeMillis = System.currentTimeMillis();

                        // isConnect = true;
                    }
                } catch (ClosedChannelException cce) {
                    cce.printStackTrace();
                    if (iConnectCounter == -1) {
                        isConnect = doConnect();
                    } else if (iConnectCounter != 0) {
                        isConnect = doConnect();
                        if (isConnect)
                            iConnectCounter = MaxConnectCounter;
                        else
                            iConnectCounter--;
                    } else if (iConnectCounter == 0) {
                        // isConnect = false;
                        isShutdown = true;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    bbSend = null;
                    if (iConnectCounter == -1) {
                        isConnect = doConnect();
                    } else if (iConnectCounter != 0) {
                        // isConnect = doConnect();
                        isConnect = doConnect();
                        if (isConnect)
                            iConnectCounter = MaxConnectCounter;
                        else
                            iConnectCounter--;
                    } else {
                        // isConnect = false;
                        isShutdown = true;
                        key.cancel();
                        try {
                            key.channel().close();
                        } catch (IOException cex) {
                            cex.printStackTrace();
                        }
                    }
                    /*
                     * key.cancel( ); isConnect = false; try { key.channel().close( ); } catch (IOException cex) { cex.printStackTrace(); }
                     */
                }
            }
        }
        try {
            isConnect = false;
            key.cancel();
            selector.close();
            key.channel().close();
            // client.close();
        } catch (IOException cex) {
            cex.printStackTrace();
            LOG.debug(cex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.debug(ex.getMessage());
        }
        LOG.info("TcpClient Shutdown!!");
        return;
    }

    public void init() {
        // 取得參數
        iRequestTimeOut = 93000;// Integer.parseInt((String)APSystem.getSYS_PRAM_MAP().get("SocketTimeoutSEC")) * 1000;
    }

    public void starClientThread() {
        ThreadObj = new Thread(this, "ClientThread");
        ThreadObj.start();

    }

    public void shutdownClientThread() {
        ThreadObj.destroy();
        ThreadObj = null;
    }

    public boolean getConnectStatus() {
        return isConnect;
    }

    public void doSend() {
        synchronized (mutex) {
            if (!isShutdown) {
                try {
                    key = client.register(selector, SelectionKey.OP_WRITE);
                } catch (ClosedChannelException cce) {
                    cce.printStackTrace();
                    // ClosedChannelException
                    //
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            } else {
                LOG.error("目前系統尚未連線");
            }
        }
    }

    public void doReceive() {
        synchronized (mutex) {
            if (!isShutdown) {
                try {
                    key = client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT);
                } catch (ClosedChannelException cce) {
                    // ClosedChannelException
                    //
                    cce.printStackTrace();
                    if (iConnectCounter == -1) {
                        isConnect = doConnect();
                    } else if (iConnectCounter != 0) {
                        boolean isConnectRtn = doConnect();
                        if (isConnectRtn)
                            iConnectCounter = MaxConnectCounter;
                        else
                            iConnectCounter--;
                    } else if (iConnectCounter == 0) {
                        isConnect = false;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            } else {
                LOG.error("目前系統尚未連線");
            }
        }
    }

    public void dumpPairMapping() {
        for (Iterator it = htPairMapping.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            LOG.debug("key =[" + key + "],value=[" + value + "]");
        }
    }

    private static String getTimeMillis() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * @param args
     *            Creation date:(2008/7/21 上午 10:40:28)
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Hashtable htSend = new Hashtable();
        Hashtable htReceive = new Hashtable();

        TcpClient tc = new TcpClient(htSend, htReceive);
        tc.starClientThread();

        try {
            // Thread.sleep(3000);
            TxnC31077 txn = new TxnC31077("8614", "4294967295");

            htSend.put(txn.getTmlID() + "-" + txn.getMsgSeqNo() + "-31077", txn);

            Thread.sleep(200);

            // htSend.put("8614-4294967295-31077", txn);
            // tc.doSend();

            Thread.sleep(100);
            TxnC31077 txn_receive = (TxnC31077) htReceive.get(txn.getTmlID() + "-" + txn.getMsgSeqNo() + "-31077");

            System.out.println(txn_receive.getTimestamp_R());

            System.out.println("=======================================");
            System.out.println("=======================================");
            System.out.println("=======================================");

            TxnC4101 txn4101 = new TxnC4101("8614", "0");
            htSend.put(txn4101.getTmlID() + "-" + txn4101.getMsgSeqNo() + "-4101", txn4101);

            tc.doSend();
            Thread.sleep(200);

            TxnC4101 txn4101_receive = (TxnC4101) htReceive.get(txn4101.getTmlID() + "-" + txn4101.getMsgSeqNo() + "-4101");
            txn4101_receive.Deconstruct();
            System.out.println("Update MsgSeqNo -->" + txn4101_receive.getMsgSeqNo_R());

            System.out.println("=======================================");
            System.out.println("=======================================");
            System.out.println("=======================================");

            Thread.sleep(200);

            TxnC4801 txn4801_receive = (TxnC4801) htReceive.get("8614-1-4801");
            System.out.println(txn4801_receive.getEkeyDLenData_R());

            TxnC4801 txn4801_1 = new TxnC4801("8614", "1");
            // htSend.put("8614-2-4801", txn4801_1);
            htSend.put(txn4801_1.getTmlID() + "-" + txn4801_1.getMsgSeqNo() + "-4801", txn4801_1);

            tc.doSend();

            System.out.println("=======================================");
            System.out.println("=======================================");
            System.out.println("=======================================");

            Thread.sleep(200);

            txn4801_receive = (TxnC4801) htReceive.get("8614-2-4801");
            System.out.println(txn4801_receive.getEkeyDLenData_R());

            TxnC4801 txn4801_2 = new TxnC4801("8614", "2");
            htSend.put(txn4801_2.getTmlID() + "-" + txn4801_2.getMsgSeqNo() + "-4801", txn4801_2);
            tc.doSend();

            Thread.sleep(200);

            TxnC4802 txn4802_receive = (TxnC4802) htReceive.get("8614-3-4802");
            // System.out.println(txn4802_receive.getVersNumber_R());

            TxnC4802 txn4802 = new TxnC4802("8614", "3");
            // htSend.put(txn4802_receive.getTmlID()+"-"+txn4802_receive.getMsgSeqNo()+"-4802", txn4802);
            // tc.doSend();

            Thread.sleep(200);
            tc.dumpPairMapping();
            Thread.sleep(1000);
            // tc.dumpPairMapping();
            System.out.println("done");

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }
}