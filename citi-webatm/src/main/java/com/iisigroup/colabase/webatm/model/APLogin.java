/*
 * @(#)APLogin.java
 *
 * Copyright (c) 2008 citi bank Incorporated. All rights reserved.
 *
 * Modify History:
 *  v1.00, 2008/07/18, Kevin Chung
 *   1) First release
 */
package com.iisigroup.colabase.webatm.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.booc.trns.DBConstant;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl;
import com.iisigroup.cap.utils.CapAppContext;

import tw.com.citi.webatm.txn.TxnTerminal;

/**
 * Application user login information & service
 *
 * @author Kevin Chung
 * @version 1.00, 2008/09/22
 */
public class APLogin implements Serializable, DBConstant {
    // final private static long serialVersionUID = 0L;

    // log4J category
    protected final static Logger LOG = LoggerFactory.getLogger("APLogin");
    private APSystemService APSystem = CapAppContext.getApplicationContext().getBean(APSystemServiceImpl.class);
    // session informations
    private String sessionId;
    private String hexSessionId;
    private String issuerBankCode;
    private String issuer_account;
    private String issuer_remark;
    private String atm_no;
    private String atm_checkcode;
    private String issuer_login_ip;
    private String issuer_login_dttm;
    private String issuer_oper_fnct;
    private String issuer_oper_dttm;
    private double issuer_time_limit;

    private String readerName;
    private int reader_type;
    private String keyboard_type;
    private String portal;

    private String Rand1;
    private String Rand1mac;
    private String Rand2;
    private String Rand2mac;

    private String AUTHCODE;

    private HashMap ReqData; // for keep form value
    private String Citi_CARD;
    private String HAS_VERIFY;

    private String CAN_TXNQ;
    private HashMap TXN_QUERY_LIST;

    private String EncMappingTable;
    private String PINIndex;

    // 20060726 Frank：以下變數，預設為 null，只有當需要時才給值！並設成 pivate
    private String[] AccountList = null;

    // trns controls
    // public TrnsParm[] trnsparm = null;

    // bank id
    // public String sbkc;

    // Session control
    private long timeoutMsec = 0; // The time out second
    private long loginTime = 0; // The login time in System Millis
    private long accessTime = 0; // The last access time in System Millis

    // Pool for store process result Objects as attribute.
    private TreeMap attribPool = null;

    private TxnTerminal txnTml = null;

    /**
     * Constructs with the specified error message.
     *
     * @param login
     *            the login user.
     */
    public APLogin() {
    }

    /**
     * Constructs with the specified error message.
     *
     * @param login
     *            the login user.
     */
    public APLogin(String session_id) {

        // Set the login user informations
        this.sessionId = session_id;

        // Initial object pool for store attriutes.
        this.attribPool = new TreeMap();

    }

    public void destroyObject() {
        AccountList = null;
        // trnsparm = null;
        ReqData = null;
        attribPool = null;
    }

    /**
     * put process result attribute to attribPool.
     */
    public void put(String key, Object obj) {
        if (attribPool != null) {
            if (this.attribPool.containsKey(key)) {
                this.attribPool.remove(key);
            }
            this.attribPool.put(key, obj);
        }
    }

    /**
     * get process result attribute from attribPool.
     */
    public Object get(String key) {
        if (attribPool != null) {
            if (!this.attribPool.containsKey(key)) {
                return null;
            }
            return this.attribPool.get(key);
        } else
            return null;
    }

    /**
     * clear the attribPool if it is not empty.
     */
    public void clear() {
        if (!this.attribPool.isEmpty()) {
            attribPool.clear();
        }
    }

    /**
     * get Issuser Session ID.
     */
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * set Issuser Session ID.
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getHexSessionId() {
        return hexSessionId;
    }

    public void setHexSessionId(String hexSessionId) {
        this.hexSessionId = hexSessionId;
    }

    /**
     * get Issuser ID.
     */
    public String getIssuerBankCode() {
        return this.issuerBankCode;
    }

    /**
     * set Issuser ID.
     */
    public void setIssuerBankCode(String issuerId) {
        this.issuerBankCode = issuerId;
    }

    /**
     * get Issuser ID.
     */
    public String getIssuerAccount() {
        return this.issuer_account;
    }

    /**
     * set Issuser ID.
     */
    public void setIssuerAccount(String issuerAccount) {
        this.issuer_account = issuerAccount;
    }

    /**
     * get Reader name.
     */
    public String getReaderName() {
        return this.readerName;
    }

    /**
     * set Reader name.
     */
    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    /**
     * get ATM No.
     */
    public String getTmlID() {
        return this.atm_no;
    }

