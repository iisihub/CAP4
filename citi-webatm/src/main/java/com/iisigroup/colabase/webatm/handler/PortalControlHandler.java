package com.iisigroup.colabase.webatm.handler;

import static com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl.WSS_3DES_KEY;
import static com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl.WSS_3DES_TYPE;
import static com.iisigroup.colabase.webatm.common.CCConstants.RAND_KEYPAD_LIST;
import static com.iisigroup.colabase.webatm.common.CCConstants.READER_TYPE_ALARM;
import static com.iisigroup.colabase.webatm.common.CCConstants.READER_TYPE_DEFAULT;
import static com.iisigroup.colabase.webatm.common.CCConstants.READER_TYPE_KEYPAD;
import static com.iisigroup.colabase.webatm.common.CCConstants.READER_TYPE_KEYPAD_II;
import static com.iisigroup.colabase.webatm.common.CCConstants.SESSION_ATTRIB;
import static com.iisigroup.colabase.webatm.common.CCConstants.SESSION_CAN_ACCESS_TO_NEXTPAGE;
import static com.iisigroup.colabase.webatm.common.CCConstants.SESSION_LAST_ACCESS_PAGENO;
import static com.iisigroup.colabase.webatm.common.CCConstants.SESSION_LOG_OUT;
import static com.iisigroup.colabase.webatm.common.CCConstants.WSS_CHK_CODE;
import static com.iisigroup.colabase.webatm.common.CCConstants.WSS_CHK_USR;
import static com.iisigroup.colabase.webatm.common.CCConstants.WSS_SYS_ID;
import static com.iisigroup.colabase.webatm.common.CCConstants.SELECT_TYPE;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.citi.utils.Misc;
import com.citi.webatm.rmi.TIssuerLogMgr;
import com.citi.webatm.rmi.TSysParmMgr;
import com.iisigroup.colabase.webatm.model.APLogin;
import com.iisigroup.colabase.webatm.model.APLogout;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl;
import com.iisigroup.colabase.webatm.common.CCConstants;
import com.iisigroup.colabase.webatm.toolkit.CommonCryptoUtils;
import com.hitrust.trustatmtrns.util.DateUtil;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapSystemConfig;

import tw.com.citi.webatm.txn.TxnTerminal;
import tw.com.citi.ws.hsm.WSSecurity;
import tw.com.iisi.desserver.execption.SAPIException;

/**
 * <pre>
 * PortalControlHandler
 * </pre>
 */
@Controller("portalhandler")
public class PortalControlHandler extends MFormHandler {
    // log4J category
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private APSystemService APSystem;
    @Autowired
    private CapSystemConfig sysProp;

    private final static String SP_Cmd_01 = "SP_SysParm_sel_byUnit";
    private final static String SP_Cmd_02 = "SP_IssuerLog_ins";

