/* 
 * C0100Handler.java
 * 
 * Copyright (c) 2009-2017 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.webatm.handler;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.citi.utils.Misc;
import com.iisigroup.colabase.webatm.model.APLogin;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl;
import com.iisigroup.colabase.webatm.common.CCConstants;
import com.iisigroup.colabase.webatm.toolkit.CommonCryptoUtils;
import com.hitrust.trustatmtrns.RMIAPException;
import com.hitrust.trustatmtrns.util.DateUtil;
import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.utils.CapAppContext;

import tw.com.citi.webatm.txn.TxnProcess01;
import tw.com.citi.webatm.txn.TxnTerminal;
import tw.com.citi.ws.hsm.WSSecurity;

import static com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl.WSS_3DES_KEY;
import static com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl.WSS_3DES_TYPE;
import static com.iisigroup.colabase.webatm.common.CCConstants.RAND_KEYPAD_LIST;
import static com.iisigroup.colabase.webatm.common.CCConstants.SESSION_ATTRIB;
import static com.iisigroup.colabase.webatm.common.CCConstants.SESSION_CAN_ACCESS_TO_NEXTPAGE;
import static com.iisigroup.colabase.webatm.common.CCConstants.WSS_CHK_CODE;
import static com.iisigroup.colabase.webatm.common.CCConstants.WSS_CHK_USR;
import static com.iisigroup.colabase.webatm.common.CCConstants.WSS_SYS_ID;

/**
 * <pre>
 * 餘額交易
 * </pre>
 * 
 * @since 2017年7月13日
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2017年7月13日,Sunkist Wang,new
 *          </ul>
 */
@Controller("democ0100handler")
public class C0100Handler extends GenericControl {
    @Autowired
    private APSystemService APSystem;
    /**
     * C0100 process
     * 
     * @param request
     * @return Result
     * @throws RMIAPException
     * @throws CapMessageException
     */
    @HandlerType(name = "TXN")
    public Result process(Request request) throws CapMessageException, RMIAPException {
        AjaxFormResult result = new AjaxFormResult();

        APLogin login = (APLogin) request.getObject("_login");
        String operation = (String) request.getObject("_operation");
        String function = (String) request.getObject("_function");

        String LogHeader = "IssuerAccount:" + login.getIssuerAccount() + ",TmlID:" + login.getTmlID();

        // Processing

        if (operation.equalsIgnoreCase("Q")) {

        	 HashMap<String,Object> DataHashMap = this.Query(request, login, operation, LogHeader);
        	 result.putAll(DataHashMap);

        } else if (operation.equalsIgnoreCase("I")) {

            this.Inquiry(request, login, operation, LogHeader);

        } else {

            // Unknow operation

            if (logger.isWarnEnabled()) {
                logger.warn("No such operation - " + operation);
            }
            result.set("_error", CapAppContext.getMessage("SYS07"));
            return result;
        }

        return result;
    }

    /**
     * operation : Q
     * 
     * @param request
     * @return
     * @throws CapMessageException
     * @throws RMIAPException
     */
    @HandlerType(name = "TXN")
    public Result query(Request request) throws CapMessageException, RMIAPException {
    	AjaxFormResult result = new AjaxFormResult();
        HttpSession session = getSession(request);
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
        String LogHeader = "IssuerAccount:" + login.getIssuerAccount() + ",TmlID:" + login.getTmlID();
        HashMap<String,Object> DataHashMap = this.Query(request, login, "Q", LogHeader);
        result.putAll(DataHashMap);
        return result;
    }

    /**
     * operation: I
     * 
     * @param request
     * @return
     */
    @HandlerType(name = "TXN")
    public Result inquiry(Request request) {
        AjaxFormResult result = new AjaxFormResult();

        HttpSession session = getSession(request);
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
        String LogHeader = "IssuerAccount:" + login.getIssuerAccount() + ",TmlID:" + login.getTmlID();
        HashMap<String,Object> DataHashMap = this.Inquiry(request, login, "I", LogHeader);
        result.putAll(DataHashMap);
        return result;
    }

