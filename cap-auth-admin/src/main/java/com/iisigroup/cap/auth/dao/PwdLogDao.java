/*
 * PwdLogDao.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.auth.dao;

import java.util.List;

import com.iisigroup.cap.auth.model.PwdLog;
import com.iisigroup.cap.db.dao.GenericDao;

public interface PwdLogDao extends GenericDao<PwdLog> {
    List<PwdLog> findByUserCode(String userCode, int maxHistory);
}
