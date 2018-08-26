/*
 * PwdLogDaoImpl.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.auth.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.iisigroup.cap.auth.dao.PwdLogDao;
import com.iisigroup.cap.auth.model.PwdLog;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;

@Repository
public class PwdLogDaoImpl extends GenericDaoImpl<PwdLog> implements PwdLogDao {

    @Override
    public List<PwdLog> findByUserCode(String userCode, int maxHistory) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "userCode", userCode);
        search.setMaxResults(maxHistory);
        search.addOrderBy("updateTime", true);
        return find(search);
    }

}
