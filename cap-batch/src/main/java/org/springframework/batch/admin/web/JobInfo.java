/* 
 * JobInfo.java
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
public class JobInfo {
    private final String name;

    private final int executionCount;

    private boolean launchable = false;

    private boolean incrementable = false;

    private final Long jobInstanceId;

    public JobInfo(String name, int executionCount) {
        this(name, executionCount, false);
    }

    public JobInfo(String name, int executionCount, boolean launchable) {
        this(name, executionCount, null, launchable, false);
    }

    public JobInfo(String name, int executionCount, boolean launchable, boolean incrementable) {
        this(name, executionCount, null, launchable, incrementable);
    }

    public JobInfo(String name, int executionCount, Long jobInstanceId, boolean launchable, boolean incrementable) {
        super();
        this.name = name;
        this.executionCount = executionCount;
        this.jobInstanceId = jobInstanceId;
        this.launchable = launchable;
        this.incrementable = incrementable;
    }

    public String getName() {
        return name;
    }

    public int getExecutionCount() {
        return executionCount;
    }

    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    public boolean isLaunchable() {
        return launchable;
    }

    public boolean isIncrementable() {
        return incrementable;
    }

    @Override
    public String toString() {
        return name;
    }
}
