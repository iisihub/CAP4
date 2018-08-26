/*
 * RemindDaoImpl.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.dao.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.iisigroup.cap.base.dao.RemindDao;
import com.iisigroup.cap.base.model.Remind;
import com.iisigroup.cap.base.support.RemindRowMapper;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;

/**
 * <pre>
 * 提醒通知Dao
 * </pre>
 * 
 * @since 2014/1/27
 * @author tammy
 * @version
 *          <ul>
 *          <li>2014/1/27,tammy,new
 *          </ul>
 */
@Repository
public class RemindDaoImpl extends GenericDaoImpl<Remind> implements RemindDao {

    @Override
    public Remind findByPid(String pid) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "oid", pid);
        return findUniqueOrNone(search);
    }

    @Override
    public List<Remind> getCalendarData(String userId, Timestamp start, Timestamp end, String locale) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("start", start);
        param.put("end", end);
        param.put("userId", userId);
        param.put("locale", locale);
        return getNamedJdbcTemplate().query("remind_getCalendarData", "", param, new RemindRowMapper());
    }
}