    public Result reader(Request request) throws IOException {
        AjaxFormResult result = new AjaxFormResult();
        HttpSession session = getSession(request);
        // get reader name
        String reader = request.get("READER_NAME");
        // get randrom number
        String sRand1 = request.get("_random");

        String EncMappingTable;
        // get Session Id
        String session_id = session.getId();

        StringBuffer pintemp = new StringBuffer();
        int[] intIndex = (int[]) session.getAttribute(RAND_KEYPAD_LIST);
        for (int i : intIndex) {
            pintemp = pintemp.append(i);
        }
        String pinIndex = pintemp + "xxxxxx";
        APLogout logout = new APLogout();
        APLogin login = new APLogin(session_id);
        String hexSession_id = getHexSessionid(session_id);
        login.setReaderName(reader);
        login.setHexSessionId(hexSession_id);

        int iReaderType = APSystem.getReaderType(reader);

        login.setReader_type(iReaderType);
        if ((iReaderType & READER_TYPE_KEYPAD_II) == READER_TYPE_KEYPAD_II) {
            login.setKeyboard_type("" + READER_TYPE_KEYPAD_II);
            throw new CapMessageException("未支援目前讀卡機。", getClass());
        } else if ((iReaderType & READER_TYPE_KEYPAD) == READER_TYPE_KEYPAD) {
            login.setKeyboard_type("" + READER_TYPE_KEYPAD);
            throw new CapMessageException("未支援目前讀卡機。", getClass());
        } else if ((iReaderType & READER_TYPE_ALARM) == READER_TYPE_ALARM) {
            login.setKeyboard_type("" + READER_TYPE_ALARM);
            throw new CapMessageException("未支援目前讀卡機。", getClass());
        } else {
            login.setKeyboard_type("" + READER_TYPE_DEFAULT);
        }
        LOG.debug("Set Session _readerType to " + iReaderType);
        login.setRand1(sRand1);

        String sRand2 = getRandNumString();
        login.setRand2(sRand2);
        try {
            String[] saChalgMac = getChallengeMAC(hexSession_id, new String[] { sRand1, sRand2 });
            login.setRand1mac(saChalgMac[0]);
            login.setRand2mac(saChalgMac[1]);
        } catch (Exception e) {
            // TODO: handle exception
            LOG.error("Challenge Error:" + e.toString());
            e.printStackTrace();
            throw new CapMessageException(CapAppContext.getMessage("SYS01"), getClass());
        }

        try {
            EncMappingTable = getEncMappingTable(hexSession_id, pinIndex);
            login.setEncMappingTable(EncMappingTable);
            login.setPINIndex(pinIndex);
        } catch (Exception e) {
            // TODO: handle exception
            LOG.error("Challenge Error:" + e.toString());
            e.printStackTrace();
            throw new CapMessageException(CapAppContext.getMessage("SYS01"), getClass());
        }

        session.setAttribute(SESSION_ATTRIB, login);
        session.setAttribute(SESSION_LOG_OUT, logout);

        result.set("keyboard_type", login.getKeyboard_type());
        result.set("reader_name", login.getReaderName());
        result.set("sessionid", login.getSessionId());
        result.set("hex_sessionid", login.getHexSessionId());
        result.set("rand1", login.getRand1());
        result.set("rand1mac", login.getRand1mac());
        result.set("rand2", login.getRand2());
        result.set("encmappingtable", login.getEncMappingTable());
        result.set("pinindex", login.getPINIndex());

        return result;
    }

