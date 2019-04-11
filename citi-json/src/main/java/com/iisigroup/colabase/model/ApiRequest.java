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

    /**
     * Call完API後所會執行的method
     * @param responseContent
     */
    void afterSendRequest(ResponseContent responseContent);

    /**
     * 在Call API時，若型態是JSON，預設會來執行此method
     * 紀錄request中的json字串
     * 如果有針對特殊的呼叫(如送base64字串)導致過長，要自行override
     * @param jsonStr 傳入所遞送的Json字串
     */
    void showRequestJsonStrLog(String jsonStr);

}
