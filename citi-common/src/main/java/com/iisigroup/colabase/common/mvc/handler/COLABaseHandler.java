/*
 * COLABaseHandler.java
 *
 * Copyright (c) 2009-2015 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.common.mvc.handler;

import org.springframework.stereotype.Controller;

import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.mvc.handler.BaseHandler;
import com.iisigroup.cap.mvc.i18n.MessageBundleScriptCreator;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.common.exception.GotoErrorPageException;

/**
 * <pre>
 * COLA base handler
 * </pre>
 *
 * @since 2015年5月18日
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2015年5月18日,Sunkist Wang,new
 *          </ul>
 */
@Controller("basehandler")
public class COLABaseHandler extends BaseHandler {

    @HandlerType(HandlerTypeEnum.FORM)
    public Result queryJsI18N(Request request) {
        if (CapString.isEmpty(request.get("f", ""))) {
            throw new GotoErrorPageException("Empty object!", getClass());
        }
        String result = MessageBundleScriptCreator.generateJson(request.get("f").replaceAll("/?webroot/page", ""));
        return new AjaxFormResult(result);
    }
}