    /**
     * 
     * 負責將該操作記錄於DB(IssuerLog)中，並顯示該操作功能的頁面
     * 
     * @param request
     *            Request
     * 
     * @param login
     *            APLogin物件
     * @throws RMIAPException
     * 
     * @exception 利用log4j將Exception寫到Log
     *                File
     * 
     */
    private HashMap<String,Object> Query(Request request, APLogin login, String operation, String LogHeader) throws CapMessageException, RMIAPException {

        // Processing

        String tNow = DateUtil.getCurrentTime("DT", "AD");

        String trnsCode = null;

        HashMap DataHashMap = new HashMap();
        
        HttpSession session = getSession(request);

        try {

            trnsCode = getTrnsCode(login.getIssuerBankCode().substring(0, 3));

            // Check TrnsControl is Lock

            // 餘額查詢 :2500,2590

            if (login.isTrnsLock(trnsCode)) {

                throw new CapMessageException(CapAppContext.getMessage("SERVICE_STOP"), this.getClass());

            }

            Integer TXNProc = null;

            TXNProc = apSystemService.getPCodeProc(login.getReader_type(), trnsCode);

            if (TXNProc.intValue() == CCConstants.READER_TXN_SET_DUMMY) {

                login.put("_needVerifyPIN", "YES");

            } else {

                login.put("_needVerifyPIN", "NO");

            }

            // put operation to IssuerSession

            logger.debug("PCODE:" + trnsCode + ",tNow:" + tNow);

            login.setIssuerOperation(trnsCode, tNow);
            
            //get EncMappingTable
            String EncMappingTable ;
            StringBuffer pintemp = new StringBuffer();
            int[] intIndex = (int[]) session.getAttribute(RAND_KEYPAD_LIST);
            for (int i : intIndex) {
                pintemp = pintemp.append(i);
            }
            String pinIndex = pintemp + "xxxxxx";
            try {
                EncMappingTable = getEncMappingTable(login.getHexSessionId(), pinIndex);

            } catch (Exception e) {
                // TODO: handle exception
                logger.error("Challenge Error:" + e.toString());
                e.printStackTrace();
                throw new CapMessageException(CapAppContext.getMessage("SYS01"), getClass());
            }

            // put result to request

            DataHashMap.put("TRNS_CODE", trnsCode);

            DataHashMap.put("BANK_NAME", getBankName(login.getIssuerBankCode().substring(0, 3)));
            
            DataHashMap.put("SESSION_ID", login.getSessionId());

            DataHashMap.put("ACCOUNT", login.getIssuerAccount());
            
            DataHashMap.put("BANKCODE", login.getIssuerBankCode());
            
            DataHashMap.put("TML_ID", login.getTmlID());
            
            DataHashMap.put("tNow", tNow);
            
            DataHashMap.put("HEX_SESSIONID", login.getHexSessionId());
            
            DataHashMap.put("PIN_INDEX",pinIndex);
            
            DataHashMap.put("ENC_MAPPING_TABLE", EncMappingTable);

            request.put("_result", DataHashMap);

            ArrayList alField = new ArrayList();

            alField.add(login.getSessionId()); // SessionID

            alField.add(login.getIssuerBankCode()); // IssuerBankCode

            alField.add(login.getIssuerAccount()); // IssuerAccount

            alField.add(tNow); // ClientDT

            alField.add(trnsCode + operation); // OperFnct

            alField.add(login.getTmlID()); // TmlID

            alField.add(trnsCode); // PCode

            alField.add("0"); // LockFlag

            if (doInsertIssuerLog(request, operation, alField) == false) {

                throw new CapMessageException(CapAppContext.getMessage("RMI_ERROR"), this.getClass());

            }
           

        } catch (ConnectException ex) {

            String errMessage = CapAppContext.getMessage("RMI_ERROR");

            logger.error(LogHeader + "<ERR> 存取遠端 RMI 錯誤！");

            request.put("_error", errMessage);

            throw new CapMessageException(errMessage, this.getClass());

        } catch (RemoteException ex) {

            String errMessage = CapAppContext.getMessage("RMI_ERROR");

            logger.error(LogHeader + "<ERR> 存取遠端 RMI 錯誤！");

            request.put("_error", errMessage);

            throw new CapMessageException(errMessage, this.getClass());

        } catch (Exception e) {

            String errMessage = CapAppContext.getMessage("HSM_ERROR");

            logger.debug(new StringBuffer(LogHeader).append("-Exception:").append(e.getMessage()).toString());

            request.put("_error", errMessage);

            throw new CapMessageException(errMessage, this.getClass());

        } // try 1

        request.put("_login", login);
        return DataHashMap;
    }

