package com.iisigroup.colabase.base.va.crypto;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IPHostName Verifier
 * 
 * @since Mar 14, 2018
 * @author Cathy
 * @version
 *          <ul>
 *          <li>Mar 14, 2018,Cathy,new
 *          </ul>
 */
public class IPHostNameVerifier implements HostnameVerifier {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public boolean verify(String paramString, SSLSession paramSSLSession) {
        if (paramString.compareTo(paramSSLSession.getPeerHost()) != 0) {
            return false;
        }
        try {
            String str1 = paramSSLSession.getPeerCertificateChain()[0].getSubjectDN().toString();
            logger.info("IPHostNameVerifier {}", str1);
            int i = str1.indexOf("CN=");
            if (i == -1) {
                return false;
            }
            String str2 = str1.substring(i + 3, str1.indexOf(',', i));
            if (paramString.compareTo(str2) == 0) {
                return true;
            }
        } catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException) {
            logger.error("IPHostNameVerifier error", localSSLPeerUnverifiedException);
        }
        return false;
    }
}
