/* 
 * CapRequestJSONMapper.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.sitemesh.mapper;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * JavaScript設定request JSON
 * </pre>
 * 
 * @since 2013/4/15
 * @author iristu
 * @version
 *          <ul>
 *          <li>2013/4/15,iristu,new
 *          </ul>
 */
public class CapRequestJSONMapper {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    public final static String PROP_KEY = "reqJSON";
    public String ignorePathReg;
    public Set<String> ignoreParams;
    public Set<String> decoratorFile;
}
