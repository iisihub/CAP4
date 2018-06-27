/* 
 * GenericControl.java
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

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citi.webatm.rmi.TActionCode;
import com.citi.webatm.rmi.TComUsrAcctMgr;
import com.citi.webatm.rmi.TIssuerLogMgr;
import com.citi.webatm.rmi.TUsrInfoMgr;
import com.iisigroup.colabase.webatm.model.APLogin;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl;
import com.iisigroup.colabase.webatm.common.CCConstants;
import com.hitrust.trustatmtrns.RMIAPException;
import com.hitrust.trustatmtrns.util.DateUtil;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapAppContext;

import com.iisigroup.colabase.webatm.parameter.BankData;
import tw.com.citi.webatm.txn.TxnTerminal;

/**
 * <pre>
 * Generic servlet of Application Control, every Control servlet must inherit it.
 * </pre>
 * 
 * @since 2017年7月13日
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2017年7月13日,Sunkist Wang,new
 *          </ul>
 */
public class GenericControl extends MFormHandler {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    APSystemService apSystemService;

    /**
     * 
     * 負責將前端畫面的的資料存於HashMap中
     * 
     * @param request
     *            HttpServletRequest
     * 
     * @return HashMap物件
     * 
     */
    protected HashMap getHashData(HttpServletRequest req) {

        Enumeration columnNames = req.getParameterNames();

        HashMap field = new HashMap();

        String element = null;

        String value = null;

        while (columnNames.hasMoreElements()) {

            element = String.valueOf(columnNames.nextElement());

            value = (String) req.getParameter(element);

            if (value != null) {

                // if (APSystem.getSYS_AP_Type().equals(APSystem.CONST_AP_TYPE_OTHER)){

                try {

                    // value = new String(value.getBytes("ISO8859-1"));

                    value = new String(value.getBytes("8859_1"), "UTF-8");

                } catch (Exception e) {

                    // TODO: handle exception

                    logger.error("Translate Input Data Occur Error");

                }

                /*
                 * }else{
                 * 
                 * try {
                 * 
                 * value = (URLDecoder.decode(value, "8859_1"));
                 * 
                 * } catch (Exception e) {
                 * 
                 * // TODO: handle exception
                 * 
                 * LOG.error("Translate Input Data Occur Error");
                 * 
                 * }
                 * 
                 * }
                 */

            }

            field.put(element, (value == null) ? "" : (value).trim());

        }

        return field;

    }

    /**
     * 
     * 取得端末設備型態
     * 
     * 具密碼輸入器之確認型讀卡機：6536
     * 
     * 不具密碼輸入器之讀卡機：6534
     * 
     * 雖然在ReaderType Table 內有設計這個欄位
     * 
     * 但目前還是以此Method取出TerminalType給FISC為主
     * 
     * @param reader_type
     * 
     * @return
     * 
     *         Creation date:(2007/12/13 上午 11:33:41)
     * 
     */
    protected String getTRMTYPE(int reader_type) {

        if ((reader_type == CCConstants.READER_TYPE_KEYPAD) || (reader_type == CCConstants.READER_TYPE_KEYPAD_II)) {

            return "6536";

        } else {

            return "6534";

        }

    }

    /**
     * 
     * 取得客戶常轉資料
     * 
     * @param null
     * 
     *            使用 SP_ComUsrAcct_sel
     * 
     * @return 回傳 ComUsrAcct 的 ArrayList 資料
     * 
     * 
     * 
     */

    protected final ArrayList getComUsrAcctData(String IssuerBankCode, String IssuerAccount) {

        ArrayList alCondition = new ArrayList();

        alCondition.add(IssuerBankCode);

        alCondition.add(IssuerAccount);

        return getComUsrAcctData(alCondition, SP_Cmd_01);

    }

    /**
     * 
     * 取得客戶常轉資料
     * 
     * @param null
     * 
     *            使用 SP_ComUsrAcct_sel
     * 
     * @return 回傳 ComUsrAcct 的 ArrayList 資料
     * 
     * 
     * 
     */

    protected final ArrayList getComUsrAcctData(ArrayList alCondition, String SP_Cmd) {

        TComUsrAcctMgr ComUsrAcctMgrMgr;

        ArrayList alRtnData = null;

        logger.info("method: - Start getComUsrAcct()");

        try {

            ComUsrAcctMgrMgr = (TComUsrAcctMgr) Naming.lookup("rmi://" + apSystemService.getRmiSrvName() + ":" + apSystemService.getRmiSrvPort() + "/TComUsrAcct");

            alRtnData = ComUsrAcctMgrMgr.getArrayList_SP_ComUsrAcct(SP_Cmd, alCondition);

            if (alRtnData.size() != 0)

                return alRtnData;

            else

                return new ArrayList();

        } catch (RemoteException ex) {

            logger.error(ex.getMessage());

            logger.error("getComUsrAcct: 存取遠端 RMI 錯誤！");

            return null;

        } catch (Exception ex) {

            logger.error("getComUsrAcct: Other Exception:" + ex.getMessage());

            return null;

        } // try 1

    }

