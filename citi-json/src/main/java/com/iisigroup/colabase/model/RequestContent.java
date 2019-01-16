package com.iisigroup.colabase.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/5/17 AndyChen,new
 *          </ul>
 * @since 2018/5/17
 */
public abstract class RequestContent extends JsonAbstract implements ApiRequest {


    private static final Logger logger = LoggerFactory.getLogger(RequestContent.class);

    /**
     * 是否使用自己的key store and trust store
     * key and trust store file path defined in config.properties
     */
    private boolean isUseOwnKeyAndTrustStore = false;

    /**
     * API的名稱
     */
    private String apiName;

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
     * 請求Header
     */
    private Map<String, List<String>> requestHeaders = new LinkedHashMap<>();

    /**
     * 需要重試的http status
     */
    private int[] retryHttpStatus;

    /**
     * 要送出的Data格式
     */
    private SendType sendType;

    /**
     * 是否忽略檢查server SSL憑證
     */
    private boolean isIgnoreSSLcert = false;

    /**
     * SslClientImpl default protocol is TLS
     * following params u can use:
     * SSL	    Supports some version of SSL; may support other versions
     * SSLv2	Supports SSL version 2 or later; may support other versions
     * SSLv3	Supports SSL version 3; may support other versions
     * TLS	    Supports some version of TLS; may support other versions
     * TLSv1	Supports RFC 2246: TLS version 1.0 ; may support other versions
     * TLSv1.1	Supports RFC 4346: TLS version 1.1 ; may support other versions
     * TLSv1.2	Supports RFC 5246: TLS version 1.2 ; may support other versions
     */
    private String protocol;

    /**
     * 紀錄request中的json字串
     * 如果有針對特殊的呼叫(如送base64字串)導致過長，要自行override
     * @param jsonStr
     */
    @Override
    public void showRequestJsonStrLog(String jsonStr) {
        logger.debug("ApiRequest: SendData = {}", jsonStr);
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

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public SendType getSendType() {
        return sendType;
    }

    public void setSendType(SendType sendType) {
        this.sendType = sendType;
    }

    public boolean isIgnoreSSLcert() {
        return isIgnoreSSLcert;
    }

    public void setIgnoreSSLcert(boolean ignoreSSLcert) {
        isIgnoreSSLcert = ignoreSSLcert;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
