/*
 * BatchScheduleDao.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.batch.dao;

import java.util.List;

import com.iisigroup.cap.batch.model.BatchSchedule;
import com.iisigroup.cap.db.dao.GenericDao;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.model.Page;

public interface BatchScheduleDao extends GenericDao<BatchSchedule> {
    Page<BatchSchedule> findForPage(SearchSetting search);

    List<BatchSchedule> findByHostId(List<String> hostIds);

    BatchSchedule findById(String id);

    void update(BatchSchedule schedule);

    void create(BatchSchedule schedule);

    void deleteById(String id);
}
