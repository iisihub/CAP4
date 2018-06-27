/* 
 * C0200Handler.java
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

import static com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl.WSS_3DES_KEY;
import static com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl.WSS_3DES_TYPE;
import static com.iisigroup.colabase.webatm.common.CCConstants.RAND_KEYPAD_LIST;
import static com.iisigroup.colabase.webatm.common.CCConstants.SESSION_ATTRIB;
import static com.iisigroup.colabase.webatm.common.CCConstants.SESSION_CAN_ACCESS_TO_NEXTPAGE;
import static com.iisigroup.colabase.webatm.common.CCConstants.WSS_CHK_CODE;
import static com.iisigroup.colabase.webatm.common.CCConstants.WSS_CHK_USR;
import static com.iisigroup.colabase.webatm.common.CCConstants.WSS_SYS_ID;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;
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
import com.hitrust.trustatmtrns.util.FileAccess;
import com.hitrust.trustatmtrns.util.MathUtil;
import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapSystemConfig;

import tw.com.citi.webatm.txn.TxnProcess02;
import tw.com.citi.webatm.txn.TxnProcess03;
import tw.com.citi.webatm.txn.TxnProcessGeneric;
import tw.com.citi.webatm.txn.TxnTerminal;
import tw.com.citi.ws.hsm.WSSecurity;

/**
 * <pre>
 * 轉帳支出
 * </pre>
 * 
 * @since 2017年7月13日
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2017年7月13日,Sunkist Wang,new
 *          </ul>
 */
@Controller("democ0200handler")
public class C0200Handler extends GenericControl {

    @Resource
    private CapSystemConfig systemConfig;
    @Autowired
    private APSystemService APSystem;

    /**
     * 由Portal呼叫的method，負責操作流程的控制
     * 
     * @param request
     * @throws CapException
     * @throws RMIAPException
     */
    @HandlerType(name = "TXN")
    public Result process(Request request) throws CapException, RMIAPException {
        AjaxFormResult result = new AjaxFormResult();

        HttpSession session = getSession(request);
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
        String operation = (String) request.getObject("_operation");
        String function = (String) request.getObject("_function");

        String LogHeader = "IssuerAccount:" + login.getIssuerAccount() + ",TmlID:" + login.getTmlID();

        logger.debug(new StringBuffer(LogHeader).append("-operation:").append(operation).toString());

        // Processing

        if (operation.equalsIgnoreCase("Start")) {

            logger.debug(LogHeader + "-doStart()");

        } else if (operation.equalsIgnoreCase("Q")) {

            this.Query(request, login, operation, LogHeader);

        } else if (operation.equalsIgnoreCase("I")) {

            this.Transfer(request, login, operation, LogHeader);

        } else if (operation.equalsIgnoreCase("Warn")) {

            this.Warn(request, login, operation, LogHeader);

        } else {

            // Unknow operation

            logger.warn("No such operation - " + operation);

            request.put("_error", CapAppContext.getMessage("SYS07"));

        }

        return result;
    }

    /**
     * operation : Start
     * 
     * @param request
     * @return
     * @throws CapMessageException
     * @throws RMIAPException
     */
    @HandlerType(name = "TXN")
    public Result start(Request request) throws CapMessageException, RMIAPException {
        AjaxFormResult result = new AjaxFormResult();
        HashMap ResHashMap = new HashMap();

        HttpSession session = getSession(request);
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
        String LogHeader = "IssuerAccount:" + login.getIssuerAccount() + ",TmlID:" + login.getTmlID();
        logger.debug(LogHeader + "-doStart()");

        ResHashMap.put("BANK_NAME", login.getIssuerBankCode().substring(0, 3) + "-" + getBankName(login.getIssuerBankCode().substring(0, 3)));
        ResHashMap.put("nextPage", "page/transfer01");
        request.put("_result", ResHashMap);
        // 完成後端處理後要記下允許至下一頁的 flag 給 checkpage 檢查
        request.<HttpServletRequest> getServletRequest().getSession().setAttribute(SESSION_CAN_ACCESS_TO_NEXTPAGE, true);
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
        String TRNS_IN_ACCOUNT = request.get("TRNS_IN_ACCOUNT");
        String TRNS_OUT_ACCOUNT=request.get("TRNS_OUT_ACCOUNT");
        if(!TRNS_IN_ACCOUNT.equals(TRNS_OUT_ACCOUNT)){
            
            String LogHeader = "IssuerAccount:" + login.getIssuerAccount() + ",TmlID:" + login.getTmlID();
            logger.debug(LogHeader + "-doStart()");
            this.Query(request, login, "Q", LogHeader);
           
        }
        
        result.set("hex_sessionid", login.getHexSessionId());
        return result;
    }

