/*
 * UserDao.java
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

import java.util.Map;

import com.iisigroup.cap.auth.model.DefaultUser;
import com.iisigroup.cap.db.dao.GenericDao;
import com.iisigroup.cap.db.model.Page;

public interface UserDao extends GenericDao<DefaultUser> {
    DefaultUser findByCode(String userId);

    Page<Map<String, Object>> findPage(String userId, String userName, String[] roleOids, String[] status, int maxResults, int firstResult);

    Page<Map<String, Object>> findPageByRoleCode(String roleCode, int firstResult, int maxResults);

    Page<Map<String, Object>> findPageUnselectedByRoleCodeAndDepCode(String roleCode, String depCode, int firstResult, int maxResults);

    void processUserStatus(int pwdExpiredDay, int pwdAccountDisable, int pwdAccountDelete);

}
