/*
 * MOMNamingStrategy.java
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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Table;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.internal.util.StringHelper;

import com.iisigroup.colabase.va.model.CAInfo;
import com.iisigroup.colabase.va.model.CrlCert;
import com.iisigroup.colabase.va.model.TransLog;
import com.iisigroup.colabase.va.model.VerPath;
import com.iisigroup.cap.base.model.SysParm;

/**
 * <pre>
 * MOMNamingStrategy
 * </pre>
 *
 * @since 2015-12-23
 * @author Bo-Xuan Fan
 * @version
 *          <ul>
 *          <li>2015-12-23,Bo-Xuan Fan,new
 *          </ul>
 */
public class MOMNamingStrategy extends ImprovedNamingStrategy {

    /** serialVersionUID */
    private static final long serialVersionUID = 1319392528412231009L;

    /** MOM PREFIX */
    private static final String MOM_PREFIX = "MOM_";
    /** MOMPR PREFIX */
    private static final String MOMPR_PREFIX = "MOMPR_";
    /** COLA PREFIX */
    private static final String COLA_PREFIX = "CO_";

    private static final Set<Class<?>> CO_SPECIAL_CLASSES = new HashSet<Class<?>>();
    private static final Set<Class<?>> MOM_SPECIAL_CLASSES = new HashSet<Class<?>>();
    private static final Set<String> CO_SPECIAL_TABLES = new HashSet<String>();
    private static final Set<String> MOM_SPECIAL_TABLES = new HashSet<String>();

    static {
        CO_SPECIAL_CLASSES.add(SysParm.class);
        CO_SPECIAL_CLASSES.add(CAInfo.class);
        CO_SPECIAL_CLASSES.add(TransLog.class);
        CO_SPECIAL_CLASSES.add(VerPath.class);
        CO_SPECIAL_CLASSES.add(CrlCert.class);

        for (Class<?> clz : CO_SPECIAL_CLASSES) {
            Table tableAnnotation = clz.getAnnotation(Table.class);
            CO_SPECIAL_TABLES.add(tableAnnotation.name());
        }

        for (Class<?> clz : MOM_SPECIAL_CLASSES) {
            Table tableAnnotation = clz.getAnnotation(Table.class);
            MOM_SPECIAL_TABLES.add(tableAnnotation.name());
        }
    }

    @Override
    public String classToTableName(String className) {
        StringBuilder tableName = new StringBuilder();
        if (CO_SPECIAL_CLASSES.contains(getClass(className))) {
            tableName.append(COLA_PREFIX);
        } else if (MOM_SPECIAL_CLASSES.contains(getClass(className))) {
            tableName.append(MOM_PREFIX);
        } else {
            tableName.append(MOMPR_PREFIX);
        }
        return tableName.append(StringHelper.unqualify(className)).toString();
    }

    /**
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            // never happened...
        }
        return null;
    }

    @Override
    public String tableName(String tableName) {
        StringBuilder tableNameBuilder = new StringBuilder();
        if (CO_SPECIAL_TABLES.contains(tableName)) {
            tableNameBuilder.append(COLA_PREFIX);
        } else if (MOM_SPECIAL_TABLES.contains(tableName)) {
            tableNameBuilder.append(MOM_PREFIX);
            } else {
                if (!tableName.startsWith(MOMPR_PREFIX)) {
                    tableNameBuilder.append(MOMPR_PREFIX);
            }
        }
        return tableNameBuilder.append(tableName).toString();
    }

    @Override
    public String columnName(String columnName) {
        return columnName;
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return propertyName;
    }
}
