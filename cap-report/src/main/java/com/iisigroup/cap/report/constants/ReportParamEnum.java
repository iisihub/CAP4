/* 
 * ReportParamEnum.java
 * 
 * Copyright (c) 2011 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.report.constants;

/**
 * <pre>
 * param enum
 * </pre>
 * 
 * @since 2014/4/1
 * @author tammy
 * @version
 *          <ul>
 *          <li>2014/4/1,tammy,new
 *          </ul>
 */
public enum ReportParamEnum {
    templateName("templateName"),
    defaultEncoding("defaultEncoding"),
    encrypt("encryptPassword"),
    defaultFont("report.defaultFont");

    private String code;

    ReportParamEnum(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
