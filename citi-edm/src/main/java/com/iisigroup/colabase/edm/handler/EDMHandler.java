/* 
 * EDMHandler.java
 * 
 * Copyright (c) 2009-2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.edm.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.colabase.edm.model.EdmSetting;
import com.iisigroup.colabase.edm.service.EDMService;

/**
 * <pre>
 * Demo EDM功能 handler
 * </pre>
 * 
 * @since 2018年4月30日
 * @author Johnson Ho
 * @version
 *          <ul>
 *          <li>2018年4月30日,Johnson Ho,new
 *          </ul>
 */

@Controller("demoedmhandler")
public class EDMHandler extends MFormHandler {

    @Autowired
    private EDMService edmService;

    private static final String RESULT = "result";

    /**
     * @param request
     * @return
     */
    public Result sendEdmDemo(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String mailAddress = null;
        String edmFtlPath = null;
        String edmCustomerName = null;
        String edmProject = null;
        try {
            mailAddress = request.get("mailAddress");
            edmFtlPath = request.get("edmFtlPath");
            // 對應ftl內的變數
            edmCustomerName = request.get("edmCustomerName");
            edmProject = request.get("edmProject");

            // 給EDM值
            EdmSetting edmSetting = new EdmSetting();
            edmSetting.setEdmFtlPath("report/" + edmFtlPath);
            edmSetting.setMailAddress(mailAddress);
            edmSetting.setFromAddress("citi@imta.citicorp.com");
            edmSetting.setFromPerson("花旗（台灣）銀行");
            edmSetting.setEdmHost("smtp.gmail.com");
            edmSetting.setEdmUsr("css123456tw@gmail.com");
            edmSetting.setEdmPwd("kvzulwkqdoiprtfb");
            edmSetting.setEdmSubject("花旗(台灣)銀行 圓滿貸線上申請確認通知函");
            edmSetting.setEdmAttachedFilePath("../citi-edm/src/test/resources/ftl/colabaseDemo/edmImages/kv.jpg");
            edmSetting.setEdmImageFileFolder("../citi-edm/src/test/resources/ftl/colabaseDemo/edmImages");

            Map<String, Object> ftlVar = new HashMap<String, Object>();
            ftlVar.put("otherAccountTitleMask", edmCustomerName);
            ftlVar.put("otherAccountNumberMask", edmProject);
            edmSetting.setMappingFtlVar(ftlVar);

            edmService.sendEDM(edmSetting);

        } catch (Exception e) {
            result.set(RESULT, "Fail, cause : " + e.getClass());
        }
        result.set(RESULT, "Success, EDM already send to : " + mailAddress);
        return result;
    }

}
