package com.iisigroup.colabase.otp.va.crypto;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iisigroup.colabase.base.va.crypto.IPHostNameVerifier;

/**
 * Https Connection Opener
 * 
 * @since Mar 14, 2018
 * @author Cathy
 * @version
 *          <ul>
 *          <li>Mar 14, 2018,Cathy,new
 *          </ul>
 */
public class HttpsConnectionOpener implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(HttpsConnectionOpener.class);
    private String protocol;
    private String host;
    private String port;
    private String entry;
    private String proxyHost;
    private String proxyPort;
    private boolean proxyEnable;
    private HttpsURLConnection connection;

    /**
     * Https Connection Opener
     * 
     * @param protocol
     *            Protocol
     * @param host
     *            Host
     * @param port
     *            Port
     * @param entry
     *            Entry
     */
    public HttpsConnectionOpener(String protocol, String host, String port, String entry) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.entry = entry;
        proxyEnable = false;
        proxyHost = null;
        proxyPort = null;
        connection = null;
    }

    /**
     * Https Connection Opener
     * 
     * @param protocol
     *            Protocol
     * @param host
     *            Host
     * @param port
     *            Port
     * @param entry
     *            Entry
     * @param proxyHost
     *            Proxy Host
     * @param proxyPort
     *            Proxy Port
     */
    public HttpsConnectionOpener(String protocol, String host, String port, String entry, String proxyHost, String proxyPort) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.entry = entry;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        proxyEnable = true;
        connection = null;
    }

    @Override
    public void run() {
        try {
            if (proxyEnable) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
                connection = this.getHttpConnection(protocol, host, port, proxy);
            } else {
                connection = this.getHttpConnection(protocol, host, port, null);
            }
        } catch (Exception e) {
            logger.error("run error", e);
        }

    }

    /**
     * Open Https URL Connection
     * 
     * @param protocol
     *            Protocol
     * @param host
     *            Host
     * @param port
     *            Port
     * @param entry
     *            Entry
     * @param timeout
     *            Timeout
     * @param proxyEnable
     *            Proxy Enable
     * @param proxyHost
     *            Proxy Host
     * @param proxyPort
     *            Proxy Port
     * @return HttpsConnectionOpener Connection
     */
    public static HttpsURLConnection openConnection(String protocol, String host, String port, String entry, int timeout, boolean proxyEnable, String proxyHost, String proxyPort) {
        HttpsConnectionOpener opener = proxyEnable ? new HttpsConnectionOpener(protocol, host, port, entry, proxyHost, proxyPort) : new HttpsConnectionOpener(protocol, host, port, entry);
        Thread t = new Thread(opener);
        t.start();
        try {
            t.join(timeout);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        return opener.getConnection();
    }

    /**
     * Get Https URL Connection
     * 
     * @return Https connection
     */
    public HttpsURLConnection getConnection() {
        return connection;
    }

    /**
     * Get Http Connection
     * 
     * @param protocol
     * @param host
     * @param port
     * @param proxy
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     */
    private HttpsURLConnection getHttpConnection(String protocol, String host, String port, Proxy proxy) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        String str = protocol + "://" + host + ":" + port + entry;
        URL localURL = new URL(str);
        HttpsURLConnection localHttpsURLConnection;
        if (proxy == null) {
            localHttpsURLConnection = (HttpsURLConnection) localURL.openConnection();
        } else {
            localHttpsURLConnection = (HttpsURLConnection) localURL.openConnection(proxy);
        }
        if ("https".equalsIgnoreCase(protocol)) {
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // Do nothing.
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // Do nothing.
                }
            } };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            localHttpsURLConnection.setSSLSocketFactory(sc.getSocketFactory());
        }
        localHttpsURLConnection.setHostnameVerifier(new IPHostNameVerifier());
        localHttpsURLConnection.setDoOutput(true);
        return localHttpsURLConnection;
    }

}
