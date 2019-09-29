/* 
 * HGService.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.hg.service;

import java.util.Map;

import com.iisigroup.cap.hg.constants.ConnStatus;

/**
 * <pre>
 * host gateway service interface
 * </pre>
 * 
 * @since 2011/12/5
 * @author rodeschen
 * @version
 *          <ul>
 *          <li>2011/12/5,rodeschen,new
 *          <li>2013/1/15,rodeschen,add setHeader,remove CapException
 *          </ul>
 */
public interface HGService {

    /**
     * set properties
     * 
     * @param property
     *            property
     * 
     */
    void setProperties(Map<String, Object> property);

    /**
     * set property
     * 
     * @param name
     *            name
     * @param value
     *            value
     */
    public void setProperty(String name, Object value);

    /**
     * get property
     * 
     * @param <T>
     *            <T>
     * @param name
     *            name
     * 
     * @return <T>
     */
    Object getProperty(String name);

    /**
     * initial connection
     * 
     */
    void initConnection();

    /**
     * get hg status
     * 
     * @param <T>
     *            stauts
     * @return <T>
     */
    ConnStatus getStatus();

    /**
     * set hg status
     * 
     * @param status
     *            stauts
     */
    void setStatus(ConnStatus status);

    /**
     * set send data
     * 
     * @param data
     *            data
     */
    void setSendData(Object data);

    /**
     * 
     * @param data
     */
    void setHeader(Object data);

    /**
     * execute connect
     * 
     * @throws Exception
     */
    void execute() throws Exception;

    /**
     * get receive data
     * 
     * @param <T>
     *            stauts
     * @return <T>
     */
    <T> T getReceiveData();

    /**
     * error handle
     * 
     * @param <T>
     *            T
     * @param e
     *            exception
     * @return T
     */
    String errorHandle(Exception e);

}
