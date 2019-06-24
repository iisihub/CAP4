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

/**
 * <pre>
 * send EDM service
 * </pre>
 * 
 * @since 2018年4月30日
 * @author Johnson Ho
 * @version
 *          <ul>
 *          <li>2018年4月30日,Johnson Ho,new
 *          </ul>
 */
public interface EDMService {

    /**
     * 寄送EDM
     * 
     * @param EdmSetting
     *            Edm相關設定參數
     */
    void sendEDM(EdmSetting edmSetting);

    /**
     * @param mailAddress
     *            收件人email
     * @param datas
     *            byte[]
     * @param edmSetting
     *            Edm相關設定參數
     * @return
     */
    Result sendEDM(String mailAddress, byte[] datas, EdmSetting edmSetting);

}
