package com.iisigroup.colabase.http.model;

import com.iisigroup.cap.component.impl.AjaxFormResult;

/**
 * <pre>
 * Http Receive Request Info
 * </pre>
 * 
 * @since 2019年4月18日
 * @author LilyPeng
 * @version <ul>
 *          <li>2019年4月18日,LilyPeng,new
 *          </ul>
 */
public class HttpReceiveRequest extends AjaxFormResult {

    private String requestString;
    private int statusCode;

    /**
     * @return requestString
     */
    public String getRequestString() {
        return requestString;
    }

    /**
     * @param requestString
     *            request內容(JSON格式的字串)
     */
    public void setRequestString(String requestString) {
        this.requestString = requestString;
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