    /**
     * 
     * 負責進行餘額查詢，將該操作記錄於DB(IssuerLog)中，並顯示餘額查詢結果的畫面
     * 
     * @param request
     *            Request
     * 
     * @param login
     *            APLogin物件
     * 
     * @exception 利用log4j將Exception寫到Log
     *                File
     * 
     */
    private HashMap<String,Object> Inquiry(Request request, APLogin login, String operation, String LogHeader) throws CapMessageException {

        // HttpSession session = request.getSession(true);

        TxnTerminal txnTml = null;

        HashMap ResHashMap = new HashMap();

        String AUTHCODE = request.get("AUTHCODE");

        String trnsCode = request.get("TRNS_CODE");
        String ICACT_INDEX = request.get("ICACT_INDEX");
        // Processing

        try {

            // 參數格式檢核

            isValidField(ICACT_INDEX, "[0-7]");

            String client_time = null;

            boolean txnResultFlag = true;

            if (trnsCode == null)
                throw new CapMessageException(trnsCode + "交易發生錯誤", this.getClass());

            if (login.isTrnsLock(trnsCode)) {

                throw new CapMessageException(CapAppContext.getMessage("SERVICE_STOP"), this.getClass());

            }

            // txnTml = login.getTxnTml();

            txnTml = getTemporaryTerminal(login.getTmlID());

            TxnProcess01 txnProcess = new TxnProcess01(txnTml);

            if (trnsCode.equals("2590")) {

                txnProcess.setOnUsCard(true);

                logger.debug(new StringBuffer(LogHeader).append("-OnUsCard").toString());

            } else {

                txnProcess.setOnUsCard(false);

                logger.debug(new StringBuffer(LogHeader).append("-OffUsCard").toString());

            }

            txnProcess.setIssuerBankCode(login.getIssuerBankCode().substring(0, 3));

            txnProcess.setIssuerAccount(login.getIssuerAccount());

            txnProcess.setPCode(trnsCode);

            int act_index = Integer.parseInt(request.get("ICACT_INDEX"));

            txnProcess.setSourceAccount(login.getAccountList()[act_index]);

            txnProcess.setICCTxnNo(request.get("ICSEQ"));

            txnProcess.setICCRemark(login.getIssuer_remark());

            txnProcess.setTmlCheckCode(AUTHCODE + AUTHCODE);

            txnProcess.setTmlType(getTRMTYPE(login.getReader_type()));

            txnProcess.setTAC(request.get("ICTAC"));

            txnProcess.setMAC(request.get("ICMAC"));

            txnProcess.setSessionID(login.getSessionId());

            txnProcess.setClientIP(login.getIssuerIP());

            txnProcess.setClientDT(request.get("TXNDT"));

            txnProcess.setAuthFlag("0");

            txnResultFlag = txnProcess.doTxn();

            if (txnResultFlag) {

                logger.debug("交易序號     = " + txnTml.getMsgSeqNo());

                logger.debug("端末機號     = " + txnTml.getTmlID());

                logger.debug("交易結果     = " + txnProcess.getTrnsStatus());

                logger.debug("錯誤代碼     = " + txnProcess.getRespCode());

                logger.debug("錯誤說明(中) = " + getDscpCH(txnProcess.getRespCode()));

                logger.debug("錯誤說明(英) = " + getDscpEN(txnProcess.getRespCode()));

                logger.debug("可用餘額     = " + txnProcess.getAvailableBalance());

                logger.debug("帳戶餘額     = " + txnProcess.getAccountBalance());

                logger.debug("手續費         = " + "0");

                logger.debug("STAN      = " + txnProcess.getSTAN());

                logger.debug("交易日期時間 = " + txnProcess.getHostDT());

                ResHashMap.put("TXTNO", txnTml.getMsgSeqNo());

                ResHashMap.put("TML_ID", txnTml.getTmlID());

                if (txnProcess.getActionCode().equals(ActionCode_99)) {

                    ResHashMap.put("ERRCODE", txnProcess.getRespCode());

                } else {

                    ResHashMap.put("ERRCODE", txnProcess.getRespCode());

                    // 查詢結果成功後續就不需再做身份認證

                    if (txnProcess.getActionCode().equals(ActionCode_Success)) {

                        login.setHAS_VERIFY("true");

                    }

                }

                ResHashMap.put("ERRMSG_CH", getDscpCH(txnProcess.getRespCode()));

                ResHashMap.put("AVBAL", txnProcess.getAvailableBalance());

                ResHashMap.put("WTHBAL", txnProcess.getAccountBalance());

                ResHashMap.put("CHARGE", "");

                ResHashMap.put("STAN1", txnProcess.getSTAN());

                client_time = (String) txnProcess.getHostDT();

                if (client_time == null || client_time.equals(""))

                    client_time = DateUtil.getCurrentTime("DT", "AD");

                String CLIENT_DTTM = null;

                CLIENT_DTTM = DateUtil.formateDateTimeForUser(client_time);

                ResHashMap.put("DATE_TIME", CLIENT_DTTM.substring(0, 16));

                ResHashMap.put("PCODE", trnsCode);

                ResHashMap.put("BANK_NAME", getBankName(login.getIssuerBankCode().substring(0, 3)));

                ResHashMap.put("TRNS_OUT_BANK", login.getIssuerBankCode().substring(0, 3));

                ResHashMap.put("TRNS_OUT_ACCOUNT", login.getAccountList()[act_index]);

                // ResHashMap.put("TRNS_IN_BANK", "");

                // ResHashMap.put("TRNS_IN_ACCOUNT", "");

                // ResHashMap.put("TXAMT", request.getParameter("TXAMT"));

                // ResHashMap.put("TRNS_MEMO",(String)ReqHashMap.get("TRNS_MEMO"));

            } else {

                ResHashMap.put("TXTNO", txnTml.getMsgSeqNo());

                ResHashMap.put("TML_ID", txnTml.getTmlID());

                ResHashMap.put("ERRCODE", "EA_" + txnProcess.getTrnsStatus());

                ResHashMap.put("ERRMSG_CH", CapAppContext.getMessage("EA_" + txnProcess.getTrnsStatus()));

                ResHashMap.put("AVBAL", "");

                ResHashMap.put("WTHBAL", "");

                ResHashMap.put("CHARGE", "");

                ResHashMap.put("STAN1", "");

                client_time = null;

                if (client_time == null || client_time.equals(""))

                    client_time = DateUtil.getCurrentTime("DT", "AD");

                String CLIENT_DTTM = null;

                CLIENT_DTTM = DateUtil.formateDateTimeForUser(client_time);

                ResHashMap.put("DATE_TIME", CLIENT_DTTM.substring(0, 16));

                ResHashMap.put("PCODE", trnsCode);

                ResHashMap.put("BANK_NAME", getBankName(login.getIssuerBankCode().substring(0, 3)));

                ResHashMap.put("TRNS_OUT_BANK", login.getIssuerBankCode().substring(0, 3));

                ResHashMap.put("TRNS_OUT_ACCOUNT", login.getAccountList()[act_index]);

                // ResHashMap.put("TRNS_IN_BANK", "");

                // ResHashMap.put("TRNS_IN_ACCOUNT", "");

                // ResHashMap.put("TXAMT", "");

            }

            APSystemServiceImpl.TmlPool.returnTerminal(login.getTmlID());
           
            request.put("_result", ResHashMap);

            // put operation to IssuerSession

            login.setIssuerOperation((String) ResHashMap.get("PCODE"), client_time);

            String tNow = DateUtil.getCurrentTime("DT", "AD");

            ArrayList alField = new ArrayList();

            alField.add(login.getSessionId()); // SessionID

            alField.add(login.getIssuerBankCode()); // IssuerBankCode

            alField.add(login.getIssuerAccount()); // IssuerAccount

            alField.add(tNow); // ClientDT

            alField.add(trnsCode + operation); // OperFnct

            alField.add(login.getTmlID()); // TmlID

            alField.add(trnsCode); // PCode

            alField.add("0"); // LockFlag

            doInsertIssuerLog(request, operation, alField);

        } catch (Exception e) {

            logger.debug(new StringBuffer(LogHeader).append("-Exception:").append(e.getMessage()).toString());

            APSystemServiceImpl.TmlPool.returnTerminal(login.getTmlID());

            request.put("_error", e.getMessage());

        } // try 1

        request.put("_login", login);
        return ResHashMap;

    }

