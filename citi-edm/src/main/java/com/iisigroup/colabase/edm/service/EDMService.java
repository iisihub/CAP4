/* 
 * EDMService.java
 * 
 * Copyright (c) 2009-2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.edm.service;

import java.util.Map;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;

/**<pre>
 * send EDM service
 * </pre>
 * @since  2018年4月30日
 * @author Johnson Ho
 * @version <ul>
 *           <li>2018年4月30日,Johnson Ho,new
 *          </ul>
 */
public interface EDMService {
    
    /**
     * 寄送EDM from
     * @param request
     */
    void sendEDM(Request request, String edmFtlPath, Map<String, Object> dataMap);
    
    /**
     * 寄送EDM from
     * @param String mail地址
     * @param byte 
     * @param String
     * @param request
     */
    Result sendEDM(String mailAddress, byte[] datas, Request request);
    
}
