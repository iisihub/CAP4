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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
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

    public Result getAvailableBalance(Request request) {

        String[] scopes = CapString.trimNull(request.get("scope"), "").split(" ");
        String username = request.get("username");
        String token = request.get("access_token");

        String url = "http://59.124.83.56:9003/v1/customer/availbalsumminq?CustID=A123456789&AcctNo=56789013-011&apikey=PufzaI4gHJNccdAEcG8EyAj1AfYrWZqf";

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(url);

            // add request header
            httpGet.addHeader("Authorization", "Bearer " + token);
            HttpResponse response = client.execute(httpGet);

            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            return new AjaxFormResult().set("result", sb.toString());
        } catch (CapException e) {
            throw e;
        } catch (Exception e) {
            throw new CapException(e, e.getClass());
        }
    }
}