    // 自行卡(2590)，他行卡(2500)

    private String getTrnsCode(String IssuerBankCode) throws Exception {

        String rtnCode = "2500";

        String[] self_BankCode = getSelfBankCode();

        for (int i = 0; i < self_BankCode.length; i++) {

            if (IssuerBankCode.equals(self_BankCode[i])) {

                rtnCode = "2590";

                break;

            } else

                rtnCode = "2500";

        }

        return rtnCode;

    }
    
    
    private String getEncMappingTable(String sSession, String pinIndex) {
        try {
            String EncMappingTable;
            String DiversifyKey;

            WSSecurity wss = new WSSecurity((String) APSystem.getSYS_PRAM_MAP().get("WS_ADDR"), (String) APSystem.getSYS_PRAM_MAP().get("EC_SLOT"), WSS_SYS_ID, WSS_CHK_CODE, WSS_CHK_USR,
                    (String) APSystem.getSYS_PRAM_MAP().get("EC_PWD"));

            DiversifyKey = wss.do3DESEncrypt(WSS_3DES_TYPE, WSS_3DES_KEY, sSession);
            logger.info("DiversifyKey=" + DiversifyKey);
            EncMappingTable = CommonCryptoUtils.encryptByKeyValueForAWATM(String.valueOf(Hex.encodeHex(pinIndex.getBytes("UTF-8"))), Misc.hex2Bin(DiversifyKey.getBytes()));
            logger.info("EncMappingTable=" + EncMappingTable);

            return EncMappingTable;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error("getChallengeMAC Occur Error :" + e);
            e.printStackTrace();
        }
        return null;
    }
    
    protected final HttpSession getSession(Request request) {
        return ((HttpServletRequest) request.getServletRequest()).getSession();
    }
}
