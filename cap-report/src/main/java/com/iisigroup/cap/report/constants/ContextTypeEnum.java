/* 
 * ContextTypeEnum.java
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
 * content type enum
 * </pre>
 * 
 * @since 2014/4/1
 * @author tammy
 * @version
 *          <ul>
 *          <li>2014/4/1,tammy,new
 *          </ul>
 */
public enum ContextTypeEnum {
    TEXT("text/html"),
    PDF("application/pdf"),
    DOC("application/msword"),
    UNKNOW("application/octet-stream"),
    XLS("application/vnd.ms-excel");

    private String code;

    ContextTypeEnum(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
