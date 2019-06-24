/* 
 * HttpService.java
 * 
 * Copyright (c) 2009-2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.http.service;

import java.util.Map;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.colabase.http.model.HttpReceiveRequest;
import com.iisigroup.colabase.http.model.HttpSendResponse;

/**
 * <pre>
 * HTTP Service
 * </pre>
 * 
 * @since 2017年5月31日
 * @author TimChiang
 * @version <ul>
 *          <li>2017年5月31日,TimChiang,new
 *          </ul>
 */
public interface HttpService {

    /**
     * @param sendUrl
     *            目標URL
     * @param sendCols
     *            傳送欄位
     * @param contents
     *            有存放欄位與對應值的Map
     * @return result
     */
    public HttpSendResponse sendUrlEncodedForm(String sendUrl, String[] sendCols, Map<String, String> contents);

    /**
     * @param sendUrl
     *            目標URL
     * @param jsonStr
     *            JSON字串
     * @return result
     */
    public HttpSendResponse sendJson(String sendUrl, String jsonStr);

    /**
     * @param request
     *            request
     * @return result
     */
    public HttpReceiveRequest receiveData(Request request);

}
