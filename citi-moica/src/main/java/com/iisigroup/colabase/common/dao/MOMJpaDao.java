/*
 * MOMJpaDao.java
 *
 * Copyright (c) 2009-2015 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.common.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;
/**
 * <pre>
 * MOMJpaDao
 * </pre>
 *
 * @since 2015-12-25
 * @author Bo-Xuan Fan
 * @version
 *          <ul>
 *          <li>2015-12-25,Bo-Xuan Fan,new
 *          </ul>
 */
public class MOMJpaDao<T> extends GenericDaoImpl<T> {

    @PersistenceContext(unitName = "pu-mom")
    protected EntityManager entityManager;

    @Autowired
    @Qualifier("capJdbcTemplate")
    private MOMNamedJdbcTemplate namedJdbcTemplate;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public MOMNamedJdbcTemplate getNamedJdbcTemplate() {
        return namedJdbcTemplate;
    }
}
