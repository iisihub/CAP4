/*
 * RespMsgFactoryProcessor.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.response;

import org.springframework.beans.factory.InitializingBean;

import com.iisigroup.cap.base.service.ErrorCodeService;

/**
 * <pre>
 * RespMsgFactoryProcessor
 * </pre>
 * 
 * @since 2012/3/30
 * @author UFO
 * @version
 *          <ul>
 *          <li>2012/3/30,UFO,new
 *          </ul>
 */
public class RespMsgFactoryProcessor implements InitializingBean {

    private ErrorCodeService errCodeService;

    public void setErrorCodeService(ErrorCodeService errCodeService) {
        this.errCodeService = errCodeService;
    }

    public void afterPropertiesSet() throws Exception {
        RespMsgFactory.setErrorCodeService(errCodeService);
    }

}
