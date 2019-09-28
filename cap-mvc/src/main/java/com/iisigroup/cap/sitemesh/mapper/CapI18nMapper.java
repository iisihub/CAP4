/* 
 * CapI18nMapper.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * i18n Decorator
 * </pre>
 * 
 * @since 2012/9/28
 * @author rodeschen
 * @version
 *          <ul>
 *          <li>2012/9/28,rodeschen,new
 *          <li>2013/1/23,RodesChen,fix weblogic getPath error
 *          </ul>
 */
public class CapI18nMapper {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    public final static String PROP_I18N = "i18n";
    public String ignorePathReg;
}
