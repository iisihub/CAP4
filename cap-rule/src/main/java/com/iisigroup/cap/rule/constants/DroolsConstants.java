/* 
 * DroolsConstants.java
 * 
 * Copyright (c) 2011 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.rule.constants;

/**
 * <p>
 * This Drools provide common use constants..
 * </p>
 * 
 * @author TimChiang
 * @version
 *          <ul>
 *          <li>2013/12/27,TimChiang,new
 *          </ul>
 */
public interface DroolsConstants {

    /**
     * 
     */
    String PACKAGE_NAME = "com.iisigroup.cap.service";

    String IMPORT_CLASS = "com.iisigroup.cap.rule.model.CaseInfo,com.iisigroup.cap.utils.CapDroolsUtil";

    String VARIABLES_NAME = "com.iisigroup.cap.utils.CapDroolsUtil comUtil";

    String CONDITION_COL = "CONDITION";

    String ACTION_COL = "ACTION";

    String ACTIVATION_GROUP = "ACTIVATION-GROUP";

    String NO_LOOP = "NO-LOOP";

    String PRIORITY = "PRIORITY";
}
