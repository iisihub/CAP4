/*
 * CrlCertDao.java
 *
 * Copyright (c) 2009-2016 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.va.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.iisigroup.colabase.va.dao.ICrlCertDao;
import com.iisigroup.colabase.va.model.CrlCert;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.colabase.common.dao.MOMJpaDao;
import com.iisigroup.colabase.common.dao.MOMNamingStrategy;
import com.iisigroup.cap.db.constants.SearchMode;

/**
 * <pre>
 * CrlCertDao
 * </pre>
 *
 * @since 2016-04-25
 * @author Bo-Xuan Fan
 * @version
 *          <ul>
 *          <li>2016-04-25,Bo-Xuan Fan,new
 *          </ul>
 */
@Repository
public class CrlCertDao extends MOMJpaDao<CrlCert> implements ICrlCertDao {

    private static final String SQL_COUNT_BY_CRL_TYPE;
    private static final String SQL_DELETE_BY_CRL_TYPE;

    static {
        Class<?> tableClz = CrlCert.class;
        Table tableAnnotation = tableClz.getAnnotation(Table.class);
        final String TABLE_NAME = new MOMNamingStrategy().tableName(tableAnnotation.name());

        StringBuilder sql = new StringBuilder("select count(CRL_TYPE) from ");
        sql.append(TABLE_NAME).append(" where CRL_TYPE = :crlType");
        SQL_COUNT_BY_CRL_TYPE = sql.toString();

        sql = new StringBuilder("delete ");
        sql.append(TABLE_NAME).append(" where CRL_TYPE = :crlType");
        SQL_DELETE_BY_CRL_TYPE = sql.toString();
    }

    @Value("${hibernate.jdbc.batch_size}")
    private int batchSize;

    public int findCrlCountsByCertType(int crlType) {
        // select count(CRL_TYPE) from CO_CRL_CERT where CRL_TYPE = :crlType
        Query query = getEntityManager()
                .createNativeQuery(SQL_COUNT_BY_CRL_TYPE);
        query.setParameter("crlType", crlType);
        return Integer.parseInt(query.getSingleResult().toString());
    }

    public CrlCert findBySerialNoAndCertType(String serialNo, int crlType) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "crlCertPK.serialNo", serialNo);
        search.addSearchModeParameters(SearchMode.EQUALS, "crlCertPK.crlType", crlType);
        return findUniqueOrNone(search);
    }

    public void truncate(int crlType) {
        // delete from CO_CRL_CERT where CRL_TYPE = :crlType
        Query query = getEntityManager()
                .createNativeQuery(SQL_DELETE_BY_CRL_TYPE, CrlCert.class);
        query.setParameter("crlType", crlType);
        query.executeUpdate();
    }

    public void batchSave(List<CrlCert> crlCerts) {
        if (crlCerts == null) {
            return;
        }
        int i = 0;
        for (CrlCert crlCert : crlCerts) {
            getEntityManager().persist(crlCert);
            if (++i % batchSize == 0) {
                getEntityManager().flush();
                getEntityManager().clear();
            }
        }
        getEntityManager().flush();
        getEntityManager().clear();
    }
}
