/*
 * HandlerPlugin.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iisigroup.cap.action.Action;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.handler.Handler;

/**
 * <pre>
 * HandlerPlugin
 * </pre>
 * 
 * @since 2011/11/22
 * @author rodeschen
 * @version
 *          <ul>
 *          <li>2011/11/22,rodeschen,new
 *          <li>2011/11/1,rodeschen,from cap
 *          </ul>
 */
public abstract class HandlerPlugin implements Plugin, Handler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Request request;

    /**
     * execute
     * 
     * @param params
     *            Client 參數
     * @return String
     */
    public abstract Result execute(Request params);

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public String getPluginName() {
        return this.getClass().getSimpleName();
    }

    /**
     * get action
     * 
     * @param formAction
     *            action name
     * @return IAction
     */
    public abstract Action getAction(String formAction);

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        // do nothing
    }

}
