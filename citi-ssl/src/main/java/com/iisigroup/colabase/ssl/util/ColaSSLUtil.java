package com.iisigroup.colabase.ssl.util;

import com.iisigroup.cap.utils.CapString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

import javax.net.ssl.*;

/**
 * @author AndyChen
 * @version
 *          <ul>
 *          <li>2018/9/27 AndyChen,new
 *          </ul>
 * @since 2018/9/27
 */
public class ColaSSLUtil {

    private final static Logger logger = LoggerFactory.getLogger(ColaSSLUtil.class);

    /**
     * 取得客製化的SSLSocketFactory
     * @param keyStorePath 自定義的java key store path
     * @param keyStorePWD key store password
     * @param trustStorePath 自定義的java trust store path
     * @return SSLSocketFactory物件
     */
    public static SSLSocketFactory getSSLSocketFactory(String keyStorePath, String keyStorePWD, String trustStorePath)
        throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        return getSSLSocketFactory("TLS", keyStorePath, keyStorePWD, trustStorePath);
    }

    /**
     * 取得客製化的SSLSocketFactory
     * @param protocol 自定義的protocol. ex: TLSv1.2
     * @param keyStorePath 自定義的java key store path
     * @param keyStorePWD key store password
     * @param trustStorePath 自定義的java trust store path
     * @return SSLSocketFactory物件
     */
    public static SSLSocketFactory getSSLSocketFactory(String protocol, String keyStorePath, String keyStorePWD, String trustStorePath)
            throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        final SSLSocketFactory factory;

        if(CapString.isEmpty(protocol))
            throw new IllegalArgumentException("protocol should NOT be empty");

        SSLContext sslContext = SSLContext.getInstance(protocol);
        //單純改變protocol
        if(CapString.isEmpty(keyStorePath) || CapString.isEmpty(keyStorePWD) || CapString.isEmpty(trustStorePath)) {
            sslContext.init(null, null, null);
            return sslContext.getSocketFactory();
        }

        try (InputStream keyStoreInputStream = new FileInputStream(keyStorePath); InputStream trustStoreInputStream = new FileInputStream(trustStorePath)) {
            // 讀取 Client KeyStore、TrustStore
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(keyStoreInputStream, keyStorePWD.toCharArray());
            keyStoreInputStream.close();

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePWD.toCharArray());

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(trustStoreInputStream, null);
            trustStoreInputStream.close();

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(trustStore);


            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            factory = sslContext.getSocketFactory();
        }

        return factory;
    }


    /**
     * 取得一個任何Hostname都能過的Verifier
     * @return HostnameVerifier
     */
    public static HostnameVerifier getAllowAllHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                logger.debug("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                return true;
            }
        };
    }

    /**
     * 取得一個任何憑證都能過的SSLSocketFactory，且使用預設protocol: TLS
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory getAllTrustSSLSocketFactory() throws KeyManagementException,
        NoSuchAlgorithmException {
        return getAllTrustSSLSocketFactory("TLS");
    }

    /**
     * 取得一個指定protocol的任何憑證都能過的SSLSocketFactory
     * @param protocol 指定protocol, ex: TLSv1.2
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory getAllTrustSSLSocketFactory(String protocol) throws KeyManagementException,
        NoSuchAlgorithmException {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new AllTrustManager();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance(protocol);
        sslContext.init(null, trustAllCerts, null);
        return sslContext.getSocketFactory();
    }

    private static class AllTrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
            return;
        }
    }

}
