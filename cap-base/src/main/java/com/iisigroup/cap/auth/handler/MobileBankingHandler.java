/* 
 * MobileBankingHandler.java
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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpGet;
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
 * 網銀帳戶餘額查詢
 * </pre>
 * 
 * @since 2018年2月22日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2018年2月22日,Sunkist,new
 *          </ul>
 */
@Controller("mobilebankinghandler")
public class MobileBankingHandler extends MFormHandler {

    @Resource
    private CapSystemProperties sysProp;

    public Result user(Request request) {
        ((HttpServletRequest) request.getServletRequest()).getSession().setAttribute("username", request.get("username"));
        return new AjaxFormResult();
    }

    public Result getUser(Request request) {
        return new AjaxFormResult().set("user", CapString.trimNull(((HttpServletRequest) request.getServletRequest()).getSession().getAttribute("username")));
    }

    public Result getAvailableBalance(Request request) {

        // String[] scopes = CapString.trimNull(request.get("scope"), "").split(" ");
        String username = request.get("username");
        String token = request.get("access_token");

        String url = "http://59.124.83.56:9003/v1/customer/availbalsumminq";

        try {
            HGService hgService = new CapHttpService();

            hgService.setProperty("SSL", "false");
            hgService.setProperty(Constants.HTTP_METHOD, HttpGet.METHOD_NAME);
            hgService.setProperty(Constants.HOST_URL, url);
            hgService.setProperty(Constants.CONNECTION_TIMEOUT, "3000");
            hgService.setProperty(Constants.ASYNC, "false");

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("CustID", "A123456789");
            params.put("AcctNo", "56789013-011");
            params.put("apikey", sysProp.get("client_id"));
            params.put("app_enduser", username);
            hgService.setSendData(params);

            // add request header
            Map<String, Object> header = new HashMap<String, Object>();
            header.put("Authorization", "Bearer " + token);
            hgService.setHeader(header);

            hgService.initConnection();
            hgService.execute();
            String receiveData = new String(hgService.getReceiveData(), "UTF-8");

            logger.debug("Response Code : " + ((CapHttpService) hgService).getHttpStatus());

            return new AjaxFormResult().set("result", receiveData);
        } catch (CapException e) {
            throw e;
        } catch (Exception e) {
            throw new CapException(e, e.getClass());
        }
    }
}
