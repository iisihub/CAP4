/*
 * RespMsgFactory.java
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

import com.iisigroup.cap.base.service.ErrorCodeService;

/**
 * <pre>
 * RespMsgFactory
 * </pre>
 * 
 * @since 2012/3/30
 * @author UFO
 * @version
 *          <ul>
 *          <li>2012/3/30,UFO,new
 *          </ul>
 */
public class RespMsgFactory {
    private static ErrorCodeService errCodeService;

    private RespMsgFactory() {

    }

    /**
     * @param errCodeService
     *            the errorCodeService to set
     */
    public static void setErrorCodeService(ErrorCodeService errCodeService) {
        RespMsgFactory.errCodeService = errCodeService;
    }

    public static ErrorCodeService getInstance() {
        if (errCodeService == null) {
            throw new IllegalStateException();
        }
        return errCodeService;
    }
}
