/*
 * IBankInfoDao.java
 *
 * Copyright (c) 2009-2013 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.va.dao;

import java.util.List;

import com.iisigroup.colabase.va.model.VerPath;
import com.iisigroup.cap.db.dao.GenericDao;

/**
 * <pre>
 * VerPath Dao interface
 * </pre>
 *
 * @since 2014/4/16
 * @author TimChiang
 * @version
 *          <ul>
 *          <li>2014/4/16,TimChiang,new
 *          </ul>
 */
public interface IVerPathDao extends GenericDao<VerPath> {

    /**
     * 查詢OID
     *
     * @param oid
     *            String
     * @return VerPath
     */
    VerPath findByOid(String oid);

    /**
     * find VerPath by parmId
     *
     * @param parmId
     *            String
     * @return
     */
    VerPath findByVerPathId(String parmId);

    /**
     * find by VerPath list by id like host%
     *
     * @return
     */
    List<VerPath> findAllHost();

}
