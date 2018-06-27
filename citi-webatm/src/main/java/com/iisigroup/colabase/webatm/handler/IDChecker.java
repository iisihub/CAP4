package  com.iisigroup.colabase.webatm.handler;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citi.webatm.rmi.TIssuerLogMgr;
import com.iisigroup.colabase.webatm.model.APLogin;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl;
import com.iisigroup.colabase.webatm.common.CCConstants;
import com.hitrust.trustatmtrns.util.DateUtil;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.utils.CapAppContext;

import tw.com.citi.webatm.txn.TxnProcess01;
import tw.com.citi.webatm.txn.TxnTerminal;

public class IDChecker {
    private static APSystemService APSystem = CapAppContext.getApplicationContext().getBean(APSystemServiceImpl.class);

    static Logger LOG = LoggerFactory.getLogger("IDChecker");

    public static boolean CheckCard(Request request, APLogin login) {
        String AUTHCODE = request.get("AUTHCODE");
        String trnsCode = null;
        boolean txnResultFlag = true;
        boolean resultFlag = false;

        // Processing
        try {
            TxnTerminal txnTml = null;
            // txnTml = login.getTxnTml();
            txnTml = getTemporaryTerminal(login.getTmlID());
            TxnProcess01 txnProcess = new TxnProcess01(txnTml);

            if (login.getCiti_CARD().equalsIgnoreCase("true")) {
                trnsCode = CITI_BALANCE_TRNS;
                txnProcess.setOnUsCard(true);
            } else {
                trnsCode = OTHER_BALANCE_TRNS;
                txnProcess.setOnUsCard(false);
            }

            txnProcess.setIssuerBankCode(login.getIssuerBankCode().substring(0, 3));
            txnProcess.setIssuerAccount(login.getIssuerAccount());
            txnProcess.setPCode(trnsCode);
            txnProcess.setSourceAccount(login.getAccountList()[0]);
            txnProcess.setICCTxnNo(request.get("ICSEQ"));
            txnProcess.setICCRemark(login.getIssuer_remark());
            txnProcess.setTmlCheckCode(AUTHCODE + AUTHCODE);
            txnProcess.setTmlType(getTRMTYPE(login.getReader_type()));
            txnProcess.setTAC(request.get("ICTAC"));
            txnProcess.setMAC(request.get("ICMAC"));
            txnProcess.setSessionID(login.getSessionId());
            txnProcess.setClientIP(login.getIssuerIP());
            txnProcess.setClientDT(request.get("TXNDT"));
            txnProcess.setAuthFlag("1");
            txnResultFlag = txnProcess.doTxn();

            if (txnResultFlag) {
                LOG.debug("交易序號     = " + txnTml.getMsgSeqNo());
                LOG.debug("端末機號     = " + txnTml.getTmlID());
                LOG.debug("交易結果     = " + txnProcess.getTrnsStatus());
                LOG.debug("錯誤代碼     = " + txnProcess.getRespCode());
                LOG.debug("可用餘額     = " + txnProcess.getAvailableBalance());
                LOG.debug("帳戶餘額     = " + txnProcess.getAccountBalance());
                LOG.debug("手續費         = " + "0");
                LOG.debug("STAN      = " + txnProcess.getSTAN());
                LOG.debug("交易日期時間 = " + txnProcess.getHostDT());

                if (txnProcess.getActionCode().equals("000")) {
                    resultFlag = true;
                } else {
                    resultFlag = false;
                }

            } else {
                resultFlag = false;
            }
            APSystemServiceImpl.TmlPool.returnTerminal(login.getTmlID());

            String tNow = DateUtil.getCurrentTime("DT", "AD");
            ArrayList alField = new ArrayList();
            alField.add(login.getSessionId()); // SessionID
            alField.add(login.getIssuerBankCode()); // IssuerBankCode
            alField.add(login.getIssuerAccount()); // IssuerAccount
            alField.add(tNow); // ClientDT
            alField.add(trnsCode + "CardChecker"); // OperFnct
            alField.add(login.getTmlID()); // TmlID
            alField.add(trnsCode); // PCode
            alField.add("0"); // LockFlag

            if (doInsertIssuerLog(request, "CardChecker", alField) == false) {
                throw new CapMessageException(CapAppContext.getMessage("RMI_ERROR"), IDChecker.class);
            }

        } catch (Exception e) {
            LOG.error("Exception：" + e.getMessage());
            // e.printStackTrace();
            request.put("_error", e.getMessage());
            return false;
        } // try

        return resultFlag;
    }

