/*
 * ErrorCodeDao.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.dao;

import java.util.List;

import com.iisigroup.cap.base.model.ErrorCode;
import com.iisigroup.cap.db.dao.GenericDao;

/**
 * <pre>
 * 訊息代碼Dao
 * </pre>
 * 
 * @since 2011/08/02
 * @author UFO
 * @version
 *          <ul>
 *          <li>2011/08/02,UFO,new
 *          </ul>
 */
public interface ErrorCodeDao extends GenericDao<ErrorCode> {

    List<ErrorCode> findByAll();

    ErrorCode findByCode(String code, String locale);

    List<ErrorCode> findListBySysId(String sysId, String locale);

}
