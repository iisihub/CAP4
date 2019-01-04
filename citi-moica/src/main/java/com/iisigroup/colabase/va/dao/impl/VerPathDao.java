/* 
 * VerPathDao.java
 * 
 * Copyright (c) 2009-2013 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.va.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;
import com.iisigroup.colabase.va.dao.IVerPathDao;
import com.iisigroup.colabase.va.model.VerPath;

/**
 * <pre>
 * VerPath Dao impl
 * </pre>
 * 
 * @since 2014/4/16
 * @author TimChiang
 * @version
 *          <ul>
 *          <li>2014/4/16,TimChiang,new
 *          </ul>
 */
 @Repository("verPathDao")
public class VerPathDao extends GenericDaoImpl<VerPath> implements IVerPathDao {
     
     private static final String PARMID = "parmId"; 
     
    // @Override
    public VerPath findByOid(String oid) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "oid", oid);
        search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
        return findUniqueOrNone(search);
    }

    // @Override
    public VerPath findByVerPathId(String parmId) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, PARMID, parmId);
        search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
        return findUniqueOrNone(search);
    }

    // @Override
    public List<VerPath> findAllHost() {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.LIKE, PARMID, "host." + "%");
        search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
        search.addOrderBy(PARMID);
        return find(search);
    }

}
