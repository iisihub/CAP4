package com.iisigroup.colabase.json.model;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2019/1/19 AndyChen,new
 * </ul>
 * @since 2019/1/19
 */
public class ProxyConfig {

    private String host;
    private int port;
    private boolean isUseProxy = false;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isUseProxy() {
        return isUseProxy;
    }

    public void setUseProxy(boolean useProxy) {
        isUseProxy = useProxy;
    }
}
