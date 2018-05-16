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
import com.iisigroup.colabase.edm.service.EDMService;

/**<pre>
 * Demo EDM功能 handler
 * </pre>
 * @since  2018年4月30日
 * @author Johnson Ho
 * @version <ul>
 *           <li>2018年4月30日,Johnson Ho,new
 *          </ul>
 */

@Controller("demoedmhandler")
public class EDMHandler extends MFormHandler {
    
    @Autowired
    private EDMService edmService;
    
    public Result sendEdmDemo(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String mailAddress = null;
        String edmFtlPath = null;
        String edmCustomerName = null;
        String edmProject = null;
        try {
              mailAddress = request.get("mailAddress");
              edmFtlPath = request.get("edmFtlPath");
              edmCustomerName = request.get("edmCustomerName");
              edmProject = request.get("edmProject");
              
              // 給EDM值
              Map<String, Object> dataMap = new HashMap<>();
              dataMap.put("mailAddress", mailAddress);
              dataMap.put("edmCustomerName", edmCustomerName);
              dataMap.put("edmProject", edmProject);
              
              //report/edm1.ftl
              edmService.sendEDM(request, "report/" + edmFtlPath, dataMap);
            
        } catch (Exception e) {
            result.set("result", "Fail, cause : " + e.getClass());
            e.printStackTrace();
        }
        result.set("result", "Success, EDM already send to : " + mailAddress);
        return result;
    }

    public Result ftlDemo(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        try {
            String sourceFileName = request.get("sourceFileName");
            String ftlDestination = request.get("ftlDestination");
            
            edmService.htmlToFtl(request, sourceFileName, ftlDestination);
            result.set("result", "Success, ftl path : " + ftlDestination);
            
        } catch (Exception e) {
            result.set("result", "Fail, cause : " + e.getClass());
            e.printStackTrace();
        }
        return result;
    }
    
}