    /**
     * operation : I
     * 
     * @param request
     * @return
     * @throws CapMessageException
     * @throws RMIAPException
     */
    @HandlerType(name = "TXN")
    public Result transfer(Request request) throws CapMessageException, RMIAPException {
        AjaxFormResult result = new AjaxFormResult();
        HttpSession session = getSession(request);
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
        String LogHeader = "IssuerAccount:" + login.getIssuerAccount() + ",TmlID:" + login.getTmlID();
        logger.debug(LogHeader + "-doStart()");
        HashMap<String,Object> DataHashMap = this.Transfer(request, login, "I", LogHeader);
        result.putAll(DataHashMap);
        return result;
    }

    /**
     * operation : Warn
     * 
     * @param request
     * @return
     * @throws CapMessageException
     * @throws RMIAPException
     */
    @HandlerType(name = "TXN")
    public Result warn(Request request) throws CapMessageException, RMIAPException {
        AjaxFormResult result = new AjaxFormResult();
        HttpSession session = getSession(request);
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
        String LogHeader = "IssuerAccount:" + login.getIssuerAccount() + ",TmlID:" + login.getTmlID();
        logger.debug(LogHeader + "-doStart()");
        HashMap<String,Object> DataHashMap = this.Warn(request, login, "Warn", LogHeader);
        result.putAll(DataHashMap);
       
        return result;
    }
    
    @HandlerType(name = "TXN")
    public Result EncMappingTable(Request request) throws CapMessageException, RMIAPException {
        AjaxFormResult result = new AjaxFormResult();
        HttpSession session = getSession(request);;
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
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
        result.set("HexSessionId", login.getHexSessionId());
        result.set("EncMappingTable", EncMappingTable);

        return result;
    } 

    /**
     * 負責將該操作記錄於DB(IssuerLog)中，並顯示該操作功能的頁面
     * 
     * @param request
     *            Request
     * @param login
     *            APLogin物件
     * @param operation
     * @param LogHeader
     * @throws CapException
     * @throws RMIAPException
     */
    private void Query(Request request, APLogin login, String operation, String LogHeader) throws CapException, RMIAPException {

        // Processing

        // HttpSession session = request.getSession(true);

        // session.setAttribute("ResData01_I",null); //釋放Session資源

        login.setReqData(null);

        String tNow = DateUtil.getCurrentTime("DT", "AD");

        try {

            ArrayList alUsrAcct = new ArrayList();

            ArrayList alUsrInfo = null;

            ArrayList BankInfoarr = apSystemService.getBankInfoArray();

            ArrayList SysCodearr = apSystemService.getTSysCodeArray("BANK_TYPE");

            // select user setup account data

            alUsrAcct = getComUsrAcctData(login.getIssuerBankCode().substring(0, 3), login.getIssuerAccount());

            if (alUsrAcct == null)
                throw new CapMessageException(CapAppContext.getMessage("RMI_ERROR"), this.getClass());

            alUsrInfo = getUsrInfoData(login.getIssuerBankCode().substring(0, 3), login.getIssuerAccount());

            if (alUsrInfo == null)
                throw new CapMessageException(CapAppContext.getMessage("RMI_ERROR"), this.getClass());

            // put operation to IssuerSession

            login.setIssuerOperation("252X", tNow);

            // put result to request

            request.put("_result_bankName", apSystemService.getBankData(login.getIssuerBankCode().substring(0, 3)).getName());

            request.put("_result_usrAcct", alUsrAcct);

            request.put("_result_usrInfo", alUsrInfo);

            login.put("SysCode", SysCodearr);

            login.put("BankInfo", BankInfoarr);

            ArrayList alField = new ArrayList();

            alField.add(login.getSessionId()); // SessionID

            alField.add(login.getIssuerBankCode()); // IssuerBankCode

            alField.add(login.getIssuerAccount()); // IssuerAccount

            alField.add(tNow); // ClientDT

            alField.add("252X" + operation); // OperFnct

            alField.add(login.getTmlID()); // TmlID

            alField.add("252X"); // PCode

            alField.add("0"); // LockFlag

//            if (doInsertIssuerLog(request, operation, alField) == false) {
//
//                throw new CapMessageException(CapAppContext.getMessage("RMI_ERROR"), this.getClass());
//
//            }
            

        } catch (Exception e) {

            String errMessage = e.getMessage();

            logger.debug(new StringBuffer(LogHeader).append("-Exception:").append(e.getMessage()).toString());

            request.put("_error", errMessage);

        } // try 1

        request.put("_login", login);
    }

