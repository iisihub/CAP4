package com.iisigroup.colabase.model;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public class ResponseContent {
    private int statusCode;
    private Map<String, List<String>> headers;
    private JSONObject responseJson;
    private Exception exception;
    
    public ResponseContent() {
        
    }

    public ResponseContent(int statusCode, Map<String, List<String>> headers, JSONObject responseJson) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseJson = responseJson;
    }
    

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setResponseJson(JSONObject responseJson) {
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
    public JSONObject getResponseJson() {
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