    /**
     * 
     * 取得客戶常轉資料
     * 
     * @param null
     * 
     *            使用 SP_ComUsrAcct_sel
     * 
     * @return 回傳 ComUsrAcct 的 ArrayList 資料
     * 
     * 
     * 
     */

    protected final ArrayList getUsrInfoData(String IssuerBankCode, String IssuerAccount) {

        TUsrInfoMgr UsrInfoMgrMgr;

        ArrayList alRtnData = null;

        ArrayList alCondition = new ArrayList();

        logger.info("method: - Start getUsrInfo()");

        try {

            alCondition.add(IssuerBankCode);

            alCondition.add(IssuerAccount);

            UsrInfoMgrMgr = (TUsrInfoMgr) Naming.lookup("rmi://" + apSystemService.getRmiSrvName() + ":" + apSystemService.getRmiSrvPort() + "/TUsrInfo");

            alRtnData = UsrInfoMgrMgr.getArrayList_SP_UsrInfo(SP_Cmd_03, alCondition);

            if (alRtnData.size() != 0)

                return alRtnData;

            else

                return new ArrayList();

        } catch (RemoteException ex) {

            logger.error(ex.getMessage());

            logger.error("getUsrInfo: 存取遠端 RMI 錯誤！");

            return null;

        } catch (Exception ex) {

            logger.error("getUsrInfo: Other Exception:" + ex.getMessage());

            return null;

        } // try 1

    }

    protected final boolean doInsertIssuerLog(Request request, String operation, ArrayList alField) {

        TIssuerLogMgr IssuerLogMgr;

        logger.info("operation:" + operation + "- Start doInsertIssuerLog");

        try {

            IssuerLogMgr = (TIssuerLogMgr) Naming.lookup("rmi://" + apSystemService.getRmiSrvName() + ":" + apSystemService.getRmiSrvPort() + "/TIssuerLog");

            int iRtn = IssuerLogMgr.insert_SP_IssuerLog(SP_Cmd_02, alField);

            if (iRtn == 0) {

                logger.debug("insertIssuerLog: fail");

                request.put("_error", CapAppContext.getMessage("DB_INS"));

                return false;

            } else {

                logger.info("insertIssuerLog: success");

                return true;

            }

        } catch (RemoteException ex) {

            logger.error(ex.getMessage());

            logger.error("insertIssuerLog: 存取遠端 RMI 錯誤！");

            request.put("_error", CapAppContext.getMessage("RMI_ERROR"));

        } catch (Exception ex) {

            logger.error("insertIssuerLog: Other Exception:" + ex.getMessage());

            request.put("_error", CapAppContext.getMessage("SYS10"));

        } // try 1

        return false;

    }

    /**
     * 
     * 取出自行代碼
     * 
     * PS..可能有多個(010,021)所以用Array
     * 
     **/

    protected String[] getSelfBankCode() {

        String[] result = null;

        result = ((String) (apSystemService.getSYS_PRAM_MAP().get("SelfBankCode"))).split(",");

        for (int i = 0; i < result.length; i++) {

            result[i] = (result[i].length() > 3) ? result[i].substring(0, 3) : result[i];

        }

        return result;

    }

    /**
     * 
     * 取出中文對照銀行名稱
     * 
     **/

    protected String getBankName(String BankCode) {

        BankData temp = apSystemService.getBankData(BankCode);

        if (temp == null)

            return "";

        else

            return temp.getName();

    }

    /**
     * 
     * 取出交易回應說明英文內容
     * 
     **/

    protected String getDscpEN(String RespCode) {

        TActionCode obj = null;

        String temp = null;

        if ((RespCode == null) || RespCode.equals(""))

            return "";

        obj = ((TActionCode) apSystemService.getActionCode(RespCode));

        if ((obj == null))

            temp = ((TActionCode) apSystemService.getActionCode("-1")).getDscpEN();

        else

            temp = obj.getDscpEN();

        return (temp == null) ? "" : temp;

    }

    /**
     * 
     * 取出交易回應說明中文內容
     * 
     **/

    protected String getDscpCH(String RespCode) {

        TActionCode obj = null;

        String temp = null;

        if ((RespCode == null) || RespCode.equals(""))

            return "";

        obj = ((TActionCode) apSystemService.getActionCode(RespCode));

        if ((obj == null))

            temp = ((TActionCode) apSystemService.getActionCode("-1")).getDscpCH();

        else

            temp = obj.getDscpCH();

        return (temp == null) ? "" : temp;

    }

    protected String getRespCodeDscp(String RespCode) {

        return ((TActionCode) apSystemService.getActionCode(RespCode)).getRespCodeDscp();

    }

    /**
     * 
     * 判斷做卡片驗證時需使用的PCode
     * 
     **/

