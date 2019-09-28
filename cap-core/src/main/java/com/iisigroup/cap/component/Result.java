/* 
 * Result.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.component;

import java.io.Serializable;

import javax.servlet.ServletResponse;

/**
 * <p>
 * IResult.
 * </p>
 * 
 * @author iristu
 * @version
 *          <ul>
 *          <li>2010/11/23,iristu,new
 *          <li>2011/11/1,rodeschen,from cap
 *          </ul>
 */
public interface Result extends Serializable {

    String getResult();

    void respondResult(ServletResponse response);

    String getLogMessage();

    void add(Result result);

    String getContextType();

    void setContextType(String cxtType);

    String getEncoding();

    void setEncoding(String encoding);

}
