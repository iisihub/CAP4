/*
 * UserRoleDao.java
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

import com.iisigroup.cap.auth.model.UserRole;
import com.iisigroup.cap.db.dao.GenericDao;

public interface UserRoleDao extends GenericDao<UserRole> {

    int deleteByRoleCodeAndUserCodes(String roleCode, List<String> delUsr);

    UserRole findByUserCodeAndRoleCode(String userCode, String roleCode);

    void deleteByUserCode(String userCode);
}