    /**
     * set ATM No.
     */
    public void setTmlID(String atmNo) {
        this.atm_no = atmNo;
    }

    /**
     * get ATM CheckCode.
     */
    public String getAtmCheckCode() {
        return this.atm_checkcode;
    }

    /**
     * set ATM CheckCode.
     */
    public void setAtmCheckCode(String checkCode) {
        this.atm_checkcode = checkCode;
    }
    
    public String getIssuerLoginDttm() {
        return issuer_login_dttm;
    }

    /**
     * set Issuer Login DateTime.
     */
    public void setIssuerLoginDttm(String loginDttm) {
        this.issuer_login_dttm = loginDttm;
        this.loginTime = Long.parseLong(loginDttm);
    }

    /**
     * set ATM Issuer Operation Fnction and DataTime.
     */
    public void setIssuerOperation(String operFnct, String operDttm) {
        this.issuer_oper_fnct = operFnct;
        this.issuer_oper_dttm = operDttm;
    }

    /**
     * set ATM Issuer Time Out.
     */
    public void setIssuerTimeOut(double timeout) {
        this.issuer_time_limit = timeout;
        setTimeOut((int) timeout);
    }

    /**
     * @return Returns the issuer_login_ip.
     */
    public String getIssuerIP() {
        return issuer_login_ip;
    }

    /**
     * @param issuer_login_ip
     *            The issuer_login_ip to set.
     */
    public void setIssuerIP(String issuer_login_ip) {
        this.issuer_login_ip = issuer_login_ip;
    }

    /**
     * Flag is in processing
     */
    public void enter() {
        LOG.debug("enter");
        // this.accessFlag = 1;
        this.accessTime = System.currentTimeMillis();
    }

    /**
     * Flag is exit process
     */
    public void exit() {
        // this.accessFlag = 0;
        this.accessTime = System.currentTimeMillis();
    }

    /**
     * Set time out second
     *
     * @param tosec
     *            Time out seconds
     */
    public void setTimeOut(int tosec) {
        this.timeoutMsec = 1000 * tosec;
    }

    /**
     * Check if the time out sec reached
     *
     * @return true if time out.
     */
    public boolean isTimeOut() {
        // Is no time checking ?
        if (this.timeoutMsec == 0) {
            return false;
        }

        // Compare with current time
        long pass = System.currentTimeMillis() - this.accessTime;
        if (pass > this.timeoutMsec) {
            return true;
        }
        return false;
    }

    /**
     * Return the login time in String
     *
     * @return the login time.
     */
    public String getLoginTime() {
        return this.dttmFormat(this.loginTime);
    }

    /**
     * Return the last access in String
     *
     * @return the last access time.
     */
    public String getAccessTime() {
        return this.dttmFormat(this.accessTime);
    }

