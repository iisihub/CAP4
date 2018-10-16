/*
 * CapSessioniExpireException.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.exception;

import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * Session 到期Exception
 * </pre>
 * 
 * @since 2012/2/28
 * @author rodeschen
 * @version
 *          <ul>
 *          <li>2012/2/28,rodeschen,new
 *          </ul>
 */
@SuppressWarnings({ "serial" })
public class CapSessioniExpireException extends CapException {

    private String i18nKey;

    /**
     * Instantiates a new cap exception.
     */
    public CapSessioniExpireException() {
        super();
    }

    /**
     * Instantiates a new cap exception.
     * 
     * @param causeClass
     *            the cause class
     */
    @SuppressWarnings("rawtypes")
    public CapSessioniExpireException(Class causeClass) {
        super();
        super.setCauseSource(causeClass);
    }

    /**
     * Instantiates a new cap exception.
     * 
     * @param message
     *            the message
     * @param causeClass
     *            the cause class
     */
    @SuppressWarnings("rawtypes")
    public CapSessioniExpireException(String message, Class causeClass) {
        super(message, causeClass);
    }

    /**
     * Instantiates a new cap exception.
     * 
     * @param cause
     *            the throwable
     * @param causeClass
     *            the cause class
     */
    @SuppressWarnings("rawtypes")
    public CapSessioniExpireException(Throwable cause, Class causeClass) {
        super(cause, causeClass);
    }

    /**
     * Instantiates a new cap exception.
     * 
     * @param message
     *            the message
     * @param cause
     *            the cause
     * @param causeClass
     *            the cause class
     */
    @SuppressWarnings("rawtypes")
    public CapSessioniExpireException(String message, Throwable cause, Class causeClass) {
        super(message, cause, causeClass);
    }

    @Override
    public String getMessage() {
        return CapString.isEmpty(i18nKey) ? super.getMessage() : i18nKey;
    }

    /**
     * set i18n key
     * 
     * @param i18nKey
     *            the i18n key
     * @return CapMessageException
     */
    public CapSessioniExpireException setMessageKey(String i18nKey) {
        this.i18nKey = i18nKey;
        return this;
    }

    /**
     * get i18n key
     * 
     * @return String
     */
    public String getMessageKey() {
        return i18nKey;
    }

}