    public Result newsession(Request request) throws IOException {
        AjaxFormResult result = new AjaxFormResult();

        HttpSession session = getSession(request);
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
        // check component challenge MAC return
        String responseMAC = request.get("ICMAC");
        LOG.debug("Check LOGIN MAC " + responseMAC + " == " + login.getRand2mac());
        if (!responseMAC.equals(login.getRand2mac())) {
            throw new CapMessageException(CapAppContext.getMessage("MAC_ERROR"), getClass());
        }

        String e_act = request.get("E_ACCOUNT");
        String e_icremark = request.get("E_ICREMARK");
        String issuer_id = request.get("ISSUER_BankCode");
        String ACCOUNT_LIST = null;
        String ICREMARK = null;

        String etype = (String)session.getAttribute(SELECT_TYPE);
        String lastAccessPageNo = (String) session.getAttribute(SESSION_LAST_ACCESS_PAGENO);
        
        LOG.debug("E_ACCOUNT=" + e_act);

        WSSecurity wss = new WSSecurity((String) APSystem.getSYS_PRAM_MAP().get("WS_ADDR"), (String) APSystem.getSYS_PRAM_MAP().get("EC_SLOT"), WSS_SYS_ID, WSS_CHK_CODE, WSS_CHK_USR,
                (String) APSystem.getSYS_PRAM_MAP().get("EC_PWD"));

        try {
            ACCOUNT_LIST = wss.do3DESDecryptByValue(Misc.hex2Bin(login.getEncMappingTable().getBytes()), e_act);
            ACCOUNT_LIST = new String(Misc.hex2Bin(ACCOUNT_LIST.getBytes())).trim();
            ICREMARK = wss.do3DESDecryptByValue(Misc.hex2Bin(login.getEncMappingTable().getBytes()), e_icremark);
            ICREMARK = new String(Misc.hex2Bin(ICREMARK.getBytes())).trim();
        } catch (SAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOG.debug("SAPIException:" + e);
        }

        LOG.debug("Decrypt=[" + ACCOUNT_LIST + "]");
        LOG.debug("Decrypt=[" + ICREMARK + "]");


        StringTokenizer st = new StringTokenizer(ACCOUNT_LIST, ",");

        String[] saAccountList = new String[st.countTokens()];
        for (int i = 0; i < saAccountList.length; i++) {
            saAccountList[i] = st.nextToken();
            LOG.debug(saAccountList[i]);
        }

        int readertype = login.getReader_type();
        String readername = login.getReaderName();
        String keyboardtype = login.getKeyboard_type();// (String) session.getAttribute("_KEYBOARDTYPE");

        String issuer_account = saAccountList[0]; // request.getParameter("issuer_account");
        String icremark = ICREMARK; // (String) request.getParameter("ICREMARK");
        String hexSessionId = login.getHexSessionId();

        if (issuer_id == null || issuer_account == null) {

            throw new CapMessageException(CapAppContext.getMessage("ISSUER_ERROR"), getClass());
        }

        session.invalidate();

        session = getSession(request);

        APLogin login_temp = new APLogin(session.getId());
        APLogout logout_temp = new APLogout();

        login_temp.setReaderName(readername);
        login_temp.setIssuerBankCode(issuer_id); // 發卡行
        login_temp.setIssuerAccount(issuer_account); // 主帳號

        login_temp.setReader_type(readertype);
        login_temp.setKeyboard_type(keyboardtype);

        login_temp.setIssuer_remark(icremark);
        login_temp.setIssuerIP(request.getServletRequest().getRemoteHost());
        login_temp.setAccountList(saAccountList);
        login_temp.setHexSessionId(hexSessionId);

        session.setAttribute(SESSION_ATTRIB, login_temp);
        session.setAttribute(SESSION_LOG_OUT, logout_temp);
        session.setAttribute(SELECT_TYPE, etype);
        session.setAttribute(SESSION_LAST_ACCESS_PAGENO, lastAccessPageNo);
        
     // 完成後端處理後要記下允許至下一頁的 flag 給 checkpage 檢查
        request.<HttpServletRequest> getServletRequest().getSession().setAttribute(SESSION_CAN_ACCESS_TO_NEXTPAGE, true);
        result.set("nextPageURL", "page/signon");
        result.set("res", "sucess");

        return result;
    }

