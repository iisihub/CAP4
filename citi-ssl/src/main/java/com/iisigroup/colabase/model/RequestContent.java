package com.iisigroup.colabase.model;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/4/9 AndyChen,new
 *          </ul>
 * @since 2018/4/9
 */
public abstract class RequestContent {

    public enum HTTPMethod {
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 是否用建構式產生的key store and trust store
     */
    private boolean isUseOwnKeyAndTrustStore = true;

    /**
     * millisecond
     */
    private int timeout;

    /**
     * 重試次數
     */
    private int retryTimes = 0;

    private String targetUrl;

    private HTTPMethod httpMethod;

    private JsonObject requestContent;

    private Map<String, List<String>> requestHeaders;

    private int[] retryHttpStatus;

    public abstract void afterSendRequest(ResponseContent responseContent);

    /**
     * 紀錄request中的json字串
     * 如果有針對特殊的呼叫(如送base64字串)導致過長，要自行override
     * @param jsonStr
     */
    public void showRequestJsonStrLog(String jsonStr) {

        logger.debug("Request: SendData = {}", jsonStr);
    }

    /**
     * 紀錄response中的json字串
     * 如果有針對特殊的呼叫(如送base64字串)導致過長，要自行override
     * @param jsonStr
     */
    public void showResponseJsonStrLog(String jsonStr) {
        logger.debug("Response responseData: " + jsonStr);
    }

    public boolean isUseOwnKeyAndTrustStore() {
        return isUseOwnKeyAndTrustStore;
    }

    public void setUseOwnKeyAndTrustStore(boolean useOwnKeyAndTrustStore) {
        isUseOwnKeyAndTrustStore = useOwnKeyAndTrustStore;
    }

    public int getTimeout() {
        return timeout;
    }

    /**
     * setting timeout time
     * @param timeout milliseconds
     */
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

    public JsonObject getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(JsonObject requestContent) {
        this.requestContent = requestContent;
    }

    public Map<String, List<String>> getRequestHeaders() {
        return this.requestHeaders;
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
