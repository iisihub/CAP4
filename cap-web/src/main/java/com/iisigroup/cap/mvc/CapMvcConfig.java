/* 
 * CapMvcConfig.java
 * 
 * Copyright (c) 2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.mvc;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.iisigroup.cap.message.CapReloadableResourceBundleMessageSource;
import com.iisigroup.cap.mvc.Interceptor.CapUserSecurityInterceptor;
import com.iisigroup.cap.mvc.i18n.LocaleChangeInterceptor;
import com.iisigroup.cap.mvc.i18n.SessionLocaleResolver;

/**
 * <pre>
 * Web Mvc Configurer 原 page.xml
 * </pre>
 * 
 * @since 2018年3月22日
 * @author shawnyhw6n9
 * @version
 *          <ul>
 *          <li>2018年3月22日,shawnyhw6n9,new
 *          </ul>
 */
@Configuration
public class CapMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    CapUserSecurityInterceptor capUserSecurityInterceptor;

    @Autowired
    LocaleChangeInterceptor localeChangeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(capUserSecurityInterceptor);
        registry.addInterceptor(localeChangeInterceptor);
    }

    @Bean
    public MessageSource messageSource() {
        CapReloadableResourceBundleMessageSource messageSource = new CapReloadableResourceBundleMessageSource();
        messageSource.setLanguages(new String[] { "_zh_CN", "_en", "_zh_TW" });
        messageSource.setBasePath("classpath:/i18n");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.TAIWAN);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.TAIWAN);
        return sessionLocaleResolver;
    }

}
