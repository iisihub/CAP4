package com.iisigroup.colabase.json.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResponseContent implements ApiResponse {

    private static Logger logger = LoggerFactory.getLogger(ResponseContent.class);

    private int statusCode;
    private Map<String, List<String>> headers;
    private JsonObject responseJson;
    private Exception exception;
    
    private List<String> records = new ArrayList<>();

    public ResponseContent(int statusCode, Map<String, List<String>> headers, JsonObject responseJson, List<String> records) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseJson = responseJson;
        this.records = records;
    }


    @Override
    public void showResponseJsonStrLog(String jsonStr) {
        logger.debug("ApiResponse responseData: {}", jsonStr);
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
     * HTTP ApiResponse Status Code
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
     * HTTP ApiResponse JSON Object
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
        return "HTTP Status Code = " + statusCode + ", ApiResponse JSON = " + responseJson;
    }
}
