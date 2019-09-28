/* 
 * Plugin.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.plugin;

import org.springframework.beans.factory.InitializingBean;

/**
 * <p>
 * interface ICapPlugin extends InitializingBean.
 * </p>
 *
 * @author Tony Wang
 * @version
 *          <ul>
 *          <li>2010/7/16,iristu,modify
 *          <li>2011/1/28,iristu,增加可設定default回傳的IResult
 *          <li>2011/11/1,rodeschen,from cap
 *          </ul>
 */
public interface Plugin extends InitializingBean {

    /** The Constant PLUGIN_NOT_FOUND_MSG. */
    public static final String PLUGIN_NOT_FOUND_MSG = "cap.core.pluginNotFound";

    /**
     * Gets the plugin name.
     * 
     * @return the plugin name
     */
    public String getPluginName();

}
