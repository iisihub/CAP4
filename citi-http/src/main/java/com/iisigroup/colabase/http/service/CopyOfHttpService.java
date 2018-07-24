/* 
 * HTTPService.java
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

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

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
public interface CopyOfHttpService {
    
    public Result sendUrlEncodedForm(Map<String, String> request, String sendUrl, String[] sendCols, boolean isTestMode);
    
    public Result sendJSON(Map<String, String> request, String sendUrl, JSONObject json, boolean isTestMode);

    public Result receiveData(Request request);
    
}
