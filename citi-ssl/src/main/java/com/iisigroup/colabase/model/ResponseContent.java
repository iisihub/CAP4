package com.iisigroup.colabase.model;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ResponseContent {
    private int statusCode;
    private Map<String, List<String>> headers;
    private JsonObject responseJson;
    private Exception exception;
    private List<String> records = new ArrayList<>();

    public ResponseContent() {
        
    }

    public ResponseContent(int statusCode, Map<String, List<String>> headers, JsonObject responseJson, List<String> records) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseJson = responseJson;
        this.records = records;
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

    public List<String> getRecords() {
        return records;
    }

    public void setRecords(List<String> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "HTTP Status Code = " + statusCode + ", Response JSON = " + responseJson;
    }
}
