package com.iisigroup.colabase.model;

import java.util.List;
import java.util.Map;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/5/17 AndyChen,new
 *          </ul>
 * @since 2018/5/17
 */
public abstract class RequestContent extends RequestAbstract {

    enum HTTPMethod {
        GET("GET"), POST("POST"), PUT("PUT");

        private String methodName;

        HTTPMethod(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public String toString() {
            return methodName;
        }
    }

    /**
     * 是否使用自己的key store and trust store
     * key and trust store file path defined in config.properties
     */
    private boolean isUseOwnKeyAndTrustStore = true;

    /**
     * millisecond
     */
    private int timeout;

    /**
     * 重試次數 (millisecond)
     */
    private int retryTimes = 0;

    /**
     * 目標URL
     */
    private String targetUrl;

    /**
     * 使用HttpMethod
     */
    private HTTPMethod httpMethod;

    /**
     * 請求Header
     */
    private Map<String, List<String>> requestHeaders;

    /**
     * 需要重試的http status
     */
    private int[] retryHttpStatus;


    public boolean isUseOwnKeyAndTrustStore() {
        return isUseOwnKeyAndTrustStore;
    }

    public void setUseOwnKeyAndTrustStore(boolean useOwnKeyAndTrustStore) {
        isUseOwnKeyAndTrustStore = useOwnKeyAndTrustStore;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public HTTPMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HTTPMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, List<String>> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, List<String>> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public int[] getRetryHttpStatus() {
        return retryHttpStatus;
    }

    public void setRetryHttpStatus(int[] retryHttpStatus) {
        this.retryHttpStatus = retryHttpStatus;
    }
}