    /**
     * 
     * Warn Page.
     * 
     * @param request
     *            HTTP Request
     * 
     * @param login
     *            the login user object.
     * @throws RMIAPException
     * 
     */

    private HashMap<String,Object>  Warn (Request request, APLogin login, String operation, String LogHeader) throws CapException, RMIAPException {

        // HttpSession session = request.getSession(true);

        // Processing

        String tNow = DateUtil.getCurrentTime("DT", "AD");

        HashMap hmReqData = getHashData((HttpServletRequest) request.getServletRequest());

        String trnsCode = null;

        String payBankID = null;

        String remitBankID = null;


        try {

            try {

                // 參數格式檢核

                isValidField(request.get("TRNS_OUT_ACCOUNT_INDEX"), "[0-7]");

                isValidField(request.get("TRNS_IN_BANK"), "\\d{3}");

                isValidField(request.get("TRNS_AMOUNT"), "\\d{1,12}");

                // check交易代號

                payBankID = login.getIssuerBankCode().substring(0, 3);

                remitBankID = (String) hmReqData.get("TRNS_IN_BANK");

                trnsCode = getTrnsCode(payBankID, remitBankID, LogHeader);

                // Check TrnsControl is Lock

                // 轉帳交易:2521, 2522, 2523, 2524, 2580

                if (login.isTrnsLock(trnsCode)) {

                    throw new CapMessageException(CapAppContext.getMessage("SERVICE_STOP"), this.getClass());

                }

                int act_index = Integer.parseInt(request.get("TRNS_OUT_ACCOUNT_INDEX"));

                hmReqData.put("TRNS_OUT_ACCOUNT", login.getAccountList()[act_index]);

                String Decrypt_TRNS_IN_ACCOUNT = null;

                String TRNS_IN_ACCOUNT = null;

                try {

                    WSSecurity wss = new WSSecurity((String) apSystemService.getSYS_PRAM_MAP().get("WS_ADDR"), (String) apSystemService.getSYS_PRAM_MAP().get("EC_SLOT"), CCConstants.WSS_SYS_ID,

                            CCConstants.WSS_CHK_CODE, CCConstants.WSS_CHK_USR, (String) apSystemService.getSYS_PRAM_MAP().get("EC_PWD"));
                
                    Decrypt_TRNS_IN_ACCOUNT = wss.do3DESDecryptByValue(Misc.hex2Bin(login.getHexSessionId().getBytes()), request.get("TRNS_IN_ACCOUNT"));
                    Decrypt_TRNS_IN_ACCOUNT = new String(Misc.hex2Bin(Decrypt_TRNS_IN_ACCOUNT.getBytes())).trim();
//                    
                    TRNS_IN_ACCOUNT = Misc.padZero(Decrypt_TRNS_IN_ACCOUNT, 16);

                } catch (Exception e) {

                    String errMessage = CapAppContext.getMessage("HSM_ERROR");

                    logger.error(LogHeader + "<ERR> 存取遠端 HSM_RMI 錯誤！");

                    throw new CapMessageException(errMessage, this.getClass());

                }

                // 參數格式檢核

                isValidField(TRNS_IN_ACCOUNT, "\\d{6,16}");

                hmReqData.put("TRNS_IN_ACCOUNT", TRNS_IN_ACCOUNT);

                hmReqData.put("TRNS_OUT_BANK", payBankID);

                hmReqData.put("PAY_BANK_NAME", apSystemService.getBankData(payBankID).getName());

                try {

                    hmReqData.put("REMIT_BANK_NAME", apSystemService.getBankData(remitBankID).getName());

                } catch (Exception e) {

                    throw new CapMessageException(CapAppContext.getMessage("TRNS_IN_BANK_ERROR"), this.getClass());

                }

                hmReqData.put("TRNS_CODE", trnsCode);
                hmReqData.put("TML_ID", login.getTmlID());
                hmReqData.put("tNow", tNow);
                

                logger.info(new StringBuffer(LogHeader).append("-前端回傳之ReqData ").append(hmReqData).toString());

            } catch (CapException ex) {

                logger.debug(new StringBuffer(LogHeader).append("-CapException:").append(ex.getMessage()).toString());

                throw new CapMessageException(ex.getMessage(), this.getClass());

            } catch (Exception ex) {

                logger.debug(new StringBuffer(LogHeader).append("-RMI Error:").append(ex.getMessage()).toString());

                throw new CapMessageException(CapAppContext.getMessage("RMI_ERROR"), this.getClass());

            }

            Integer TXNProc = null;

            TXNProc = apSystemService.getPCodeProc(login.getReader_type(), trnsCode);

            if (TXNProc.intValue() == CCConstants.READER_TXN_SET_DUMMY) {

                login.put("_needVerifyPIN", "YES");

            } else {

                login.put("_needVerifyPIN", "NO");

            }

            // put operation to IssuerSession

            login.setIssuerOperation(trnsCode, tNow);

            login.setReqData(hmReqData);

            request.put("_result", hmReqData);

            // session.setAttribute("ResData01_I",hmReqData);

            ArrayList alField = new ArrayList();

            alField.add(login.getSessionId()); // SessionID

            alField.add(login.getIssuerBankCode()); // IssuerBankCode

            alField.add(login.getIssuerAccount()); // IssuerAccount

            alField.add(tNow); // ClientDT

            alField.add(trnsCode + operation); // OperFnct

            alField.add(login.getTmlID()); // TmlID

            alField.add(trnsCode); // PCode

            alField.add("0"); // LockFlag

//            if (doInsertIssuerLog(request, operation, alField) == false) {
//
//                throw new CapMessageException(CapAppContext.getMessage("RMI_ERROR"), this.getClass());
//
//            }
           

        } catch (Exception e) {

            String errMessage = e.getMessage();

            logger.debug(new StringBuffer(LogHeader).append("-Exception:").append(e.getMessage()).toString());

            request.put("_error", errMessage);

        } // try

        request.put("_login", login);
        return hmReqData;
    }

