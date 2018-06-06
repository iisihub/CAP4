/*
 * @(#)TerminalPool.java
 *
 * Copyright (c) 2008 Citi Group Incorporated. All rights reserved.
 *
 * Modify History:
 *  v1.00, 2008/07/07, Kevin Chung
 *   1) First release
 *
 */
package tw.com.citi.webatm.webap.signaler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citi.webatm.rmi.TTmlInfo;

import tw.com.citi.webatm.txn.TxnTerminal;

/**
 * This pool is store terminal(include keychangeTml,txnTml) when server startup all terminal need to put into this pool follow-up use getTml and returnTml to exec Txn other main function is detect
 * TcpClient's connection Status for each Tml, and if detect reconnect again then run hankshaking process.
 **/
public class TerminalPool implements Runnable {

    // log4J category
    static Logger LOG = LoggerFactory.getLogger("TerminalPool");

    private static boolean isShutdown = false;
    private Hashtable hmTxnTmlPool;
    private ArrayList alTmlID;
    Hashtable hmSend;
    Hashtable hmReceive;
    TxnTerminal txntml_HandShaking = null;
    private static Thread ThreadObj = null;

    private final static Object mutex = new Object();

    public TerminalPool(Hashtable send, Hashtable receive) {
        hmSend = send;
        hmReceive = receive;
    }

    /**
     * for HandShaking Terminal
     **/
    public void initTerminalPool(String tmlid, TcpClient tc) {
        if (hmTxnTmlPool != null)
            return;
        TxnTerminal txnTml = new TxnTerminal(tmlid, tc, hmSend, hmReceive);
        hmTxnTmlPool.put(tmlid, txnTml);
    }

    /**
     * for Trns Terminal
     **/
    public void initTerminalPool(ArrayList TmlID, TcpClient tc) {
        if (hmTxnTmlPool != null)
            return;
        alTmlID = TmlID;
        int alSize = alTmlID.size();
        String tmlid = null;
        hmTxnTmlPool = new Hashtable(alSize);

        for (int index = 0; index < alSize; index++) {
            tmlid = (String) alTmlID.get(index);
            TxnTerminal txnTml = new TxnTerminal(tmlid, tc, hmSend, hmReceive);
            hmTxnTmlPool.put(tmlid, txnTml);
        }
        /*
         * for (int index = 0; index < alSize; index++) { tmlid = (String)alTmlID.get(index); ((TxnTerminal)hmTxnTmlPool.get(tmlid)).doTxnP4501(); }
         */
    }

    /**
     * get first unUse Terminal when it's free and complete initial process
     * 
     **/
    public TxnTerminal getTerminal() {

        TxnTerminal txntml = null;
        /*
         * Enumeration tmlIDs = hmTxnTmlPool.keys();
         * 
         * while (tmlIDs.hasMoreElements()) { String tmlID = (String) tmlIDs.nextElement(); txntml = (TxnTerminal)hmTxnTmlPool.get(tmlID); System.out.println(txntml.getTmlID()); //return
         * (TxnTerminal)hmTxnTmlPool.get(tmlID); }
         */

        /*
         * remark 2009.07.15 [get id by sequel] synchronized(mutex) { int alSize = alTmlID.size(); String tmlid = null; for (int index = 0; index < alSize; index++) { tmlid =
         * (String)alTmlID.get(index); txntml =((TxnTerminal)hmTxnTmlPool.get(tmlid)); if (txntml.isInitHandShaking() && txntml.isFree() && txntml.isInit()) {
         * LOG.debug("LockTerminal-"+txntml.getTmlID()); txntml.LockTml(); return txntml; } } }
         */

        synchronized (mutex) {
            int alSize = alTmlID.size();
            String tmlid = null;
            // for (int index = 0; index < alSize; index++) {
            Random randomId = new Random();
            int index = randomId.nextInt(alSize);
            tmlid = (String) alTmlID.get(index);
            txntml = ((TxnTerminal) hmTxnTmlPool.get(tmlid));
            if (txntml.isInitHandShaking() && txntml.isFree() && txntml.isInit()) {
                LOG.debug("LockTerminal-" + txntml.getTmlID());
                txntml.LockTml();
                return txntml;
            }
            // }
        }

        return null;
    }

    /**
     * get Terminal when it's free and complete initial process
     * 
     **/
    public TxnTerminal getTerminal(String tmlid) {
        TxnTerminal txntml = null;

        synchronized (mutex) {
            txntml = ((TxnTerminal) hmTxnTmlPool.get(tmlid));
            if (txntml.isFree() && txntml.isInit()) {
                LOG.debug("LockTerminal-" + txntml.getTmlID());
                txntml.LockTml();
                return txntml;
            }
        }
        return null;
    }

