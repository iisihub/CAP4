/* 
 * CapSecurityConfig.java
 * 
 * Copyright (c) 2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.event.LoggerListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.iisigroup.cap.auth.handler.AjaxAuthenticationFailureHandler;
import com.iisigroup.cap.auth.provider.CapAuthenticationProvider;
import com.iisigroup.cap.security.web.CapAuthenticationEntryPoint;

/**
 * <pre>
 * Spring security Configuration
 * </pre>
 * 
 * @since 2018年3月27日
 * @author shawnyhw6n9
 * @version
 *          <ul>
 *          <li>2018年3月27日,shawnyhw6n9,new
 *          </ul>
 */
@Configuration
// @ImportResource("classpath:spring/security.xml")
public class CapSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CapAuthenticationProvider authenticationProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // FIXME
        // http.authorizeRequests().anyRequest().fullyAuthenticated().and().formLogin().defaultSuccessUrl("/page/index").failureHandler(ajaxAuthenticationFailureHandler).failureUrl("/page/login?error")
        // .loginPage("/page/login").permitAll().and().logout().permitAll();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider).userDetailsService(userDetailsService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // FIXME
        web.ignoring().antMatchers("/img/**", "/**/images/**", "/jquery/**", "/**/**.css", "/**/**.js", "/i18njs", "/captcha.png*", "/app/error/message*", "/page/login");
    }

    /**
     * Session Registry to store sessions
     * 
     * @return
     */
    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * Session event publisher.
     * 
     * @return
     */
    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // TODO Auto-generated method stub
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() throws Exception {
        CapAuthenticationEntryPoint entryPoint = new CapAuthenticationEntryPoint("/page/login");
        entryPoint.setForceHttps(false);
        return entryPoint;
    }

    @Bean
    public LoggerListener loggerListener() {
        return new LoggerListener();
    }

}
