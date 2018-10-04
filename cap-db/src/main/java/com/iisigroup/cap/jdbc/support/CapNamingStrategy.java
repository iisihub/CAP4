/* 
 * CapNamingStrategy.java
 * 
 * Copyright (c) 2009-2014 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.jdbc.support;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.internal.util.StringHelper;

/**
 * <pre>
 * Custom Naming Strategy.
 * </pre>
 * 
 * @since 2014/3/31
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2014/3/31,Sunkist Wang,new
 *          <li>2018/10/4,Sunkist Wang,update
 *          </ul>
 */
public class CapNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    /***/
    private static final long serialVersionUID = 1L;

    private static final String TABLE_PREFIX = "";

    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        return context.getIdentifierHelper().toIdentifier((new StringBuilder()).append(TABLE_PREFIX).append(StringHelper.unqualify(name.getText())).toString(), name.isQuoted());
    }

}