    public Result login(Request request) throws IOException {
        AjaxFormResult result = new AjaxFormResult();
        HttpSession session = getSession(request);
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
        APLogout logout = (APLogout) session.getAttribute(SESSION_LOG_OUT);

        try {
            // Processing
            String tNow = DateUtil.getCurrentTime("DT", "AD");
            // First Login
            if (login.getTmlID() == null || login.getTmlID().equalsIgnoreCase("")) {
                TxnTerminal txnTml = null;

                double timeOut;
                String SYS_STTS = "0";
                try {
                    timeOut = Double.parseDouble((String) (APSystem.getSYS_PRAM_MAP().get("UTOL")));

                    // Call RMI get SYS Status 2006.05.29 add by kevin
                    // ATM_STTS In SYS_PARM TABLE (0=> OFF / 1 => ON)
                    SYS_STTS = (String) (APSystem.getSYS_PRAM_MAP().get("ATM_STTS"));

                    LOG.debug("ATM_STTS=" + SYS_STTS);
                    if (SYS_STTS.equals("0"))
                        throw new CapMessageException(CapAppContext.getMessage("SYS16"), getClass());

                    // 到TerminalPool取出一個Terminal
                    int iReceiveTimeout = APSystem.getWaitSleepTime() * 1000;
                    int iWaitSleep = 1 * 1000; // TxnTml 接受不到Receive MsQ時Sleep間隔
                    int iRetryTimer = iReceiveTimeout / iWaitSleep;

                    txnTml = APSystemServiceImpl.TmlPool.getTerminal();

                    if (txnTml == null) {
                        for (int i = iRetryTimer; i > 0; i--) {
                            txnTml = APSystemServiceImpl.TmlPool.getTerminal();
                            Thread.sleep(iWaitSleep);
                            LOG.debug("getTerminal()");
                            if (txnTml != null) {
                                break;
                            }
                        }
                    }

                    if (txnTml == null)
                        throw new CapMessageException(CapAppContext.getMessage("ATM_ERROR"), getClass());
                    LOG.debug("Use TML_ID = " + txnTml.getTmlID());

                    ArrayList alField = new ArrayList();
                    alField.add(login.getSessionId()); // SessionID
                    alField.add(login.getIssuerBankCode()); // IssuerBankCode
                    alField.add(login.getIssuerAccount()); // IssuerAccount
                    alField.add(tNow); // ClientDT
                    alField.add("signin"); // OperFnct
                    alField.add(txnTml.getTmlID()); // TmlID
                    alField.add(""); // PCode
                    alField.add("0"); // LockFlag

                    doInsertIssuerLog(request, "signin", alField);

                } catch (CapException ex) {
                    LOG.debug(ex.getMessage());
                    throw ex;

                } catch (Exception ex) {
                    LOG.debug("RMI Error:" + ex.toString());
                    ex.printStackTrace();
                    throw new CapMessageException(CapAppContext.getMessage("RMI_ERROR"), getClass());
                }
                // put ATM to IssuerSession
                if (login.getTmlID() == null) {
                    // set ATM Info to login object
                    login.setTmlID(txnTml.getTmlID());
                    // login.setTxnTml(txnTml);

                    login.setAtmCheckCode("");

                    // set ATM Info to logout object
                    logout.setTmlID(txnTml.getTmlID());
                    logout.setSessionId(login.getSessionId());
                    logout.setIssuerBankCode(login.getIssuerBankCode());
                    logout.setIssuerAccount(login.getIssuerAccount());

                    session.setAttribute(SESSION_LOG_OUT, logout);

                    LOG.debug("logout TmlID:" + logout.getTmlID());
                }

                LOG.debug("TmlID:" + txnTml.getTmlID());
                APSystemServiceImpl.TmlPool.returnTerminal(txnTml.getTmlID());
                // put Date Time to IssuerSession
                login.setIssuerLoginDttm(tNow);
                login.setIssuerOperation("signin", tNow); // 登入
                login.setIssuerTimeOut(timeOut);
                String nextPage = null;
                String etype = (String)session.getAttribute(SELECT_TYPE);
                if(etype.equals("balance")){
                    
                    nextPage =  "page/balance"; 
                }else{
                    
                    nextPage = "page/transfer";
                }
                session.setAttribute(SESSION_ATTRIB, login);
             // 完成後端處理後要記下允許至下一頁的 flag 給 checkpage 檢查
                request.<HttpServletRequest> getServletRequest().getSession().setAttribute(SESSION_CAN_ACCESS_TO_NEXTPAGE, true);
                result.set("nextPageURL",nextPage);
                result.set("res", "sucess");

            }

        } catch (Exception ex) {
            LOG.debug(ex.getMessage());
            throw new CapMessageException(CapAppContext.getMessage("LOGIN_STATUS_ERR"), getClass());
        }
        return result;
    }

    public Result logout(Request request) throws IOException {
        AjaxFormResult result = new AjaxFormResult();
        HttpSession session = getSession(request);
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
        APLogout logout = (APLogout) session.getAttribute(SESSION_LOG_OUT);
        if (login != null) {
            login.clear();
            login.destroyObject();
            session.removeAttribute(SESSION_ATTRIB);
            session.invalidate();
            login = null;
            logout = null;
            LOG.debug("login object:" + login);
            LOG.debug("SessionID:" + session.getId());

        }
        result.set("nextPageURL", "page/login");
        result.set("res", "sucess");
        return result;
    }
    