    /**
     * 
     * 進行與BAFES的交易並將結果Keep起來供後續之頁面顯示.
     * 
     * 圖形驗證碼如果輸入錯誤則導回設定之頁面
     * 
     * @param request
     *            HTTP Request
     * 
     * @param login
     *            the login user object.
     * 
     */

    private HashMap<String,Object> Transfer(Request request, APLogin login, String operation, String LogHeader) throws CapException {

        HashMap<String,Object> ReqHashMap = login.getReqData();

        String AUTHCODE = request.get("AUTHCODE");

        String trnsCode = request.get("TRNS_CODE");
        
        HashMap ResHashMap = new HashMap();

        // Processing

        try {



            String client_time = null;

            boolean txnResultFlag = true;

            logger.info(new StringBuffer(LogHeader).append("-ReqHashMap:").append(ReqHashMap).toString());

            String isSendMail = (String)request.get("EmailType");

            logger.info(new StringBuffer(LogHeader).append("-TrnsCode:").append(trnsCode).toString());

            if (trnsCode == null)
                throw new CapMessageException(trnsCode + "交易發生錯誤", this.getClass());

            if (login.isTrnsLock(trnsCode)) {

                throw new CapMessageException(CapAppContext.getMessage("SERVICE_STOP"), this.getClass());

            }

            TxnTerminal txnTml = null;

            TxnProcessGeneric txnProcess = null;

            // txnTml = login.getTxnTml();

            txnTml = getTemporaryTerminal(login.getTmlID());

            int act_index;

            if (trnsCode.equals("2580")) {

                txnProcess = new TxnProcess02(txnTml);

                ((TxnProcess02) txnProcess).setTransactionType(TxnProcess02.TransactionType_Transfer);

                txnProcess.setIssuerBankCode(login.getIssuerBankCode().substring(0, 3));

                txnProcess.setIssuerAccount(login.getIssuerAccount());

                txnProcess.setPCode(trnsCode);

                act_index = Integer.parseInt((String) ReqHashMap.get("TRNS_OUT_ACCOUNT_INDEX"));

                txnProcess.setSourceAccount(login.getAccountList()[act_index]);

                txnProcess.setDestBankCode((String) ReqHashMap.get("TRNS_IN_BANK"));

                txnProcess.setDestAccount((String) ReqHashMap.get("TRNS_IN_ACCOUNT"));

                txnProcess.setTxnAmount((String) ReqHashMap.get("TRNS_AMOUNT"));

                txnProcess.setMemo((String) ReqHashMap.get("TRNS_MEMO"));

                txnProcess.setICCTxnNo(String.valueOf(Integer.parseInt(request.get("ICSEQ"))));

                txnProcess.setICCRemark(login.getIssuer_remark());

                txnProcess.setTmlCheckCode(AUTHCODE + AUTHCODE);

                txnProcess.setTmlType(getTRMTYPE(login.getReader_type()));

                txnProcess.setTAC(request.get("ICTAC"));

                txnProcess.setMAC(request.get("ICMAC"));

                txnProcess.setSessionID(login.getSessionId());

                txnProcess.setClientIP(login.getIssuerIP());

                txnProcess.setClientDT(request.get("TXNDT"));

                txnResultFlag = txnProcess.doTxn();

            } else {

                txnProcess = new TxnProcess03(txnTml);

                txnProcess.setOnUsCard(false);

                String IssuerBankCode = login.getIssuerBankCode().substring(0, 3);

                String[] self_BankCode = getSelfBankCode();

                for (int i = 0; i < self_BankCode.length; i++) {

                    if (IssuerBankCode.equals(self_BankCode[i])) {

                        txnProcess.setOnUsCard(true);

                        break;

                    }

                }

                ((TxnProcess03) txnProcess).setTransactionType(TxnProcess03.TransactionType_Transfer);

                txnProcess.setIssuerBankCode(IssuerBankCode);

                txnProcess.setIssuerAccount(login.getIssuerAccount());

                txnProcess.setPCode(trnsCode);

                act_index = Integer.parseInt((String) ReqHashMap.get("TRNS_OUT_ACCOUNT_INDEX"));

                txnProcess.setSourceAccount(login.getAccountList()[act_index]);

                txnProcess.setDestBankCode((String) ReqHashMap.get("TRNS_IN_BANK"));

                txnProcess.setDestAccount((String) ReqHashMap.get("TRNS_IN_ACCOUNT"));

                txnProcess.setTxnAmount((String) ReqHashMap.get("TRNS_AMOUNT"));

                txnProcess.setMemo((String) ReqHashMap.get("TRNS_MEMO"));

                txnProcess.setICCTxnNo(String.valueOf(Integer.parseInt(request.get("ICSEQ"))));

                txnProcess.setICCRemark(login.getIssuer_remark());

                txnProcess.setTmlCheckCode(AUTHCODE + AUTHCODE);

                txnProcess.setTmlType(getTRMTYPE(login.getReader_type()));

                txnProcess.setTAC(request.get("ICTAC"));

                txnProcess.setMAC(request.get("ICMAC"));

                txnProcess.setSessionID(login.getSessionId());

                txnProcess.setClientIP(login.getIssuerIP());

                txnProcess.setClientDT(request.get("TXNDT"));

                txnResultFlag = txnProcess.doTxn();

            }

            if (txnResultFlag) {

                logger.debug("交易序號     = " + txnTml.getMsgSeqNo());

                logger.debug("端末機號     = " + txnTml.getTmlID());

                logger.debug("交易結果     = " + txnProcess.getTrnsStatus());

                logger.debug("錯誤代碼     = " + txnProcess.getRespCode());

                logger.debug("錯誤說明(中) = " + getDscpCH(txnProcess.getRespCode()));

                logger.debug("錯誤說明(英) = " + getDscpEN(txnProcess.getRespCode()));

                logger.debug("可用餘額     = " + txnProcess.getAvailableBalance());

                logger.debug("帳戶餘額     = " + txnProcess.getAccountBalance());

                logger.debug("手續費         = " + txnProcess.getFeeCharge());

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

                ResHashMap.put("CHARGE", txnProcess.getFeeCharge());

                ResHashMap.put("BizDayFlag", txnProcess.getBizDayFlag());

                // 自行交易2580 沒有STAN,Show自行的一組ConfNo

                if (trnsCode.equals("2580"))

                    ResHashMap.put("STAN1", txnProcess.getTxnConfNo());

                else

                    ResHashMap.put("STAN1", txnProcess.getSTAN());

                client_time = (String) txnProcess.getHostDT();

                if (client_time == null || client_time.equals(""))

                    client_time = DateUtil.getCurrentTime("DT", "AD");

                String CLIENT_DTTM = null;

                CLIENT_DTTM = DateUtil.formateDateTimeForUser(client_time);

                ResHashMap.put("DATE_TIME", CLIENT_DTTM.substring(0, 16));

                ResHashMap.put("TAC_ID", trnsCode);

                ResHashMap.put("PAY_BANK_NAME", (String) ReqHashMap.get("PAY_BANK_NAME"));

                ResHashMap.put("REMIT_BANK_NAME", (String) ReqHashMap.get("REMIT_BANK_NAME"));

                ResHashMap.put("TRNS_OUT_BANK", login.getIssuerBankCode().substring(0, 3));

                ResHashMap.put("TRNS_OUT_ACCOUNT", login.getAccountList()[act_index]);

                ResHashMap.put("TRNS_IN_BANK", (String) ReqHashMap.get("TRNS_IN_BANK"));

                ResHashMap.put("TRNS_IN_ACCOUNT", (String) ReqHashMap.get("TRNS_IN_ACCOUNT"));
                
                ResHashMap.put("TXAMT", (String) ReqHashMap.get("TRNS_AMOUNT"));

                

                // ResHashMap.put("TRNS_MEMO",(String)ReqHashMap.get("TRNS_MEMO"));

            } else {

                ResHashMap.put("TXTNO", txnTml.getMsgSeqNo());

                ResHashMap.put("TML_ID", txnTml.getTmlID());

                ResHashMap.put("ERRCODE", "EA_" + txnProcess.getTrnsStatus());

                ResHashMap.put("ERRMSG_CH", CapAppContext.getMessage("EA_" + txnProcess.getTrnsStatus()));

                ResHashMap.put("AVBAL", "");

                ResHashMap.put("WTHBAL", "");

                ResHashMap.put("CHARGE", "");

                ResHashMap.put("BizDayFlag", "");

                ResHashMap.put("STAN1", "");

                client_time = null;

                if (client_time == null || client_time.equals(""))

                    client_time = DateUtil.getCurrentTime("DT", "AD");

                String CLIENT_DTTM = null;

                CLIENT_DTTM = DateUtil.formateDateTimeForUser(client_time);

                ResHashMap.put("DATE_TIME", CLIENT_DTTM.substring(0, 16));

                ResHashMap.put("TAC_ID", trnsCode);

                ResHashMap.put("PAY_BANK_NAME", (String) ReqHashMap.get("PAY_BANK_NAME"));

                ResHashMap.put("REMIT_BANK_NAME", (String) ReqHashMap.get("REMIT_BANK_NAME"));

                ResHashMap.put("TRNS_OUT_BANK", login.getIssuerBankCode().substring(0, 3));

                ResHashMap.put("TRNS_OUT_ACCOUNT", login.getAccountList()[act_index]);

                ResHashMap.put("TRNS_IN_BANK", (String) ReqHashMap.get("TRNS_IN_BANK"));

                ResHashMap.put("TRNS_IN_ACCOUNT", (String) ReqHashMap.get("TRNS_IN_ACCOUNT"));

                ResHashMap.put("TXAMT", (String) ReqHashMap.get("TRNS_AMOUNT"));
                
               

            }

            APSystemServiceImpl.TmlPool.returnTerminal(login.getTmlID());

            // 表示成功，則寄發入帳通知給收款人

            logger.info(new StringBuffer(LogHeader).append("-txnResultFlag:").append(txnResultFlag).append(" isSendMail:").append(isSendMail).toString());

            if (txnResultFlag && txnProcess.getActionCode().equals("000")) {

                // if (isSendMail != null && isSendMail.equals("2")){

                if ((ReqHashMap.get("MYEMAIL") != null) || (isSendMail != null && isSendMail.equals("2"))) {

                    // sendmail
                    ResHashMap.put("EMAIL",(String)request.get("EMAIL"));
                    
                    ResHashMap.put("notes"," ");

                    sendMail(ReqHashMap, client_time, ResHashMap, LogHeader);

                }

            }

            // put operation to IssuerSession

            login.setIssuerOperation(trnsCode, client_time);
            
          

            request.put("_result", ResHashMap);

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

            //doInsertIssuerLog(request, operation, alField);

            // login.setReqData(null);

        } catch (Exception e) {

            logger.error(new StringBuffer(LogHeader).append("-Exception:").append(e.getMessage()).toString());

            APSystemServiceImpl.TmlPool.returnTerminal(login.getTmlID());
            // e.printStackTrace();

            request.put("_error", e.getMessage());

        } // try

        request.put("_login", login);
        return ResHashMap;
    }

