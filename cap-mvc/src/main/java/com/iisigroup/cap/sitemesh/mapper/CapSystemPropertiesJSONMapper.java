/* 
 * CapSystemPropertiesJSONMapper.java
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

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 實作 Sitemesh 的 JSONMapper，把系統參數帶到 page。
 * page 上對應的 properties 為 prop
 * </pre>
 * 
 * <code><meta name="decorator" content="control">
 * <decorator:getProperty property="prop" default="" /></code>
 * 
 * @since 2014/1/19
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2014/1/19,Sunkist Wang,new
 *          </ul>
 */
public class CapSystemPropertiesJSONMapper {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    public final static String PROP_KEY = "prop";
    public Set<String> searchKeys;
    public Set<String> decoratorFile;
    public Map<String, Object> sysProp;
}
