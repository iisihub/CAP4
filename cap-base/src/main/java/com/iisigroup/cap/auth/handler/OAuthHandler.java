/* 
 * OAuthHandler.java
 * 
 * Copyright (c) 2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.auth.handler;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpPost;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.base.CapSystemProperties;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.constants.Constants;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.hg.service.HGService;
import com.iisigroup.cap.hg.service.impl.CapHttpService;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * 取得 access token
 * </pre>
 * 
 * @since 2018年2月22日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2018年2月22日,Sunkist,new
 *          </ul>
 */
@Controller("oauthhandler")
public class OAuthHandler extends MFormHandler {

    @Resource
    private CapSystemProperties sysProp;

    public Result token(Request request) {
        String code = request.get("code");
        String username = CapString.trimNull(((HttpServletRequest) request.getServletRequest()).getSession().getAttribute("username"));
        // String[] scopes = CapString.trimNull(request.get("scope"), "").split(" ");

        HGService hgService = new CapHttpService();

        hgService.setProperty("SSL", "false");
        hgService.setProperty(Constants.HTTP_METHOD, HttpPost.METHOD_NAME);
        hgService.setProperty(Constants.HOST_URL, "http://59.124.83.56:9003/no-target/oauth/token");
        hgService.setProperty(Constants.CONNECTION_TIMEOUT, "3000");
        hgService.setProperty(Constants.ASYNC, "false");

        Map<String, Object> header = new HashMap<String, Object>();
        header.put("Authorization", "Basic " + Base64.getEncoder().encodeToString((sysProp.get("client_id") + ":" + sysProp.get("client_secret")).getBytes()));
        header.put("Content-Type", "application/x-www-form-urlencoded");
        hgService.setHeader(header);

        hgService.setSendData("grant_type=authorization_code&code=" + code + "&app_enduser=" + username);
        try {
            hgService.initConnection();
            hgService.execute();
            String receiveData = new String(hgService.getReceiveData(), "UTF-8");
            return new AjaxFormResult(receiveData);
        } catch (CapException e) {
            throw e;
        } catch (Exception e) {
            throw new CapException(e, e.getClass());
        }

    }

}