    private void sendMail(HashMap ReqHashMap, String client_time, HashMap ResHashMap, String LogHeader) {

        String trns_out_bank = "";

        String trns_in_bank = "";

        String trns_out_account = "";

        String trns_in_account = "";

        String trns_amount = "";

        String recv_mail = "";

        String smtp = "";

        String sender = "";

        String subject = "";

        String mailContent = "";

        String notes = "";

        String txtno = "";

        String fee = "";

        String stan = "";

        try {

            trns_out_bank = apSystemService.getBankData((String) ReqHashMap

                    .get("TRNS_OUT_BANK")).getName();

            trns_in_bank = apSystemService.getBankData((String) ReqHashMap

                    .get("TRNS_IN_BANK")).getName();

            trns_in_account = (String) ReqHashMap.get("TRNS_IN_ACCOUNT");

            trns_in_account = (trns_in_account == null) ? "" : trns_in_account.trim();

            trns_in_account = "00" + DateUtil.padding(Long.parseLong(trns_in_account), 16);

            trns_in_account = trns_in_account.substring(trns_in_account.length() - 16);

            trns_out_account = (String) ReqHashMap.get("TRNS_OUT_ACCOUNT");

            trns_out_account = (trns_out_account == null) ? "" : trns_out_account.trim();

            trns_out_account = "00" + trns_out_account;

            trns_out_account = trns_out_account.substring(trns_out_account.length() - 16);

            logger.debug(new StringBuffer(LogHeader).append(" trns_in_account:").append(trns_in_account).toString());

            logger.debug(new StringBuffer(LogHeader).append(" trns_out_account:").append(trns_out_account).toString());

            trns_in_account = "***********" + trns_in_account.substring(trns_in_account.length() - 5);

            trns_out_account = "***********" + trns_out_account.substring(trns_out_account.length() - 5);

            trns_amount = (String) ReqHashMap.get("TRNS_AMOUNT");

            trns_amount = (trns_amount == null) ? "0" : trns_amount;

            trns_amount = MathUtil.addAmtComma(Double.parseDouble(trns_amount)); // 金額加上千分號

            recv_mail = (String) ResHashMap.get("EMAIL");

            notes = (String) ResHashMap.get("notes");

            txtno = (String) ResHashMap.get("TXTNO");

            fee = (String) ResHashMap.get("CHARGE");

            stan = (String) ResHashMap.get("STAN1");

            smtp = (String) apSystemService.getSYS_PRAM_MAP().get("SMTP");

            sender = (String) apSystemService.getSYS_PRAM_MAP().get("SENDER");

            subject = (String) apSystemService.getSYS_PRAM_MAP().get("SUBJECT");

            logger.debug(new StringBuffer(LogHeader).append(" recv_mail:").append(recv_mail).toString());

            logger.debug(new StringBuffer(LogHeader).append(" sender:").append(sender).toString());

            logger.debug(new StringBuffer(LogHeader).append(" smtp:").append(smtp).toString());

            // 讀取format
            // FIXME
            mailContent = new String(FileAccess.readFile(systemConfig.getProperty("emailFileLocation") + "/" + systemConfig.getProperty("mailContent")));

            mailContent = mailContent.replaceAll("<%=TRNS_OUT_BANK%>", trns_out_bank);

            mailContent = mailContent.replaceAll("<%=TRNS_OUT_ACCOUNT%>", trns_out_account);

            mailContent = mailContent.replaceAll("<%=TRNS_AMOUNT%>", trns_amount);

            mailContent = mailContent.replaceAll("<%=TRNS_IN_BANK%>", trns_in_bank);

            mailContent = mailContent.replaceAll("<%=TRNS_IN_ACCOUNT%>", trns_in_account);

            mailContent = mailContent.replaceAll("<%=NOTES%>", notes.replace('$', '＄'));

            mailContent = mailContent.replaceAll("<%=TRNS_DT%>", DateUtil.formateDateTimeForUser(client_time));

            mailContent = mailContent.replaceAll("<%=TRNS_FEE%>", (fee.equals("0")) ? "0.00" : fee);

            mailContent = mailContent.replaceAll("<%=TRNS_STAN%>", stan);

            logger.debug(new StringBuffer(LogHeader).append(" mailContent:").append(mailContent).toString());



            logger.info(new StringBuffer(LogHeader).append(" TXTNO:").append(txtno).append(" mail已送出!!").toString());

        } catch (Exception ex) {

            logger.error(new StringBuffer(LogHeader).append(" TXTNO:").append(txtno).append(" mail傳送失敗!!").append(ex).toString());

        }

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

    /**
     * 
     * 取得交易代碼
     * 
     * @param payBankCode
     *            付款銀行代碼
     * 
     * @param remitBankCode
     *            收款銀行代碼
     * 
     */

    private String getTrnsCode(String payBankCode, String remitBankCode, String LogHeader) throws Exception {

        logger.debug(new StringBuffer(LogHeader).append("-PayBankCode:").append(payBankCode).append("-RemitBankCode:").append(remitBankCode).toString());

        String[] self_BankCode = getSelfBankCode();

        payBankCode = (payBankCode.length() > 3) ? payBankCode.substring(0, 3) : payBankCode;

        remitBankCode = (remitBankCode.length() > 3) ? remitBankCode.substring(0, 3) : remitBankCode;

        String rtnCode = "";

        logger.debug(
                new StringBuffer(LogHeader).append("-PayBankCode:").append(payBankCode).append(" RemitBankCode:").append(remitBankCode).append(" self_BankCode:").append(self_BankCode).toString());

        boolean isSelfBank_Pay = false;

        boolean isSelfBank_Remit = false;

        for (int i = 0; i < self_BankCode.length; i++) {

            if (payBankCode.equals(self_BankCode[i])) {

                isSelfBank_Pay = true;

            }

            if (remitBankCode.equals(self_BankCode[i])) {

                isSelfBank_Remit = true;

            }

        }

        if (payBankCode.equals(remitBankCode) || (isSelfBank_Pay && isSelfBank_Remit)) {

            if (isSelfBank_Pay && isSelfBank_Remit) {

                rtnCode = "2580";

            } else

                rtnCode = "2523";

        } else if (isSelfBank_Pay) {

            rtnCode = "2521";

        } else if (isSelfBank_Remit) {

            rtnCode = "2522";

        } else {

            rtnCode = "2524";

        }

        logger.debug(new StringBuffer(LogHeader).append("-rtnCode:").append(rtnCode).toString());

        return rtnCode;

    }

}
