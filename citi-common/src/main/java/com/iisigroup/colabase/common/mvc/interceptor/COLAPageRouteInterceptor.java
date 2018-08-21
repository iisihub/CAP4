/* 
 * COLAPageRouteInterceptor.java
 * 
 * Copyright (c) 2009-2015 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.common.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * Apply form page routing
 * </pre>
 * 
 * @since 2015-12-23
 * @author Bo-Xuan Fan
 * @version
 *          <ul>
 *          <li>2015-12-23,Bo-Xuan Fan,new
 *          </ul>
 */
public class COLAPageRouteInterceptor extends HandlerInterceptorAdapter {

    private String include;

    /** default constructor */
    public COLAPageRouteInterceptor() {
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getPathInfo();
        return CapString.checkRegularMatch(path, this.include);
    }
}
