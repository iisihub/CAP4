package com.iisigroup.colabase.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.iisigroup.colabase.annotation.JsonTemp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/4/9 AndyChen,new
 *          </ul>
 * @since 2018/4/9
 */
public abstract class RequestContent implements RequestFather{

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
     * 重試次數
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
     * 請求內容JsonObject型態表示
     */
    private JsonObject requestContent;

    /**
     * 請求Header
     */
    private Map<String, List<String>> requestHeaders;

    /**
     * 需要重試的http status
     */
    private int[] retryHttpStatus;

    /**
     * !!! do not use this map yourself
     */
    private Map<String, Object> arrayMap = new HashMap<>();

    /**
     * !!! do not use this list yourself
     */
    private List<String> noSendList = new ArrayList<>();

    @Override
    public String getJsonString() {
        return requestContent.toString();
    }

    /**
     * 紀錄request中的json字串
     * 如果有針對特殊的呼叫(如送base64字串)導致過長，要自行override
     * @param jsonStr
     */
    @Override
    public void showRequestJsonStrLog(String jsonStr) {
        logger.debug("Request: SendData = " + jsonStr);
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

    @Override
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
