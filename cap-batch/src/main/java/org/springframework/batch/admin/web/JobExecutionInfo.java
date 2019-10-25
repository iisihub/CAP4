/* 
 * JobExecutionInfo.java
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
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
public class JobExecutionInfo {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    private SimpleDateFormat durationFormat = new SimpleDateFormat("HH:mm:ss");

    private Long id;

    private int stepExecutionCount;

    private Long jobId;

    private String jobName;

    private String startDate = "";

    private String startTime = "";

    private String duration = "";

    private JobExecution jobExecution;

    private String jobParameters;

    private boolean restartable = false;

    private boolean abandonable = false;

    private boolean stoppable = false;

    private JobParametersConverter converter = new DefaultJobParametersConverter();

    private final TimeZone timeZone;

    public JobExecutionInfo(JobExecution jobExecution, TimeZone timeZone) {

        this.jobExecution = jobExecution;
        this.timeZone = timeZone;
        this.id = jobExecution.getId();
        this.jobId = jobExecution.getJobId();
        this.stepExecutionCount = jobExecution.getStepExecutions().size();

        JobInstance jobInstance = jobExecution.getJobInstance();
        if (jobInstance != null) {
            this.jobName = jobInstance.getJobName();
            Properties properties = converter.getProperties(jobExecution.getJobParameters());
            this.jobParameters = PropertiesConverter.propertiesToString(properties);
            BatchStatus status = jobExecution.getStatus();
            this.restartable = status.isGreaterThan(BatchStatus.STOPPING) && status.isLessThan(BatchStatus.ABANDONED);
            this.abandonable = status.isGreaterThan(BatchStatus.STARTED) && status != BatchStatus.ABANDONED;
            this.stoppable = status.isLessThan(BatchStatus.STOPPING);
        } else {
            this.jobName = "?";
            this.jobParameters = null;
        }

        // Duration is always in GMT
        durationFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        // The others can be localized
        timeFormat.setTimeZone(timeZone);
        dateFormat.setTimeZone(timeZone);
        if (jobExecution.getStartTime() != null) {
            this.startDate = dateFormat.format(jobExecution.getStartTime());
            this.startTime = timeFormat.format(jobExecution.getStartTime());
            Date endTime = jobExecution.getEndTime() != null ? jobExecution.getEndTime() : new Date();
            this.duration = durationFormat.format(new Date(endTime.getTime() - jobExecution.getStartTime().getTime()));
        }

    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public String getName() {
        return jobName;
    }

    public Long getId() {
        return id;
    }

    public int getStepExecutionCount() {
        return stepExecutionCount;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getDuration() {
        return duration;
    }

    public JobExecution getJobExecution() {
        return jobExecution;
    }

    public boolean isRestartable() {
        return restartable;
    }

    public boolean isAbandonable() {
        return abandonable;
    }

    public boolean isStoppable() {
        return stoppable;
    }

    public String getJobParameters() {
        return jobParameters;
    }
}
