package com.iisigroup.colabase.model;

import com.google.gson.JsonObject;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/5/11 AndyChen,new
 *          </ul>
 * @since 2018/5/11
 */
public interface RequestFather {

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

    void afterSendRequest(ResponseContent responseContent);

    JsonObject getRequestContent();

    String getJsonString();

    void showRequestJsonStrLog(String jsonStr);

}
