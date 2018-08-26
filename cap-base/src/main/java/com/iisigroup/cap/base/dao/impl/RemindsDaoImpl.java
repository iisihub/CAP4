/*
 * RemindsDaoImpl.java
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.iisigroup.cap.base.dao.RemindsDao;
import com.iisigroup.cap.base.model.Reminds;
import com.iisigroup.cap.base.support.RemindsRowMapper;
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
public class RemindsDaoImpl extends GenericDaoImpl<Reminds> implements RemindsDao {

    @Override
    public List<Reminds> findCurrentRemindItem(String[] styleTyp, String locale) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("styleTyp", Arrays.asList(styleTyp));
        param.put("locale", locale);
        return getNamedJdbcTemplate().query("reminds_findCurrentRemindItem", "", param, new RemindsRowMapper());
    }

    @Override
    public void merge(Reminds entity) {
        super.merge(entity);
    }
}