    /**
     * Return the date time in String of "yyyy/MM/dd HH:mm:ss" format
     *
     * @param dttm
     *            the date time in long for convert
     * @return the date time String.
     */
    private String dttmFormat(long dttm) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(new Date(dttm));
    }

    /**
     * check trrn controls is lock
     *
     * @param trnscode
     *            the trns control code
     * @return the trns control name.
     */
    public boolean isTrnsLock(String trnscode) {
        String sTemp = null;
        sTemp = (String) APSystem.getSYS_PRAM_MAP().get(trnscode);
        if (sTemp != null && sTemp.equals("0")) {
            return true;
        }
        return false;
    }

    /**
     * Return trns control name.
     *
     * @param trnscode
     *            the trns control code
     * @return the trns control name.
     */
    public String getTrnsName(String trnscode) {
        String trnsname = ((String[]) APSystem.getValue("PROC_CODE", trnscode))[0];
        if (trnsname != null)
            return trnsname;
        else
            return "";
        /*
         * for(int i=0; i<trnsparm.length; i++){ if(trnsparm[i].parm_code.equalsIgnoreCase(trnscode)){ trnsname = trnsparm[i].parm_name; break; } } return trnsname;
         */
    }

    /**
     * Return bank id
     * 
     * @return the bank id.
     */
    // public String getSBKC() {
    // return sbkc;
    // }

    /**
     * @return Returns the accountList.
     */
    public String[] getAccountList() {
        return AccountList;
    }

    /**
     * @param accountList
     *            The accountList to set.
     */
    public void setAccountList(String[] accountList) {
        AccountList = accountList;
    }

    /**
     * @return Returns the issuer_remark.
     */
    public String getIssuer_remark() {
        return issuer_remark;
    }

    /**
     * @param issuer_remark
     *            The issuer_remark to set.
     */
    public void setIssuer_remark(String issuer_remark) {
        this.issuer_remark = issuer_remark;
    }

    /**
     * @return Returns the reader_type.
     */
    public int getReader_type() {
        return reader_type;
    }

    /**
     * @param reader_type
     *            The reader_type to set.
     */
    public void setReader_type(int reader_type) {
        this.reader_type = reader_type;
    }

    /**
     * @return Returns the portal.
     */
    public String getPortal() {
        return portal;
    }

    /**
     * @param portal
     *            The portal to set.
     */
    public void setPortal(String portal) {
        this.portal = portal;
    }

    /**
     * @return Returns the rand1.
     */
    public String getRand1() {
        return Rand1;
    }

    /**
     * @param rand1
     *            The rand1 to set.
     */
    public void setRand1(String rand1) {
        Rand1 = rand1;
    }

    /**
     * @return Returns the rand1mac.
     */
    public String getRand1mac() {
        return Rand1mac;
    }

    /**
     * @param rand1mac
     *            The rand1mac to set.
     */
    public void setRand1mac(String rand1mac) {
        Rand1mac = rand1mac;
    }

    /**
     * @return Returns the rand2.
     */
    public String getRand2() {
        return Rand2;
    }

    /**
     * @param rand2
     *            The rand2 to set.
     */
    public void setRand2(String rand2) {
        Rand2 = rand2;
    }

    /**
     * @return Returns the rand2mac.
     */
    public String getRand2mac() {
        return Rand2mac;
    }

    /**
     * @param rand2mac
     *            The rand2mac to set.
     */
    public void setRand2mac(String rand2mac) {
        Rand2mac = rand2mac;
    }

    /**
     * @return Returns the aUTHCODE.
     */
    public String getAUTHCODE() {
        return AUTHCODE;
    }

    /**
     * @param authcode
     *            The aUTHCODE to set.
     */
    public void setAUTHCODE(String authcode) {
        AUTHCODE = authcode;
    }

    /**
     * @return Returns the reqData.
     */
    public HashMap getReqData() {
        return ReqData;
    }

    /**
     * @param reqData
     *            The reqData to set.
     */
    public void setReqData(HashMap reqData) {
        ReqData = reqData;
    }

    /**
     * @return Returns the bOOC_CARD.
     */
    public String getCiti_CARD() {
        return Citi_CARD;
    }

    /**
     * @param booc_card
     *            The bOOC_CARD to set.
     */
    public void setCiti_CARD(String booc_card) {
        Citi_CARD = booc_card;
    }

    /**
     * @return Returns the hAS_VERIFY.
     */
    public String getHAS_VERIFY() {
        return HAS_VERIFY;
    }

    /**
     * @param has_verify
     *            The hAS_VERIFY to set.
     */
    public void setHAS_VERIFY(String has_verify) {
        HAS_VERIFY = has_verify;
    }

    /**
     * @return Returns the cAN_TXNQ.
     */
    public String getCAN_TXNQ() {
        return CAN_TXNQ;
    }

    /**
     * @param can_txnq
     *            The cAN_TXNQ to set.
     */
    public void setCAN_TXNQ(String can_txnq) {
        CAN_TXNQ = can_txnq;
    }

    /**
     * @return Returns the tXN_QUERY_LIST.
     */
    public HashMap getTXN_QUERY_LIST() {
        return TXN_QUERY_LIST;
    }

    /**
     * @param txn_query_list
     *            The tXN_QUERY_LIST to set.
     */
    public void setTXN_QUERY_LIST(HashMap txn_query_list) {
        TXN_QUERY_LIST = txn_query_list;
    }

    /**
     * @return Returns the keyboard_type.
     */
    public String getKeyboard_type() {
        return keyboard_type;
    }

    /**
     * @param keyboard_type
     *            The keyboard_type to set.
     */
    public void setKeyboard_type(String keyboard_type) {
        this.keyboard_type = keyboard_type;
    }

    /**
     * @return Returns the txnTml.
     */
    public TxnTerminal getTxnTml() {
        return txnTml;
    }

    /**
     * @param txnTml
     *            The txnTml to set.
     */
    public void setTxnTml(TxnTerminal txnTml) {
        this.txnTml = txnTml;
    }

    /**
     * @return Returns EncMappingTable.
     */
    public String getEncMappingTable() {
        return EncMappingTable;
    }

    public void setEncMappingTable(String encMappingTable) {
        EncMappingTable = encMappingTable;
    }

    /**
     * @return Returns PINIndex.
     */
    public String getPINIndex() {
        return PINIndex;
    }

    public void setPINIndex(String pINIndex) {
        PINIndex = pINIndex;
    }
}
