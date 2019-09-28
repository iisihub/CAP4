/* 
 * CapDateFormatter.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.formatter.impl;

import com.iisigroup.cap.constants.Constants;
import com.iisigroup.cap.formatter.Formatter;
import com.iisigroup.cap.utils.CapDate;

/**
 * <pre>
 * 使用CapDate執行format
 * </pre>
 * 
 * @since 2013/4/16
 * @author iristu
 * @version
 *          <ul>
 *          <li>2013/4/16,iristu,new
 *          </ul>
 */

public class CapDateFormatter implements Formatter {

    private static final long serialVersionUID = 1L;

    String fromDateFormat;
    String toDateFormat;

    public CapDateFormatter(String fromDateFormat, String toDateFormat) {
        this.fromDateFormat = fromDateFormat;
        this.toDateFormat = toDateFormat;
    }

    @Override
    public String reformat(Object in) {
        if (in != null) {
            String str = (String) in;
            return CapDate.formatDateFromF1ToF2(str, fromDateFormat, toDateFormat);
        }
        return Constants.EMPTY_STRING;
    }

}
