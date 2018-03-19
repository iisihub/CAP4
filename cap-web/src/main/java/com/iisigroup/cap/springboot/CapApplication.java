/* 
 * CapApplication.java
 * 
 * Copyright (c) 2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.springboot;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;
import org.tuckey.web.filters.urlrewrite.gzip.GzipFilter;

import com.iisigroup.cap.web.CapHandlerServlet;
import com.iisigroup.cap.web.LogContextFilter;
import com.opensymphony.module.sitemesh.filter.PageFilter;

/**
 * <pre>
 * TODO Write a short description on the purpose of the program
 * </pre>
 * 
 * @since 2018年3月16日
 * @author Lancelot
 * @version
 *          <ul>
 *          <li>2018年3月16日,Lancelot,new
 *          </ul>
 */
@SpringBootApplication
@ImportResource({ "classpath:spring/applicationContext.xml", "classpath:spring/security.xml" })
public class CapApplication {
    public static void main(String[] args) {
        SpringApplication.run(CapApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean dispatcherServletRegistrationBean() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new DispatcherServlet(), "/page/*");
        registrationBean.addInitParameter("contextConfigLocation", "classpath:spring/page.xml");
        return registrationBean;
    }

    @Bean
    public ServletRegistrationBean capHandlerServletRegistrationBean() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new CapHandlerServlet(), "/handler/*");
        registrationBean.addInitParameter("pluginManager", "CapPluginManager");
        registrationBean.addInitParameter("defaultRequest", "CapDefaultRequest");
        registrationBean.addInitParameter("errorResult", "CapDefaultErrorResult");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setForceEncoding(true);
        characterEncodingFilter.setEncoding("UTF-8");
        registration.setFilter(characterEncodingFilter);
        registration.setOrder(3);
        return registration;
    }

    @Bean
    public FilterRegistrationBean openEntityManagerInViewFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter openEntityManagerInViewFilter = new OpenEntityManagerInViewFilter();
        registration.setFilter(openEntityManagerInViewFilter);
        registration.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        registration.setOrder(6);
        return registration;
    }

    @Bean
    public FilterRegistrationBean logContextFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter logContextFilter = new LogContextFilter();
        registration.setFilter(logContextFilter);
        registration.setOrder(5);
        return registration;
    }

    @Bean
    public FilterRegistrationBean urlRewriteFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter urlRewriteFilter = new UrlRewriteFilter();
        registration.setFilter(urlRewriteFilter);
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setOrder(4);
        return registration;
    }

    @Bean
    public FilterRegistrationBean gzipFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        GzipFilter gzipFilter = new GzipFilter();
        registration.setFilter(gzipFilter);
        registration.addUrlPatterns("*.js", "*.css");
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setOrder(2);
        return registration;
    }

    @Bean
    public FilterRegistrationBean pageFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter pageFilter = new PageFilter();
        registration.setFilter(pageFilter);
        registration.addUrlPatterns("/page/*");
        registration.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        registration.setOrder(7);
        return registration;
    }

    @Bean
    public FilterRegistrationBean delegatingFilterProxy() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy();
        delegatingFilterProxy.setTargetBeanName(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME);
        registration.setFilter(delegatingFilterProxy);
        registration.setOrder(1);
        return registration;
    }
}