    protected static boolean verifyID(Request request, APLogin login) throws CapException {
        HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession(false);
        if (session == null) {
            throw new CapMessageException(CapAppContext.getMessage("SYS04"), IDChecker.class);
        }

        // 20060720 Frank：新增處理步驟－－身份檢核
        if (login.getHAS_VERIFY().equalsIgnoreCase("false")) {
            String AUTHCODE = request.get("AUTHCODE");

            // Check 驗證碼
            LOG.info(new StringBuffer("AUTHCODE：").append(AUTHCODE).append("-Session_AUTHCODE:").append(login.getAUTHCODE()).toString());
            if (!AUTHCODE.equals(login.getAUTHCODE())) {
                login.setAUTHCODE(null);
                String errMessage = CapAppContext.getMessage("AUTHCODE_ERR");
                request.put("_error_GOPT", errMessage);
                request.put("_error", errMessage);
                return false;
            }
            if (CheckCard(request, login) == false) {
                LOG.error("<ERR> 檢核晶片卡身份錯誤！");
                // login.setCAN_TXNQ(null);
                // login.setCiti_CARD(null);
                // login.setHAS_VERIFY("false");
                String errMessage = CapAppContext.getMessage("ERR_CARD_VERIFY");
                // 因需導向至合宜的Page，只好先用驗證碼錯誤導向頁面漏
                request.put("_error_GOPT", errMessage);
                request.put("_error", errMessage);
                return false;
            }
            login.setHAS_VERIFY("true");
        }
        return true;
    }

    protected static final boolean doInsertIssuerLog(Request request, String operation, ArrayList alField) {
        TIssuerLogMgr IssuerLogMgr;
        LOG.info("operation:" + operation + "- Start doInsertIssuerLog");
        try {
            IssuerLogMgr = (TIssuerLogMgr) Naming.lookup("rmi://" + APSystem.getRmiSrvName() + ":" + APSystem.getRmiSrvPort() + "/TIssuerLog");
            int iRtn = IssuerLogMgr.insert_SP_IssuerLog(SP_Cmd_01, alField);

            if (iRtn == 0) {
                LOG.debug("insertIssuerLog: fail");
                return false;
            } else {
                LOG.info("insertIssuerLog: success");
                return true;
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("insertIssuerLog: 存取遠端 RMI 錯誤！");

        } catch (Exception ex) {
            LOG.error("insertIssuerLog: Other Exception:" + ex.getMessage());
        } // try 1
        return false;
    }

    /**
     * 取得端末設備型態 具密碼輸入器之確認型讀卡機：6536 不具密碼輸入器之讀卡機：6534
     * 
     * @param reader_type
     * @return Creation date:(2007/12/13 上午 11:33:41)
     */
    private static String getTRMTYPE(int reader_type) {
        if ((reader_type == CCConstants.READER_TYPE_KEYPAD) || (reader_type == CCConstants.READER_TYPE_KEYPAD_II)) {
            return "6536";
        } else {
            return "6534";
        }

    }

    protected static TxnTerminal getTemporaryTerminal(String tmlid) throws CapException {
        TxnTerminal txnTml = null;
        // 到TerminalPool取出一個Terminal
        int iReceiveTimeout = APSystemServiceImpl.waitTime * 1000;
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
                LOG.debug("getTerminal()");
                if (txnTml != null) {
                    break;
                }
            }
        }

        if (txnTml == null)
            throw new CapMessageException(CapAppContext.getMessage("ATM_ERROR"), IDChecker.class);

        LOG.debug("Use TML_ID = " + txnTml.getTmlID());
        return txnTml;
    }

    public static final String CITI_BALANCE_TRNS = "2590";
    public static final String OTHER_BALANCE_TRNS = "2500";

    private final static String SP_Cmd_01 = "SP_IssuerLog_ins";
}
