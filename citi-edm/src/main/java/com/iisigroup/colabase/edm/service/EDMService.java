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


import com.iisigroup.cap.component.Result;
import com.iisigroup.colabase.edm.model.EdmSetting;

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
     * 寄送EDM
     * @param EdmSetting model
     */
    void sendEDM(EdmSetting edmSetting);
    
    /**
     * 寄送EDM from
     * @param String mail地址
     * @param byte 
     * @param Map<String, Object>
     */
    Result sendEDM(String mailAddress, byte[] datas, EdmSetting edmSetting);
    
}
