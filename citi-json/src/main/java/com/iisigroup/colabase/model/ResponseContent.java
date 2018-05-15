package com.iisigroup.colabase.model;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResponseContent {

    private static Logger logger = LoggerFactory.getLogger(ResponseContent.class);

    private int statusCode;
    private Map<String, List<String>> headers;
    private JsonObject responseJson;
    private Exception exception;
    
    public ResponseContent() {
        
    }

    public ResponseContent(int statusCode, Map<String, List<String>> headers, JsonObject responseJson) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseJson = responseJson;
    }

    /**
     * 紀錄response中的json字串
     * 如果有針對特殊的呼叫(如送base64字串)導致過長，要自行override
     * @param jsonStr
     */
    public void showResponseJsonStrLog(String jsonStr) {
        logger.debug("Response responseData: " + jsonStr);
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setResponseJson(JsonObject responseJson) {
        this.responseJson = responseJson;
    }

    /**
     * HTTP Response Status Code
     * 
     * @return int
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * HTTP Headers
     * 
     * @return Map<String, List<String>>
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * HTTP Response JSON Object
     * 
     * @return JSONObject
     */
    public JsonObject getResponseJson() {
        return responseJson;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "HTTP Status Code = " + statusCode + ", Response JSON = " + responseJson;
    }
}
