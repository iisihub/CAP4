package com.iisigroup.colabase.otp.model;

/**
 * SMS Config
 * 
 * @since Apr 15, 2019
 * @author Cathy
 * @version
 *          <ul>
 *          <li>Apr 15, 2019,Cathy,new
 *          </ul>
 */
public class SmsConfig {

    /** SMS host */
    private String host;
    /** SMS entry */
    private String entry;
    /** SMS port */
    private String port;
    /** SMS user name */
    private String username;
    /** SMS password */
    private String password;
    /** SMS encoding */
    private String encoding;
    /** Proxy enable */
    private String proxyEnable;
    /** Proxy host */
    private String proxyHost;
    /** Proxy port */
    private String proxyPort;

    /**
     * SMS Config
     */
    public SmsConfig() {
    }

    /**
     * 
     * @param host
     *            SMS host
     * @param entry
     *            SMS entry
     * @param port
     *            SMS port
     * @param username
     *            SMS user name
     * @param password
     *            SMS password
     * @param encoding
     *            SMS encoding
     * @param proxyEnable
     *            Proxy enable
     * @param proxyHost
     *            Proxy host
     * @param proxyPort
     *            Proxy port
     */
    public SmsConfig(String host, String entry, String port, String username, String password, String encoding, String proxyEnable, String proxyHost, String proxyPort) {
        this.host = host;
        this.entry = entry;
        this.port = port;
        this.username = username;
        this.password = password;
        this.encoding = encoding;
        this.proxyEnable = proxyEnable;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    /**
     * Get SMS Host
     * 
     * @return SMS host
     */
    public String getHost() {
        return host;
    }

    /**
     * Set SMS Host
     * 
     * @param host
     *            SMS Host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Get SMS Entry
     * 
     * @return SMS entry
     */
    public String getEntry() {
        return entry;
    }

    /**
     * Set SMS Entry
     * 
     * @param entry
     *            SMS Entry
     */
    public void setEntry(String entry) {
        this.entry = entry;
    }

    /**
     * Get SMS Port
     * 
     * @return SMS Port
     */
    public String getPort() {
        return port;
    }

    /**
     * Set SMS Port
     * 
     * @param port
     *            SMS Port
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Get SMS User Name
     * 
     * @return SMS User Name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set SMS User Name
     * 
     * @param username
     *            SMS User Name
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Set SMS Password
     * 
     * @return SMS Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get SMS Password
     * 
     * @param password
     *            SMS Password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get SMS Encoding
     * 
     * @return SMS Encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Set SMS Encoding
     * 
     * @param encoding
     *            SMS Encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Get Proxy Enable
     * 
     * @return Proxy Enable
     */
    public String getProxyEnable() {
        return proxyEnable;
    }

    /**
     * Set Proxy Enable
     * 
     * @param proxyEnable
     *            Proxy Enable
     */
    public void setProxyEnable(String proxyEnable) {
        this.proxyEnable = proxyEnable;
    }

    /**
     * Get Proxy Host
     * 
     * @return Proxy Host
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * Set Proxy Host
     * 
     * @param proxyHost
     *            Proxy Host
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * Get Proxy Port
     * 
     * @return Proxy Port
     */
    public String getProxyPort() {
        return proxyPort;
    }

    /**
     * Get Proxy Port
     * 
     * @param proxyPort
     *            Proxy Port
     */
    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

}
