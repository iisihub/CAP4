package com.iisigroup.colabase.va.crypto;

import java.net.InetSocketAddress;

import java.net.Proxy;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    private String protocol;
    private String host;
    private String port;
    private String entry;
    private String proxyHost;
    private String proxyPort;
    private boolean proxyEnable;
    private HttpsURLConnection connection;

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
            e.printStackTrace();
        }

    }

    public static HttpsURLConnection openConnection(String protocol, String host, String port, String entry, int timeout, boolean proxyEnable, String proxyHost, String proxyPort) {
        HttpsConnectionOpener opener = proxyEnable ? new HttpsConnectionOpener(protocol, host, port, entry, proxyHost, proxyPort) : new HttpsConnectionOpener(protocol, host, port, entry);
        Thread t = new Thread(opener);
        t.start();
        try {
            t.join(timeout);
        } catch (InterruptedException exception) {
        }
        return opener.getConnection();
    }

    public HttpsURLConnection getConnection() {
        return connection;
    }

    private HttpsURLConnection getHttpConnection(String protocol, String host, String port, Proxy proxy) throws Exception {
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
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
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
