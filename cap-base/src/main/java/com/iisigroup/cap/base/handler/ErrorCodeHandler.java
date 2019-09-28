/* 
 * ErrorCodeHandler.java
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

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.base.model.ErrorCode;
import com.iisigroup.cap.base.service.ErrorCodeService;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.BeanGridResult;
import com.iisigroup.cap.constants.Constants;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.model.Page;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapAppContext;

/**
 * <pre>
 * 訊息代碼維護
 * </pre>
 * 
 * @since 2013/12/31
 * @author tammy
 * @version
 *          <ul>
 *          <li>2013/12/31,tammy,new
 *          </ul>
 */
@Controller("errorCodehandler")
public class ErrorCodeHandler extends MFormHandler {

    @Resource
    private ErrorCodeService errorCodeService;

    /**
     * 訊息代碼維護 grid
     * 
     * @param search
     * @param params
     * @return
     */
    @HandlerType(HandlerTypeEnum.GRID)
    public BeanGridResult query(SearchSetting search, Request params) {
        Page<ErrorCode> page = errorCodeService.findPage(search, params);
        return new BeanGridResult(page.getContent(), page.getTotalRow());
    }

    /**
     * 新增 ErrorCode
     * 
     * @param request
     * @return
     */
    public Result add(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String code = request.get("code");
        String locale = request.get("locale");
        String severity = request.get("severity");
        String message = request.get("message");
        String suggestion = request.get("suggestion");
        errorCodeService.addErrorCode(code, locale, severity, message, suggestion);
        result.set(Constants.AJAX_NOTIFY_MESSAGE, CapAppContext.getMessage("js.addSuccess"));
        return result;
    }

    /**
     * 修改 ErrorCode
     * 
     * @param request
     * @return
     */
    public Result modify(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String oid = request.get("oid");
        String code = request.get("code");
        String locale = request.get("locale");
        String severity = request.get("severity");
        String message = request.get("message");
        String suggestion = request.get("suggestion");
        errorCodeService.modifyErrorCode(oid, code, locale, severity, message, suggestion);
        result.set(Constants.AJAX_NOTIFY_MESSAGE, CapAppContext.getMessage("js.modifySuccess"));
        return result;
    }

    /**
     * 刪除資料
     * 
     * @param request
     *            IRequest
     * @return {@link Result.com.iisi.cap.response.IResult}
     * @throws CapException
     */
    public Result delete(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        errorCodeService.deleteErrorCodeByOid(request.get("oid"));
        result.set(Constants.AJAX_NOTIFY_MESSAGE, CapAppContext.getMessage("js.deleteSuccess"));
        return result;
    }

}
