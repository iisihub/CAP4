/*
 * RemindsDao.java
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

import com.iisigroup.cap.base.model.Reminds;
import com.iisigroup.cap.db.dao.GenericDao;

/**
 * <pre>
 * 通知項目Dao
 * </pre>
 * 
 * @since 2014/1/27
 * @author tammy
 * @version
 *          <ul>
 *          <li>2014/1/27,tammy,new
 *          </ul>
 */
public interface RemindsDao extends GenericDao<Reminds> {

    List<Reminds> findCurrentRemindItem(String[] styleTyp, String locale);

    void merge(Reminds entity);

}
