/*
 * CodeTypeFormatter.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.formatter.impl;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

import com.iisigroup.cap.base.service.CodeTypeService;
import com.iisigroup.cap.constants.Constants;
import com.iisigroup.cap.constants.KeyValueFormatTypeEnum;
import com.iisigroup.cap.formatter.Formatter;
import com.iisigroup.cap.operation.simple.SimpleContextHolder;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.cap.utils.CapWebUtil;

/**
 * <pre>
 * 取得codeType
 * </pre>
 * 
 * @since 2013/2/26
 * @author iristu
 * @version
 *          <ul>
 *          <li>2013/2/26,iristu,new
 *          </ul>
 */
public class CodeTypeFormatter implements Formatter {

    private static final long serialVersionUID = 3499609752233060894L;
    private KeyValueFormatTypeEnum show;
    private Map<String, String> codeMap;

    public CodeTypeFormatter(CodeTypeService service, String codeType, Locale locale) {
        this.codeMap = service.findByCodeType(codeType, locale.toString());
        this.show = KeyValueFormatTypeEnum.VALUE;
    }

    public CodeTypeFormatter(CodeTypeService service, String codeType, Locale locale, KeyValueFormatTypeEnum show) {
        this.codeMap = service.findByCodeType(codeType, locale.toString());
        this.show = show;
    }

    public CodeTypeFormatter(CodeTypeService service, String codeType) {
        this.codeMap = service.findByCodeType(codeType, SimpleContextHolder.get(CapWebUtil.LOCALE_KEY).toString());
        this.show = KeyValueFormatTypeEnum.VALUE;
    }

    public CodeTypeFormatter(CodeTypeService service, String codeType, KeyValueFormatTypeEnum show) {
        this.codeMap = service.findByCodeType(codeType, SimpleContextHolder.get(CapWebUtil.LOCALE_KEY).toString());
        this.show = show;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.formatter.IFormatter#reformat(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public String reformat(Object in) {
        if (in instanceof BigDecimal) {
            in = ((BigDecimal) in).toPlainString();
        }
        String k = (String) in;
        if (codeMap != null && !CapString.isEmpty(k)) {
            String value = "";
            if (codeMap.containsKey(k)) {
                value = codeMap.get(k);
            }
            switch (show) {
            case KEY_VALUE:
                return new StringBuilder(k).append("-").append(value).toString();
            case KEY_SPACE_VALUE:
                return new StringBuilder(k).append(" ").append(value).toString();
            default:
                return value;
            }
        }
        return Constants.EMPTY_STRING;
    }

}
