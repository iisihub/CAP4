/*
 * COLASystemProperties.java
 *
 * Copyright (c) 2009-2015 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.common.jsp;

import java.util.List;

import com.iisigroup.cap.base.CapSystemProperties;
import com.iisigroup.cap.db.service.CommonService;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.common.model.COLASysParm;

/**
 * <pre>
 * JSP bean, let jsp show what system properties is.
 * add SysParm information into this.
 * 順序是 System properties -> SysParm
 * </pre>
 *
 * @since 2015-12-23
 * @author Bo-Xuan Fan
 * @version
 *          <ul>
 *          <li>2015-12-23,Bo-Xuan Fan,new
 *          </ul>
 */
public class COLASystemProperties extends CapSystemProperties{

    /** serialVersionUID */
    private static final long serialVersionUID = 5956843085955765523L;

    private List<String> ignoreCache;

    @Override
    public String get(Object key) {

        if (super.get(key) != null && (ignoreCache == null || !ignoreCache.contains(key))) {
            return super.get(key);
        }

        String sKey = key != null ? key.toString() : null;
        if (CapString.isEmpty(sKey)) {
            return null;
        }

        String val = null;
        val = System.getProperty(sKey);

        if (!CapString.isEmpty(val)) {
            put(sKey, val);
            return val;
        }

        CommonService commonSrv = CapAppContext.getApplicationContext().getBean(CommonService.class);
        COLASysParm sysParm = commonSrv.findById(COLASysParm.class, sKey);
        val = sysParm != null ? sysParm.getParmValue() : null;
        put(sKey, val);
        return val;
    }

    public void setIgnoreCache(List<String> ignoreCache) {
        this.ignoreCache = ignoreCache;
    }
}