    public Result menu(Request request) throws IOException {
        AjaxFormResult result = new AjaxFormResult();
        String type = request.get("E_TYPE");
        String nextPage = null;
        String issuerLogin = null;
        HttpSession session = getSession(request);
        session.setAttribute(SELECT_TYPE, type);
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
        if(login !=null)
        issuerLogin = login.getIssuerLoginDttm();
        if (issuerLogin != null) {
            
            switch (type) {
            case "balance":
                nextPage =  "page/balance"; 
                break;
            case "transfer":
                nextPage =  "page/transfer"; 
                break; 
            case "exchange":
                nextPage =  "page/rate_exchange"; 
                break;
            case "interest":
                nextPage =  "page/rate_interest"; 
                break;
            case "sitemap":
                nextPage =  "page/sitemap"; 
                break;       
            default:
                nextPage =  "page/login"; 
                break;
            }
            
           
            LOG.debug("login object:" + login);
            LOG.debug("SessionID:" + session.getId());

        }else{
            switch (type) {
            case "exchange":
                nextPage =  "page/rate_exchange"; 
                break;
            case "interest":
                nextPage =  "page/rate_interest"; 
                break; 
            case "sitemap":
                nextPage =  "page/sitemap"; 
                break;           
            default:
                nextPage =  "page/login"; 
                break;
            }
        }
     // 完成後端處理後要記下允許至下一頁的 flag 給 checkpage 檢查
        request.<HttpServletRequest> getServletRequest().getSession().setAttribute(SESSION_CAN_ACCESS_TO_NEXTPAGE, true);
        result.set("nextPageURL",nextPage);
        result.set("res", "sucess");
        return result;
    }
    
    public Result checkLogin(Request request) throws IOException {
        AjaxFormResult result = new AjaxFormResult();

        HttpSession session = getSession(request);
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
        String type = (String)session.getAttribute(SELECT_TYPE) != null ?(String)session.getAttribute(SELECT_TYPE):"balance";
        if (login != null) {
            result.set("type",type);
            result.set("Login", "1");
        }else{
            session.setAttribute(SELECT_TYPE, type);
            result.set("type",type);
            result.set("Login", "0");
        }

        return result;
    }
    
    public Result cleanData(Request request) throws IOException {
        AjaxFormResult result = new AjaxFormResult();
        HttpServletRequest httpRequest = (HttpServletRequest) request.getServletRequest();
        HttpSession session = getSession(request);
        String[] keepInSession = {SELECT_TYPE,"hex_sessionid","login","readerName","account"};
        Map<String, Object> _ar = new HashMap<String, Object>();

        Map<String, Object> old_ar = getAr(session);
        session.removeAttribute(CCConstants.ATTR_REDIRECT);

        for (String s : keepInSession) {
            _ar.put(s, MapUtils.getString(old_ar, s, ""));
        }
        Enumeration<String> params = httpRequest.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            for (String s : keepInSession) {
                if (s.equals(paramName)) {
                    _ar.put(paramName, httpRequest.getParameter(paramName));
                }
            }
        }
        session.setAttribute(CCConstants.ATTR_REDIRECT, _ar);

