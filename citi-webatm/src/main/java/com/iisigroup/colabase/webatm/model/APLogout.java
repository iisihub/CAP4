/*
 * @(#)APlogout.java
 *
 *<p>Copyright:Copyright (c) 2008</p>
 *<p>Company:Citi Group</p>
 *<p>ChangeLog:</p>
 *<p>v1.0 2008/07/07 by Kevin Chung</p>
 *<p>Author:Kevin</p>
 *<p>Version:V1.0</p>
 */
package com.iisigroup.colabase.webatm.model;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citi.webatm.rmi.TIssuerLogMgr;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.impl.APSystemServiceImpl;
import com.hitrust.trustatmtrns.util.DateUtil;
import com.iisigroup.cap.utils.CapAppContext;;

/**
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class APLogout implements HttpSessionBindingListener {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    private HttpSession Session = null;

    private String tml_id;
    private String session_id;
    private String issuer_id;
    private String issuer_account;

    private final static String SP_Cmd_01 = "SP_IssuerLog_ins";
    private APSystemService hsmService = CapAppContext.getApplicationContext().getBean(APSystemServiceImpl.class);;

    /**
     * valueUnbound
     *
     * @Parameter: HttpSessionBindingEvent
     * @return: none
     */

    public void valueUnbound(HttpSessionBindingEvent parm1) {
        // --- variable declaration -------------
        HttpSession session = parm1.getSession();

        LOG.debug("valueUnbound");
        LOG.debug("TmlID:" + tml_id);

        // end-add

        try {
            // APSystem.TmlPool.returnTerminal(tml_id);

            // Processing

            String tNow = DateUtil.getCurrentTime("DT", "AD");

            LOG.debug("sessionId:" + session_id + " tmlId:" + tml_id);

            if (session_id != null) {
                ArrayList alField = new ArrayList();
                alField.add(session_id); // SessionID
                alField.add(issuer_id); // IssuerBankCode
                alField.add(issuer_account); // IssuerAccount
                alField.add(tNow); // ClientDT
                alField.add("signout"); // OperFnct
                alField.add(tml_id); // TmlID
                alField.add(""); // PCode
                alField.add("0"); // LockFlag

                doInsertIssuerLog(alField);
            }
        } catch (Exception ex) {
            LOG.debug("valueUnbound exception " + ex.toString());
        }
    }

    final boolean doInsertIssuerLog(ArrayList alField) {
        TIssuerLogMgr IssuerLogMgr;
        LOG.info("operation:logout- Start doInsertIssuerLog");
        try {
            IssuerLogMgr = (TIssuerLogMgr) Naming.lookup("rmi://" + hsmService.getRmiSrvName() + ":" + hsmService.getRmiSrvPort() + "/TIssuerLog");
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

    public void setTmlID(String TmlID) {
        tml_id = TmlID;
    }

    public String getTmlID() {
        return tml_id;
    }

    /**
     * set Issuser Session ID.
     */
    public void setSessionId(String sessionId) {
        this.session_id = sessionId;
    }

    /**
     * get Issuser ID.
     */
    public String getIssuerBankCode() {
        return this.issuer_id;
    }

    /**
     * set Issuser ID.
     */
    public void setIssuerBankCode(String issuerId) {
        this.issuer_id = issuerId;
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

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueBound(HttpSessionBindingEvent arg0) {
        // Auto-generated method stub
        this.Session = arg0.getSession();
    }
}
