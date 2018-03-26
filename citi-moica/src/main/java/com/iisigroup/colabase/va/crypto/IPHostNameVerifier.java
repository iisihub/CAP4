package com.iisigroup.colabase.va.crypto;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

public class IPHostNameVerifier implements HostnameVerifier {
    public boolean verify(String paramString, SSLSession paramSSLSession) {
        if (paramString.compareTo(paramSSLSession.getPeerHost()) != 0) {
            return false;
        }
        try {
            String str1 = paramSSLSession.getPeerCertificateChain()[0].getSubjectDN().toString();
            System.out.println(str1);
            int i = str1.indexOf("CN=");
            if (i == -1) {
                return false;
            }
            String str2 = str1.substring(i + 3, str1.indexOf(',', i));
            if (paramString.compareTo(str2) == 0) {
                return true;
            }
        } catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException) {
            localSSLPeerUnverifiedException.printStackTrace();
        }
        return false;
    }
}
