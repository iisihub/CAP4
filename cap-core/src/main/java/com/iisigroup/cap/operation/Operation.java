/*
 * Operation.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.operation;

import java.util.Map;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.handler.Handler;
import com.iisigroup.cap.model.OpStepContext;

/**
 * <p>
 * Operation.
 * </p>
 * 
 * @author iristu
 * @version
 *          <ul>
 *          <li>2010/7/22,iristu,new
 *          </ul>
 */
public interface Operation {

    String getName();

    void setName(String name);

    Map<String, OperationStep> getRuleMap();

    void setRuleMap(Map<String, OperationStep> ruleMap);

    void execute(OpStepContext ctx, Request params, Handler handler);
}
