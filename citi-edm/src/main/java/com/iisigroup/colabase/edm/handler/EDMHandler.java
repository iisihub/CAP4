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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.colabase.edm.service.EDMService;

/**<pre>
 * TODO Write a short description on the purpose of the program
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
        String eamilAccount = null;
        String edmFtlPath = null;
        String edmCustomerName = null;
        String edmProject = null;
        try {
              eamilAccount = request.get("eamilAccount");
              edmFtlPath = request.get("edmFtlPath");
              edmCustomerName = request.get("edmCustomerName");
              edmProject = request.get("edmProject");
              
              // 給EDM值
              Map<String, Object> dataMap = new HashMap<>();
              dataMap.put("eamilAccount", eamilAccount);
              dataMap.put("idNo", edmCustomerName);
              dataMap.put("edmProject", edmProject);
              
              //report/edm1.ftl
              edmService.sendEDM(request, edmFtlPath, dataMap);
            
        } catch (Exception e) {
            result.set("result", "Fail, cause : " + e.getClass());
            e.printStackTrace();
        }
        result.set("result", "Success, EDM already send to : " + eamilAccount);
        return result;
    }

    public Result genFtlDemo(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        try {
            File destination = new File(request.get("unzipOutPath"));
            File unzipFiles = new File(request.get("unzipFile"));
            String password = request.get("unzipPassword");

            result.set("result", "Success, unzip path : " + destination);
        } catch (Exception e) {
            result.set("result", "Fail, cause : " + e.getClass());
            e.printStackTrace();
        }
        return result;
    }
    
}