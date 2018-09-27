package com.iisigroup.colabase.model;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/5/17 AndyChen,new
 *          </ul>
 * @since 2018/5/17
 */
public interface ApiRequest {
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

    enum SendType {
        POST_FORM, JSON
    }

    void afterSendRequest(ResponseContent responseContent);

    void showRequestJsonStrLog(String jsonStr);

}
