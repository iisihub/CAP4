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

/**
 * <pre>
 * HTTP Service
 * </pre>
 * 
 * @since 2017/5/31
 * @author TimChiang
 * @version <ul>
 *          <li>2017/5/31, new
 *          <li>2018/7/5, Lily, update
 *          </ul>
 */
public interface HttpService {
    
    public Result sendUrlEncodedForm(String sendUrl, String[] sendCols, Map<String, String> contents, boolean isTestMode);
    
    public Result sendJson(String sendUrl, String jsonStr, boolean isTestMode);

    public Result receiveData(Request request);
    
}
