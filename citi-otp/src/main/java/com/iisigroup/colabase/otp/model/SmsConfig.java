package com.iisigroup.colabase.otp.model;

public class SmsConfig {

    private String host;
    private String entry;
    private String port;
    private String username;
    private String password;
    private String encoding;
    private String proxyEnable;
    private String proxyHost;
    private String proxyPort;

    public SmsConfig() {
    }

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getProxyEnable() {
        return proxyEnable;
    }

    public void setProxyEnable(String proxyEnable) {
        this.proxyEnable = proxyEnable;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

}
