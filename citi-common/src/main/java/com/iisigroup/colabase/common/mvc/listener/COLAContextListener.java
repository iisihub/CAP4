/* 
 * COLAContextListener.java
 * 
 * Copyright (c) 2009-2016 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.common.mvc.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.iisigroup.colabase.common.jsp.COLASystemProperties;

/**
 * <pre>
 * TODO Write a short description on the purpose of the program
 * </pre>
 * 
 * @since 2016-01-29
 * @author Bo-Xuan Fan
 * @version
 *          <ul>
 *          <li>2016-01-29,Bo-Xuan Fan,new
 *          </ul>
 */
public class COLAContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        sce.getServletContext().setAttribute("systemProperties", ctx.getBean(COLASystemProperties.class));
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
