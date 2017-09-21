/*
 * CapAuthenticationEntryPoint.java
 *
 * Copyright (c) 2009-2011 International Integrated System, Inc.
 * 11F, No.133, Sec.4, Minsheng E. Rd., Taipei, 10574, Taiwan, R.O.C.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System,Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. ("Confidential Information").
 */
package com.iisigroup.cap.security.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * <p>
 * 當sessin過期時的動作， 若Ajax Request時需記錄為AjaxRequest，導致loginFormUrl以便判別 若為一般頁面之Request時，需導到loginFormUrl
 * </p>
 *
 * @author iristu
 * @version
 *          <ul>
 *          <li>2010/11/2,iristu,new
 *          </ul>
 */
public class CapAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(CapAuthenticationEntryPoint.class);
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public CapAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String redirectUrl = null;

        if (isUseForward()) {

            if (isForceHttps() && "http".equals(request.getScheme())) {
                // First redirect the current request to HTTPS.
                // When that request is received, the forward to the login page will be
                // used.
                redirectUrl = buildHttpsRedirectUrlForRequest(request);
            }

            if (redirectUrl == null) {
                String loginForm = determineUrlToUseForThisRequest(request, response, authException);

                if (logger.isDebugEnabled()) {
                    logger.debug("Server side forward to: " + loginForm);
                }

                RequestDispatcher dispatcher = request.getRequestDispatcher(loginForm);

                // ajax redirect 無作用，set customize response code
                if (loginForm.indexOf("ajax=1") > 0) {
                    response.setStatus(999);
                }

                dispatcher.forward(request, response);

                return;
            }
        } else {
            // redirect to login page. Use https if forceHttps true

            redirectUrl = buildRedirectUrlToLoginPage(request, response, authException);

        }

        if (redirectUrl.indexOf("ajax=1") > 0) {
            RequestDispatcher dispatcher = request.getRequestDispatcher(determineUrlToUseForThisRequest(request, response, authException));
            response.setStatus(999);
            dispatcher.forward(request, response);
        } else {
            redirectStrategy.sendRedirect(request, response, redirectUrl);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint#determineUrlToUseForThisRequest(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
     */
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String xReq = httpRequest.getHeader("x-requested-with");
        if ("XMLHttpRequest".equalsIgnoreCase(xReq)) {
            return new StringBuffer(getLoginFormUrl()).append("?ajax=1").toString();
        } else {
            return getLoginFormUrl();
        }
    }

}
