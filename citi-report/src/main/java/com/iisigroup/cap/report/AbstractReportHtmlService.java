/* 
 * AbstractReportHtmlService.java
 * 
 * Copyright (c) 2009-2015 International Integrated System, Inc. 
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
import java.io.Writer;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
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
 * Base Page of Report.
 * from freemarker to html
 * </pre>
 * 
 * @since 2015年5月21日
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2015年5月21日,Sunkist Wang,new
 *          </ul>
 */
public abstract class AbstractReportHtmlService implements ReportService {

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
    public ByteArrayOutputStream generateReport(Request request) throws CapException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = null;
        OutputStreamWriter wr = null;
        try {
            Template t = getFmConfg().getConfiguration().getTemplate(getReportDefinition() + REPORT_SUFFIX);
            Map<String, Object> reportData = execute(request);
            wr = new OutputStreamWriter(out, getSysConfig().getProperty(ReportParamEnum.defaultEncoding.toString(), DEFAULT_ENCORDING));
            writer = new BufferedWriter(wr);
            t.process(reportData, writer);
        } catch (Exception e) {
            if (e.getCause() != null) {
                throw new CapException(e.getCause(), e.getClass());
            } else {
                throw new CapException(e, e.getClass());
            }
        } finally {
            IOUtils.closeQuietly(wr);
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(out);
        }
        return out;
    }

    public FreeMarkerConfigurer getFmConfg() {
        return fmConfg;
    }

    public CapSystemConfig getSysConfig() {
        return sysConfig;
    }

    @Override
    public boolean isWriteToFile() {
        return false;
    }

}
