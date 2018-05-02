package com.iisigroup.colabase.model;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;


public class ResponseContent {
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
