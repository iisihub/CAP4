/*
 * CapHandleOpStep.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.operation.step;

import com.iisigroup.cap.action.Action;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.handler.Handler;
import com.iisigroup.cap.model.OpStepContext;

/**
 * <pre>
 * CapFormHandleOpStep
 * </pre>
 * 
 * @since 2010/7/23
 * @author iristu
 * @version
 *          <ul>
 *          <li>2010/7/23,iristu,new
 *          <li>2013/1/28,RodesChen,remove setName
 *          </ul>
 */
public class CapHandleOpStep extends AbstractCustomizeOpStep {

    @Override
    public OpStepContext execute(OpStepContext ctx, Request params, Handler handler) {
        Result rtn = null;
        @SuppressWarnings("static-access")
        String actionType = params.get(handler.FORM_ACTION);
        Action action = handler.getAction(actionType);
        rtn = action.doWork(params);
        ctx.setGoToStep(NEXT);
        ctx.setResult(rtn);
        return ctx;
    }

}// ~
