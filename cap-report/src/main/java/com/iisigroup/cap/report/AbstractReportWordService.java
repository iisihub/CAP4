/* 
 * AbstractReportWordService.java
 * 
 * Copyright (c) 2011 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.report;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.report.constants.ReportParamEnum;
import com.iisigroup.cap.utils.CapSystemConfig;

import freemarker.template.Template;

/**
 * <pre>
 * The base word report service.
 * </pre>
 * 
 * @since 2014年12月10日
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2014年12月10日,Sunkist Wang,new
 *          </ul>
 */
public abstract class AbstractReportWordService implements ReportService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final static String REPORT_SUFFIX = ".ftl";
    protected final static String DEFAULT_ENCORDING = "utf-8";
    @Resource
    private FreeMarkerConfigurer fmConfg;
    @Resource
    private CapSystemConfig sysConfig;

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.report.ReportService#generateReport(com.iisigroup.cap.component.Request)
     */
    @Override
    public ByteArrayOutputStream generateReport(Request request) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, getSysConfig().getProperty(ReportParamEnum.DEFAULT_ENCODING.toString(), DEFAULT_ENCORDING)));) {
            Template t = getFmConfg().getConfiguration().getTemplate(getReportDefinition() + REPORT_SUFFIX);
            Map<String, Object> reportData = execute(request);
            t.process(reportData, writer);
        } catch (Exception e) {
            if (e.getCause() != null) {
                throw new CapException(e.getCause(), e.getClass());
            } else {
                throw new CapException(e, e.getClass());
            }
        }
        return out;
    }

    public FreeMarkerConfigurer getFmConfg() {
        return fmConfg;
    }

    public CapSystemConfig getSysConfig() {
        return sysConfig;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.report.ReportService#isWriteToFile()
     */
    @Override
    public boolean isWriteToFile() {
        return false;
    }
}
