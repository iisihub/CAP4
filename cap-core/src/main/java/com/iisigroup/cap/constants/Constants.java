/*
 * CapConstants.java
 *
 * Copyright (c) 2009 International Integrated System, Inc.
 * 11F, No.133, Sec.4, Minsheng E. Rd., Taipei, 10574, Taiwan, R.O.C.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. ("Confidential Information").
 */
package com.iisigroup.cap.constants;

import java.math.BigDecimal;

/**
 * <p>
 * This interface provide common use constants..
 * </p>
 * 
 * @author iristu
 * @version
 *          <ul>
 *          <li>2010/6/30,iristu,modify
 *          <li>2011/11/1,rodeschen,from cap
 *          </ul>
 */
public interface Constants {

    /**
     * Line break.
     */
    String LINE_BREAK = System.getProperty("line.separator");

    /**
     * empty string.
     */
    String EMPTY_STRING = "";

    /**
     * a space string.
     */
    String SPACE = " ";

    /**
     * a "0" string.
     */
    String S0 = "0";

    /**
     * a "1" string.
     */
    String S1 = "1";

    /**
     * a "-1" string.
     */
    String S1N = "-1";
    /**
     * a "Y" string
     */
    public static final String FLAG_Y = "Y";
    /**
     * a "N" string
     */
    public static final String FLAG_N = "N";
    /**
     * a "-" string
     */
    String NEGATIVE = "-";
    /**
     * a BigDecimal, value is 0.
     */
    BigDecimal B0 = new BigDecimal(0d);

    /**
     * a BigDecimal, value is 1.
     */
    BigDecimal B1 = new BigDecimal(1d);

    /**
     * a BigDecimal, value is -1.
     */
    BigDecimal B1N = new BigDecimal(-1d);

    /**
     * a empty string array.
     */
    String[] EMPTY_ARRAY = new String[0];

    String VALUES_SEPARATOR = "|";

    String COMMA = ",";

    String DATE_STAMP = "'";

    /**
     * http
     */
    String HOST_URL = "HOST_URL";// HTTP.TARGET_HOST;

    String CONNECTION_TIMEOUT = "TIMEOUT";// HttpConnectionParams.CONNECTION_TIMEOUT;

    String ASYNC = "ASYNC";

    String HTTP_RETRY_COUNT = "HTTP_RETRY_COUNT";

    String HTTP_METHOD = "HTTP_METHOD";
    /**
     * Empty Json
     */
    String EMPTY_JSON = "{}";

    /** The Constant AJAX_HANDLER_TIMEOUT. */
    String AJAX_HANDLER_TIMEOUT = "AJAX_HANDLER_TIMEOUT";

    /** UI端顯示訊息 */
    String AJAX_NOTIFY_MESSAGE = "NOTIFY_MESSAGE";

    /**
     * security
     */
    String SECURITY_CONTEXT = "capSecurityContext";

    /**
     * system config
     */
    String SYSTEM_CONFIG = "systemConfig";

    String TXNCD = "transCde";

}
