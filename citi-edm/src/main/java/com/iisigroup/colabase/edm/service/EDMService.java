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
     * 寄送EDM
     * dataMap參數對照:
     * @param edmFtlPath ftl的檔案路徑
     * @param mailAddress 欲寄送的目標mail
     * @param edmImageFileLocation ftl會用到的image"資料夾"路徑
     * @param edmSendFileLocation 附加檔案的路徑
     * @param fromAddress 發送者位置
     * @param fromPerson 發送者
     * @param edmHost 主機
     * @param edmUsr 使用者
     * @param edmPwd 密碼
     * @param edmSubject mail主旨
     */
    void sendEDM(Map<String, Object> dataMap);
    
    /**
     * 寄送EDM from
     * @param String mail地址
     * @param byte 
     * @param Map<String, Object>
     */
    Result sendEDM(String mailAddress, byte[] datas, Map<String, Object> dataMap);
    
}
