/*
 * ErrorCodeService.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.service;

import java.util.List;

import com.iisigroup.cap.base.model.ErrorCode;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.model.Page;

/**
 * <pre>
 * 訊息代碼表
 * </pre>
 * 
 * @since 2012/03/29
 * @author UFOJ
 * @version
 *          <ul>
 *          <li>2012/03/29,UFO,new
 *          </ul>
 */
public interface ErrorCodeService {

    /**
     * 重新載入分行資訊
     */
    void reload();

    /**
     * add ErrorCode
     * 
     * @param code
     * @param locale
     * @param severity
     * @param message
     * @param suggestion
     */
    void addErrorCode(String code, String locale, String severity, String message, String suggestion);

    /**
     * modify ErrorCode
     * 
     * @param oid
     * @param code
     * @param locale
     * @param severity
     * @param message
     * @param suggestion
     */
    void modifyErrorCode(String oid, String code, String locale, String severity, String message, String suggestion);

    /**
     * get the error code by code and locale
     * 
     * @param ErrorCode
     *            代碼類型
     * @param locale
     *            語系
     * @return error code
     * 
     */
    ErrorCode getErrorCode(String code, String locale);

    List<ErrorCode> getErrorCodeListBySysId(String sysId, String locale);

    /**
     * delete ErrorCode By Oid
     * 
     * @param oid
     */
    void deleteErrorCodeByOid(String oid);

    /**
     * find page
     * 
     * @param search
     * @param params
     * @return
     */
    Page<ErrorCode> findPage(SearchSetting search, Request params);

}
