/*
 * CCEntityUtil.java
 *
 * Copyright (c) 2009-2014 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.webatm.toolkit;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.iisigroup.cap.db.utils.CapEntityUtil;

/**
 * <pre>
 * Credit Card Utils.
 * </pre>
 *
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2014/1/28,Sunkist Wang,new
 *          </ul>
 * @since 2014/1/28
 */
public class CCEntityUtil extends CapEntityUtil {

    /**
     * 取得傳入entity所有欄位名稱，排除Id
     *
     * @param <T>
     *            entity
     * @param entity
     *            jpa entity
     * @return String[]
     */
    public static <T> String[] getColumnName(T entity) {
        Set<Class<? extends Annotation>> ignore = new HashSet<Class<? extends Annotation>>();
        ignore.add(OneToMany.class);
        ignore.add(OneToOne.class);
        ignore.add(ManyToMany.class);
        ignore.add(ManyToOne.class);
        ignore.add(Id.class);
        return getColumnName(entity, ignore);
    }
}