    /**
     * return Terminal when that won't use it anymore
     * 
     **/
    public void returnTerminal(TxnTerminal txntml) {
        synchronized (mutex) {
            txntml.FreeTml();
            // clean value for next transaction
            txntml.setMsgSeqNo(null);
            txntml.setIssuerBankCode(null);
            txntml.setIssuerAccount(null);
            txntml.setPCode(null);
            LOG.debug("FreeTerminal-" + txntml.getTmlID());
        }
    }

    /**
     * return Terminal when that won't use it anymore
     * 
     **/
    public boolean returnTerminal(String rtnTmlid) {
        TxnTerminal txntml = null;

        synchronized (mutex) {
            int alSize = alTmlID.size();
            String tmlid = null;
            for (int index = 0; index < alSize; index++) {
                tmlid = (String) alTmlID.get(index);
                txntml = ((TxnTerminal) hmTxnTmlPool.get(tmlid));
                if (txntml.isInitHandShaking() && (txntml.getTmlID().equals(rtnTmlid)) && !txntml.isFree() && txntml.isInit()) {
                    txntml.FreeTml();
                    // clean value for next transaction
                    txntml.setMsgSeqNo(null);
                    txntml.setIssuerBankCode(null);
                    txntml.setIssuerAccount(null);
                    txntml.setPCode(null);
                    LOG.debug("FreeTerminal-" + txntml.getTmlID());
                    return true;
                }
            }
        }
        return false;
    }

    public void addHandShaking_Tml(TTmlInfo tmlInfo, TcpClient tc) {
        txntml_HandShaking = new TxnTerminal(tmlInfo.getTmlID().trim(), tc, hmSend, hmReceive);
    }

    public TxnTerminal getHandShaking_Tml() {
        return txntml_HandShaking;
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
     * new thread detect TcpClient Status.. then update HandShaking Tml status and follow up process
     **/
    public void run() {
        while (!isShutdown) {
            if (txntml_HandShaking.getTC_isConnect()) {
                // System.out.println(txntml_HandShaking.getTC_iKeepAliveCounter());
            }
            // System.out.println(txntml_HandShaking.getTC_iKeepAliveCounter());
            if (txntml_HandShaking.getTC_iKeepAliveCounter() == 0) {
                txntml_HandShaking.setStopTrns(true);
                if (txntml_HandShaking.getTC_isConnect()) {
                    // String MsgSeqNo = TxnTerminal.getTml_SeqNo(txntml_HandShaking.getTmlID());
                    // txntml_HandShaking.setMsgSeqNo(MsgSeqNo);
                    if (txntml_HandShaking.doTimeSync()) {
                        if (txntml_HandShaking.initKeyExchange()) {
                            txntml_HandShaking.setStopTrns(false);
                        }
                    }
                }
            } else if ((txntml_HandShaking.getTC_isReConnectFlag())) {
                txntml_HandShaking.setTC_isReConnectFlag(false);
                txntml_HandShaking.setStopTrns(true);
                if (txntml_HandShaking.getTC_isConnect()) {
                    // String MsgSeqNo = TxnTerminal.getTml_SeqNo(txntml_HandShaking.getTmlID());
                    // txntml_HandShaking.setMsgSeqNo(MsgSeqNo);
                    if (txntml_HandShaking.doTimeSync()) {
                        if (txntml_HandShaking.initKeyExchange()) {
                            txntml_HandShaking.setStopTrns(false);
                        }
                    }
                }
            }
            try {
                // 間隔需大於做完所有HandShaking 之時間。
                Thread.sleep(5000);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        LOG.debug("DetectReConnectThread Shutdown!!");
    }

    public void starDetectReConnectThread() {
        ThreadObj = new Thread(this, "DetectReConnectThread");
        ThreadObj.start();
    }

    public void shutdownDetectReConnectThread() {
        ThreadObj.destroy();
        ThreadObj = null;
    }

    /**
     * @param args
     *            Creation date:(2008/7/30 上午 10:11:02)
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Hashtable hmSend = new Hashtable();
        Hashtable hmReceive = new Hashtable();

        ArrayList alTmlID = new ArrayList();
        alTmlID.add("8614");
        alTmlID.add("8615");
        alTmlID.add("8616");
        alTmlID.add("8617");
        alTmlID.add("8618");
        alTmlID.add("8619");
        alTmlID.add("8620");
        alTmlID.add("8621");
        alTmlID.add("8622");
        alTmlID.add("8623");
        alTmlID.add("8624");
        alTmlID.add("8625");
        alTmlID.add("8626");
        alTmlID.add("8627");
        alTmlID.add("8628");
        alTmlID.add("8629");
        TerminalPool tp = new TerminalPool(hmSend, hmReceive);

        TcpClient tc = new TcpClient("172.18.92.35", 9000, hmSend, hmReceive);
        tc.starClientThread();
        tp.initTerminalPool(alTmlID, tc);
        TxnTerminal txn;
        txn = tp.getTerminal();
        // txn.doTxnP4501();
        // tp.returnTerminal(txn);
    }

}