    protected String getCardAuth_TrnsCode(String IssuerBankCode) throws Exception {

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

    /**
     * 
     * 進行晶片卡驗證
     * 
     * 主要要看verifyID()這個Method
     * 
     * @throws RMIAPException
     * 
     * 
     * 
     **/

    public void CardAuth(String function, String operation, Request request, APLogin login) throws CapException, RMIAPException {

        // Processing

        if (login.getCiti_CARD() == null) {

            String[] self_BankCode = getSelfBankCode();

            String IssuerBankCode = login.getIssuerBankCode().substring(0, 3);

            boolean is_citi_card = false;

            for (int i = 0; i < self_BankCode.length; i++) {

                if (IssuerBankCode.equals(self_BankCode[i])) {

                    is_citi_card = true;

                    break;

                } else

                    is_citi_card = false;

            }

            if (is_citi_card) {

                login.setCiti_CARD("true");

            } else {

                login.setCiti_CARD("false");

                login.setCAN_TXNQ("false");

                Integer TXNProc = null;

                TXNProc = apSystemService.getPCodeProc(login.getReader_type(), "2500");

                if (TXNProc.intValue() == CCConstants.READER_TXN_SET_DUMMY) {

                    login.put("_needVerifyPIN", "YES");

                } else {

                    login.put("_needVerifyPIN", "NO");

                }

            }

        }

        if (login.getCiti_CARD().equalsIgnoreCase("true")) {

            if (login.getCAN_TXNQ() == null) {

                login.setCAN_TXNQ("true");

            }

        }

        if (login.getHAS_VERIFY() == null) {

            login.setHAS_VERIFY("false");

        }

        if (login.getHAS_VERIFY().equals("true") == false) {

            request.put("NEXTPAGE", request.get("_service"));

            if (((String) request.get("ICTAC") != null) &&

                    (((String) request.get("ICTAC")).length() > 0)) {

                if (IDChecker.verifyID(request, login) == false) {

                    return;

                }

            } else if ((String) request.get("VPIN") != null) { // 目前沒有用

                login.setHAS_VERIFY("true");

            } else {

                // FIXME
                // request.put("_REDISPATCH", apSystemService.USER_AUTH_URL);

                if (login.getHAS_VERIFY().equalsIgnoreCase("false")) {

                    Integer TXNProc = null;

                    String trnsCode = "2590";

                    try {

                        trnsCode = getCardAuth_TrnsCode(login.getIssuerBankCode().substring(0, 3));

                    } catch (Exception e) {

                    }

                    TXNProc = apSystemService.getPCodeProc(login.getReader_type(), trnsCode);

                    if (TXNProc.intValue() == CCConstants.READER_TXN_SET_DUMMY) {

                        login.put("_needVerifyPIN", "YES");

                    } else {

                        login.put("_needVerifyPIN", "NO");

                    }

                }

                request.put("_result", getBankName(login.getIssuerBankCode().substring(0, 3)));

                String tNow = DateUtil.getCurrentTime("DT", "AD");

                ArrayList alField = new ArrayList();

                alField.add(login.getSessionId()); // SessionID

                alField.add(login.getIssuerBankCode()); // IssuerBankCode

                alField.add(login.getIssuerAccount()); // IssuerAccount

                alField.add(tNow); // ClientDT

                alField.add("CardChecker"); // OperFnct

                alField.add(login.getTmlID()); // TmlID

                alField.add(""); // PCode

                alField.add("0"); // LockFlag

                doInsertIssuerLog(request, "CardChecker", alField);

                return;

            }

        }

    }

    /**
     * 
     * 使用regex檢核字串內容
     * 
     **/

    protected void isValidField(String field, String Pattern) throws CapException {

        boolean retval = false;

        if (field == null)
            throw new CapMessageException(CapAppContext.getMessage("SYS09"), this.getClass());

        retval = field.matches(Pattern);

        if (!retval)
            throw new CapMessageException(CapAppContext.getMessage("SYS09"), this.getClass());

        return;

    }

    protected TxnTerminal getTemporaryTerminal(String tmlid) throws CapException {

        TxnTerminal txnTml = null;

        // 到TerminalPool取出一個Terminal

        int iReceiveTimeout = apSystemService.getWaitSleepTime() * 1000;

        int iWaitSleep = 1 * 1000; // TxnTml 接受不到Receive MsQ時Sleep間隔

        int iRetryTimer = iReceiveTimeout / iWaitSleep;

        txnTml = APSystemServiceImpl.TmlPool.getTerminal(tmlid);

        if (txnTml == null) {

            for (int i = iRetryTimer; i > 0; i--) {

                txnTml = APSystemServiceImpl.TmlPool.getTerminal(tmlid);

                try {

                    Thread.sleep(iWaitSleep);

                } catch (Exception e) {

                    e.printStackTrace();

                }

                logger.debug("getTerminal()");

                if (txnTml != null) {

                    break;

                }

            }

        }

        if (txnTml == null)

            throw new CapMessageException(CapAppContext.getMessage("ATM_ERROR"), this.getClass());

        logger.debug("Use TML_ID = " + txnTml.getTmlID());

        return txnTml;

    }

    public final static String ActionCode_99 = "99";

    public final static String ActionCode_Success = "000";

    private final static String SP_Cmd_01 = "SP_ComUsrAcct_sel";

    private final static String SP_Cmd_02 = "SP_IssuerLog_ins";

    private final static String SP_Cmd_03 = "SP_UsrInfo_sel";
}
