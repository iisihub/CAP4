/* 
 * CapBatchConstants.java
 * 
 * Copyright (c) 2011 International Integrated System, Inc. 

 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.batch.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * CapBatchConstants
 * </pre>
 * 
 * @since 2012/11/1
 * @author iristu
 * @version
 *          <ul>
 *          <li>2012/11/1,iristu,new
 *          </ul>
 */
public interface CapBatchConstants {

    String JOB_NAME = "jobName";

    String LOCALHOST = "localhost";

    String EXECUTOR = "executor";

    String K_JOB_EXECUTION = "_jobExecution";

    List<String> SCHEDULE_KEYWORDS = Collections
            .unmodifiableList(Arrays.asList(new String[] { "isEnabled", "exeHost", "cronExpression", "timeZoneId", "repeatCount", "repeatInterval", "priority", "jobData" }));

}
