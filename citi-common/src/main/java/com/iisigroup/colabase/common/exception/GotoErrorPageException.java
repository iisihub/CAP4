/* 
 * GotoErrorPageException.java
 * 
 * Copyright (c) 2009-2015 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.common.exception;

import com.iisigroup.cap.exception.CapException;

/**
 * <pre>
 * Goto error page (/page/errro)
 * </pre>
 * 
 * @since 2015年5月18日
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2015年5月18日,Sunkist Wang,new
 *          </ul>
 */
public class GotoErrorPageException extends CapException {

    /** serialVersionUID */
    private static final long serialVersionUID = -14286631173856616L;

    @SuppressWarnings("rawtypes")
    public GotoErrorPageException(String message, Class causeClass) {
        super(message, causeClass);
    }

}
