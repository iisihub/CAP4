/* 
 * SysParmHandler.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.handler;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.base.annotation.CapAuditLogAction;
import com.iisigroup.cap.base.annotation.CapAuditLogAction.CapActionTypeEnum;
import com.iisigroup.cap.base.constants.CapFunctionCode;
import com.iisigroup.cap.base.model.SysParm;
import com.iisigroup.cap.base.service.SysParmService;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.BeanGridResult;
import com.iisigroup.cap.constants.Constants;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.model.Page;
import com.iisigroup.cap.formatter.Formatter;
import com.iisigroup.cap.formatter.impl.ADDateFormatter;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapAppContext;

/**
 * <pre>
 * 系統設定維護
 * </pre>
 * 
 * @since 2012/10/25
 * @author iristu
 * @version
 *          <ul>
 *          <li>2012/10/25,iristu,new
 *          </ul>
 */
@Controller("sysparmhandler")
public class SysParmHandler extends MFormHandler {

    @Resource
    private SysParmService sysParmService;

    /**
     * 系統設定維護 grid
     * 
     * @param search
     * @param params
     * @return
     */
    @HandlerType(HandlerTypeEnum.GRID)
    @CapAuditLogAction(functionCode = CapFunctionCode.F102, actionType = CapActionTypeEnum.QUERY)
    public BeanGridResult query(SearchSetting search, Request params) {
        Page<SysParm> page = sysParmService.findPage(search, params);
        Map<String, Formatter> fmt = new HashMap<String, Formatter>();
        fmt.put("updateTime", new ADDateFormatter());
        return new BeanGridResult(page.getContent(), page.getTotalRow(), fmt);
    }

    /**
     * add SysParm
     * 
     * @param request
     *            request
     * @return IResult
     */
    @CapAuditLogAction(functionCode = CapFunctionCode.F102, actionType = CapActionTypeEnum.ADD)
    public Result add(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String parmId = request.get("parmId");
        String parmValue = request.get("parmValue");
        String parmDesc = request.get("parmDesc");
        sysParmService.addSysParm(parmId, parmValue, parmDesc);
        result.set(Constants.AJAX_NOTIFY_MESSAGE, CapAppContext.getMessage("js.addSuccess"));
        return result;
    }

    /**
     * modify SysParm
     * 
     * @param request
     *            request
     * @return IResult
     */
    @CapAuditLogAction(functionCode = CapFunctionCode.F102, actionType = CapActionTypeEnum.UPDATE)
    public Result modify(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String parmId = request.get("parmId");
        String parmValue = request.get("parmValue");
        String parmDesc = request.get("parmDesc");
        sysParmService.modifySysParm(parmId, parmValue, parmDesc);
        result.set(Constants.AJAX_NOTIFY_MESSAGE, CapAppContext.getMessage("js.modifySuccess"));
        return result;
    }

    /**
     * delete SysParm
     * 
     * @param request
     *            request
     * @return IResult
     */
    @CapAuditLogAction(functionCode = CapFunctionCode.F102, actionType = CapActionTypeEnum.DELETE)
    public Result delete(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        sysParmService.deleteSysParmByParmId(request.get("parmId"));
        result.set(Constants.AJAX_NOTIFY_MESSAGE, CapAppContext.getMessage("js.deleteSuccess"));
        return result;
    }

}
