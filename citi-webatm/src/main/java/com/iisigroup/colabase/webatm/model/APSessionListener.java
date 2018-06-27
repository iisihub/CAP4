package com.iisigroup.colabase.webatm.model;

import static com.iisigroup.colabase.webatm.common.CCConstants.SESSION_ATTRIB;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class APSessionListener implements HttpSessionListener {

    public APSessionListener() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void sessionCreated(HttpSessionEvent arg0) {
        // TODO Auto-generated method stub
        APLogin.LOG.debug("sessionCreated");
    }

    public void sessionDestroyed(HttpSessionEvent arg0) {
        // TODO Auto-generated method stub
        APLogin.LOG.debug("sessionDestroyed");
        HttpSession session = arg0.getSession();
        APLogin.LOG.debug("Session_id:" + session.getId());
        APLogin login = (APLogin) session.getAttribute(SESSION_ATTRIB);
        if (login != null && login.getTmlID() != null && !login.getTmlID().equals("")) {
            login.destroyObject();
            login = null;
            session.removeAttribute(SESSION_ATTRIB);
            System.gc();
            login.LOG.debug("login change 2 null");
        } else
            APLogin.LOG.debug("login is null");
    }

}
