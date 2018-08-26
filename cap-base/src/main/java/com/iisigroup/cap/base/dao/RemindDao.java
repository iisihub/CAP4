/*
 * RemindDao.java
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

import java.sql.Timestamp;
import java.util.List;

import com.iisigroup.cap.base.model.Remind;
import com.iisigroup.cap.db.dao.GenericDao;

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
public interface RemindDao extends GenericDao<Remind> {

    public Remind findByPid(String pid);

    /**
     * @param userId
     *            登入者ID
     * @param start
     *            Timestamp
     * @param end
     *            Timestamp
     * @param locale
     *            語系
     * @return
     */
    public List<Remind> getCalendarData(String userId, Timestamp start, Timestamp end, String locale);

}
