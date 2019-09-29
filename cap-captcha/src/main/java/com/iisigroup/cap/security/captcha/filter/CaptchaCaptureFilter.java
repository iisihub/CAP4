/* 
 * CaptchaCaptureFilter.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.security.captcha.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.iisigroup.cap.security.CapSecurityContext;

public class CaptchaCaptureFilter extends OncePerRequestFilter {

    private String userCaptchaResponse;
    private HttpServletRequest request;

    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        logger.debug("Captcha capture filter");

        // Assign values only when user has submitted a Captcha value.
        // Without this condition the values will be reset due to redirection
        // and CaptchaVerifierFilter will enter an infinite loop

        synchronized (req) {
            if (req.getParameter("captcha") != null) {
                // CapSecurityContext.getUser().put("request", req);
                CapSecurityContext.getUser().getExtraAttrib().put("request", req);
                request = req;
            }
            logger.debug("userResponse: " + req.getParameter("captcha"));
        }

        logger.debug("userResponse: " + userCaptchaResponse);

        // Proceed with the remaining filters
        chain.doFilter(req, res);
    }

    public String getUserCaptchaResponse() {
        return userCaptchaResponse;
    }

    public void setUserCaptchaResponse(String userCaptchaResponse) {
        this.userCaptchaResponse = userCaptchaResponse;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}