        return result;
    }
    
    public Result canNextPage(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        // 完成後端處理後要記下允許至下一頁的 flag 給 checkpage 檢查
        try {
            request.<HttpServletRequest> getServletRequest().getSession().setAttribute(SESSION_CAN_ACCESS_TO_NEXTPAGE, true);
            result.set("res", "sucess");
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        return result;
    }
    
    private Map<String, Object> getAr(HttpSession session) {
        return (Map<String, Object>) session.getAttribute(CCConstants.ATTR_REDIRECT);
    }

    public Result getRandKeypad(Request request) throws IOException {
        AjaxFormResult result = new AjaxFormResult();
        HttpSession session = getSession(request);
        // 取隨機產生的動態鍵盤(10位數字)
        int[] list = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        String[] stringlist = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
        // 長度
        int count = list.length;
        // 結果
        int[] resultInt = new int[count];
        LinkedList<Integer> temp = new LinkedList<Integer>();

        // 初始化temp
        for (int i = 0; i < count; i++) {
            temp.add((Integer) list[i]);
        }

        // 取數
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < count; i++) {
            int num = random.nextInt(count - i);
            resultInt[i] = (Integer) temp.get(num);

            temp.remove(num);
        }

        LOG.debug("RandKeypadList = " + resultInt);
        session.setAttribute(RAND_KEYPAD_LIST, resultInt);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("RandKeypadValue", stringlist);
        resultMap.put("RandKeypad", resultInt);
        result.putAll(resultMap);
        return result;
    }

    protected final HttpSession getSession(Request request) {
        return ((HttpServletRequest) request.getServletRequest()).getSession();
    }

    private String getRandNumString() {
        // 取隨機產生的認證碼(6位數字)
        StringBuffer sbRand = new StringBuffer();
        // double RndNum = java.lang.Math.random()*1000;
        double RndNum = 0.0;
        String sTemp = null;
        for (int i = 0; i < 4; i++) {
            RndNum = java.lang.Math.random() * 10000;
            sTemp = String.valueOf(new Double(RndNum).intValue());
            // System.out.println(sTemp);
            if (sTemp.length() != 4) {
                --i;
                continue;
            }
            sbRand.append(sTemp);
        }

        String rn = String.valueOf(RndNum).substring(0, 10);

        LOG.debug("RandNumString = " + sbRand.toString());
        return sbRand.toString();
    }

    private String getChallengeMAC(String sSession, String ChallengeData) {
        return getChallengeMAC(sSession, new String[] { ChallengeData })[0];
    }

    private String[] getChallengeMAC(String sSession, String[] ChallengeData) {
        try {

            // 計算Diversify Key
            String[] saMAC = new String[ChallengeData.length];

            WSSecurity wss = new WSSecurity((String) APSystem.getSYS_PRAM_MAP().get("WS_ADDR"), (String) APSystem.getSYS_PRAM_MAP().get("EC_SLOT"), WSS_SYS_ID, WSS_CHK_CODE, WSS_CHK_USR,
                    (String) APSystem.getSYS_PRAM_MAP().get("EC_PWD"));

            for (int i = 0; i < ChallengeData.length; i++) {
                // String ComMac = wss.getChallengeMAC(WSS_3DES_TYPE, WSS_3DES_KEY, ChallengeData[i], sSession);
                String DiversifyKey = wss.do3DESEncrypt(WSS_3DES_TYPE, WSS_3DES_KEY, sSession);
                String ComMac = CommonCryptoUtils.encryptByKeyValueForAWATM(ChallengeData[i], Misc.hex2Bin(DiversifyKey.getBytes()));

                if (ComMac != null) {
                    // System.out.println("Decrypt=" + wss.do3DESDecrypt(WSS_3DES_TYPE, WSS_3DES_KEY, ComMac, sSession));
                    ComMac = ComMac.substring(0, 16);
                }
                LOG.debug("Challeng MAC=" + ComMac);
                saMAC[i] = ComMac;

            }

            return saMAC;
        } catch (Exception e) {
            // TODO: handle exception
            LOG.error("getChallengeMAC Occur Error :" + e);
            e.printStackTrace();
        }
        return null;
    }

    private String getEncMappingTable(String sSession, String pinIndex) {
        try {
            String EncMappingTable;
            String DiversifyKey;

            WSSecurity wss = new WSSecurity((String) APSystem.getSYS_PRAM_MAP().get("WS_ADDR"), (String) APSystem.getSYS_PRAM_MAP().get("EC_SLOT"), WSS_SYS_ID, WSS_CHK_CODE, WSS_CHK_USR,
                    (String) APSystem.getSYS_PRAM_MAP().get("EC_PWD"));

            DiversifyKey = wss.do3DESEncrypt(WSS_3DES_TYPE, WSS_3DES_KEY, sSession);
            LOG.info("DiversifyKey=" + DiversifyKey);
            EncMappingTable = CommonCryptoUtils.encryptByKeyValueForAWATM(String.valueOf(Hex.encodeHex(pinIndex.getBytes("UTF-8"))), Misc.hex2Bin(DiversifyKey.getBytes()));
            LOG.info("EncMappingTable=" + EncMappingTable);

            return EncMappingTable;
        } catch (Exception e) {
            // TODO: handle exception
            LOG.error("getChallengeMAC Occur Error :" + e);
            e.printStackTrace();
        }
        return null;
    }

    private String getHexSessionid(String sSession) {
        String hexSessionid = null;
        if (sSession == null)
            sSession = "";
        byte[] baSession = new byte[24];

        if (sSession.length() <= 24) {
            System.arraycopy(sSession.getBytes(), 0, baSession, 0, sSession.length());
        } else {
            System.arraycopy(sSession.getBytes(), 0, baSession, 0, 24);
        }

        hexSessionid = new String(Misc.bin2Hex(baSession));

        return hexSessionid;
    }

    /**
     * 取得交易主機資料
     * 
     * @param null
     *            使用 SP_SysParm_sel_SrvTrnsData
     * @return 回傳 SysParm 的 HashMap 資料 只有getPramCode 與 getPramValue有值。
     */
    private final ArrayList getSysParmTrnsData() {
        TSysParmMgr SysParmMgr;
        // ArrayList alRtnData = null;
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        alCondition.add(sysProp.getProperty("systemidno"));
        alCondition.add("TRNS");
        LOG.info("method: - Start getTrnsSrvData()");
        try {
            SysParmMgr = (TSysParmMgr) Naming.lookup("rmi://" + APSystem.getRmiSrvName() + ":" + APSystem.getRmiSrvPort() + "/TSysParm");
            alRtnData = SysParmMgr.getArrayList_SP_SysParm(SP_Cmd_01, alCondition);
            if (alRtnData.size() != 0) {
                LOG.info("Init SYS_PRAM_MAP Finished.【" + alRtnData.size() + "】inserted.");
                return alRtnData;
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("getTrnsSrvData: 存取遠端 RMI 錯誤！");
            return null;
        } catch (Exception ex) {
            LOG.error("getTrnsSrvData: Other Exception:" + ex.getMessage());
            return null;
        } // try 1
        return null;
    }

    private final boolean doInsertIssuerLog(Request request, String operation, ArrayList alField) {
        TIssuerLogMgr IssuerLogMgr;
        LOG.info("operation:" + operation + "- Start doInsertIssuerLog");
        try {
            IssuerLogMgr = (TIssuerLogMgr) Naming.lookup("rmi://" + APSystem.getRmiSrvName() + ":" + APSystem.getRmiSrvPort() + "/TIssuerLog");
            int iRtn = IssuerLogMgr.insert_SP_IssuerLog(SP_Cmd_02, alField);

            if (iRtn == 0) {
                LOG.debug("insertIssuerLog: fail");
                throw new CapMessageException(CapAppContext.getMessage("DB_INS"), getClass());
            } else {
                LOG.info("insertIssuerLog: success");
                return true;
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("insertIssuerLog: 存取遠端 RMI 錯誤！");
            throw new CapMessageException(CapAppContext.getMessage("RMI_ERROR"), getClass());

        } catch (Exception ex) {
            LOG.error("insertIssuerLog: Other Exception:" + ex.getMessage());
            throw new CapMessageException(CapAppContext.getMessage("SYS10"), getClass());
        } // try 1
    }

}
