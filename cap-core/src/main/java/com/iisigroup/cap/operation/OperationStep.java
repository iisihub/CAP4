/*
 * OperationStep.java
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

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.handler.Handler;
import com.iisigroup.cap.model.OpStepContext;

/**
 * <p>
 * OperationStep.
 * </p>
 * 
 * @author iristu
 * @version
 *          <ul>
 *          <li>2010/7/23,iristu,new
 *          </ul>
 */
public interface OperationStep {

    String NEXT = "next";

    String ERROR = "error";

    String RETURN = "return";

    String getName();

    void setName(String name);

    OpStepContext execute(OpStepContext ctx, Request params, Handler handler);

    OpStepContext handleException(OpStepContext ctx, Exception e);

}
