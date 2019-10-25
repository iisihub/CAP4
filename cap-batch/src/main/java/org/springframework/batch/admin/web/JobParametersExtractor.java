/* 
 * JobParametersExtractor.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package org.springframework.batch.admin.web;

import java.util.Properties;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.support.PropertiesConverter;

/**
 * <pre>
 * TODO Write a short description on the purpose of the program
 * </pre>
 * 
 * @since 2019年10月25日
 * @author Lancelot
 * @version
 *          <ul>
 *          <li>2019年10月25日,Lancelot,new
 *          </ul>
 */
public class JobParametersExtractor {
    private JobParametersConverter converter = new DefaultJobParametersConverter();

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * @param oldParameters
     *            the latest job parameters
     * @return a String representation for rendering the job parameters from the last instance
     */
    public String fromJobParameters(JobParameters oldParameters) {

        String properties = PropertiesConverter.propertiesToString(converter.getProperties(oldParameters));
        if (properties.startsWith("#")) {
            properties = properties.substring(properties.indexOf(LINE_SEPARATOR) + LINE_SEPARATOR.length());
        }
        properties = properties.replace("\\:", ":");
        return properties;

    }

    public JobParameters fromString(String params) {
        Properties properties = PropertiesConverter.stringToProperties(params);
        return converter.getJobParameters(properties);
    }
}
