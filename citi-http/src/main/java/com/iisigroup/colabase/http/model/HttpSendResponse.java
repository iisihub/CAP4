package com.iisigroup.colabase.http.model;

import com.iisigroup.cap.component.impl.AjaxFormResult;

/**
 * <pre>
 * Http Send Response Info
 * </pre>
 * 
 * @since 2019年4月18日
 * @author LilyPeng
 * @version <ul>
 *          <li>2019年4月18日,LilyPeng,new
 *          </ul>
 */
public class HttpSendResponse extends AjaxFormResult {

    private String responseString;
    private int statusCode;

    /**
     * @return responseString
     */
    public String getResponseString() {
        return responseString;
    }

    /**
     * @param responseString
     *            response內容(JSON格式的字串)
     */
    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    /**
     * @return statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode
     *            狀態碼
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}
