/* 
 * 
 * Copyright (c) 2009-2012 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.handler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;

import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.base.annotation.CapAuditLogAction;
import com.iisigroup.cap.base.annotation.CapAuditLogAction.CapActionTypeEnum;
import com.iisigroup.cap.base.constants.CapFunctionCode;
import com.iisigroup.cap.base.model.CodeType;
import com.iisigroup.cap.base.service.CodeTypeService;
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
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * base Handler
 * </pre>
 * 
 * @since 2011/11/28
 * @author rodeschen
 * @version
 *          <ul>
 *          <li>2011/11/28,rodeschen,new
 *          <li>2011/12/23，gabriella,add method
 *          <li>2012/3/1,rodeschen,增加判斷重覆及更改程式名稱
 *          </ul>
 */
@Controller("codetypehandler")
public class CodeTypeHandler extends MFormHandler {

    @Resource
    private CodeTypeService codeTypeService;

    /**
     * 共用參數 grid
     * 
     * @param search
     * @param params
     * @return
     */
    @CapAuditLogAction(functionCode = CapFunctionCode.F101, actionType = CapActionTypeEnum.QUERY)
    @HandlerType(HandlerTypeEnum.GRID)
    public BeanGridResult query(SearchSetting search, Request params) {
        Page<CodeType> page = codeTypeService.findPage(search, params);
        Map<String, Formatter> fmt = new HashMap<String, Formatter>();
        fmt.put("updateTime", new ADDateFormatter());
        return new BeanGridResult(page.getContent(), page.getTotalRow(), fmt);
    }

    /**
     * add codetype
     * 
     * @param request
     *            request
     * @return IResult
     */
    @CapAuditLogAction(functionCode = CapFunctionCode.F101, actionType = CapActionTypeEnum.ADD)
    public Result add(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String codeType = request.get("codeType");
        String codeValue = request.get("codeValue");
        String codeDesc = request.get("codeDesc");
        String codeOrder = request.get("codeOrder");
        String locale = request.get("locale");
        codeTypeService.addCodeType(codeType, codeValue, codeDesc, Integer.parseInt(codeOrder), locale);
        result.set(Constants.AJAX_NOTIFY_MESSAGE, CapAppContext.getMessage("js.addSuccess"));
        return result;
    }

    /**
     * modify codetype
     * 
     * @param request
     *            request
     * @return IResult
     */
    @CapAuditLogAction(functionCode = CapFunctionCode.F101, actionType = CapActionTypeEnum.UPDATE)
    public Result modify(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String oid = request.get("oid");
        String codeType = request.get("codeType");
        String codeValue = request.get("codeValue");
        String codeDesc = request.get("codeDesc");
        String codeOrder = request.get("codeOrder");
        String locale = request.get("locale");
        codeTypeService.modifyCodeType(oid, codeType, codeValue, codeDesc, Integer.parseInt(codeOrder), locale);
        result.set(Constants.AJAX_NOTIFY_MESSAGE, CapAppContext.getMessage("js.modifySuccess"));
        return result;
    }

    /**
     * delete codetype
     * 
     * @param request
     *            request
     * @return IResult
     */
    @CapAuditLogAction(functionCode = CapFunctionCode.F101, actionType = CapActionTypeEnum.DELETE)
    public Result delete(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        codeTypeService.deleteById(request.get("oid"));
        result.set(Constants.AJAX_NOTIFY_MESSAGE, CapAppContext.getMessage("js.deleteSuccess"));
        return result;
    }

    /**
     * get combo list By Keys
     * 
     * @param request
     *            request
     * @return IResult
     */
    @CapAuditLogAction(functionCode = CapFunctionCode.F101, actionType = CapActionTypeEnum.QUERY)
    @SuppressWarnings("rawtypes")
    public Result queryByKeys(Request request) {
        String locale = CapSecurityContext.getLocale().toString();
        String[] keys = request.getParamsAsStringArray("keys");
        String[] aKeys = request.getParamsAsStringArray("akeys");
        AjaxFormResult mresult = new AjaxFormResult();
        if (keys.length > 0 && !CapString.isEmpty(keys[0])) {
            Set<String> k = new HashSet<String>(Arrays.asList(keys));// 排除重覆的key
            Map<String, AjaxFormResult> m = codeTypeService.getCodeTypeByTypes(k.toArray(new String[k.size()]), locale);
            mresult.setResultMap(m);
        }
        if (aKeys.length > 0 && !CapString.isEmpty(aKeys[0])) {
            Class[] paramTypes = { Request.class };
            Result rtn = null;
            for (String key : aKeys) {
                if (mresult.containsKey(key)) {
                    continue;
                }
                Method method = ReflectionUtils.findMethod(this.getClass(), key, paramTypes);
                if (method != null) {
                    try {
                        rtn = (Result) method.invoke(this, request);
                    } catch (Exception e) {
                        logger.error("load ComboBox error : key = " + key, e);
                    }
                    mresult.set(key, (AjaxFormResult) rtn);
                }
            }
        }
        return mresult;
    }

}// ~
