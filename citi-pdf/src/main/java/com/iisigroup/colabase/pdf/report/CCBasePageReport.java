/* 
 * CCBasePageReport.java
 * 
 * Copyright (c) 2009-2013 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.pdf.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.report.factory.ItextFontFactory;
import com.iisigroup.cap.utils.CapSystemConfig;

/**
 * <pre>
 * Base Page of Report.
 * </pre>
 * 
 * @since 2013/10/24
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2013/10/24,Sunkist Wang,new
 *          </ul>
 */
public abstract class CCBasePageReport extends MFormHandler implements IFreeMarkerReport {

    public static final String fileUrlPrefix = "file:///";

    public enum PageReportParam {
        templateName("templateName"),
        defaultEncoding("defaultEncoding");

        private String code;

        PageReportParam(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }

    @Autowired
    private FreeMarkerConfigurer fmConfg;
    @Autowired
    private CapSystemConfig sysConfig;
    @Autowired
    private ItextFontFactory fontFactory;

    public FreeMarkerConfigurer getFmConfg() {
        return fmConfg;
    }

    public CapSystemConfig getSysConfig() {
        return sysConfig;
    }

    public ItextFontFactory getFontFactory() {
        return fontFactory;
    }
}
