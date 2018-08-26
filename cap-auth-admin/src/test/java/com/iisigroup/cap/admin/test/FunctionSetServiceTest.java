/*
 * FunctionSetServiceTest.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.admin.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iisigroup.cap.auth.service.FunctionSetService;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * <pre>
 * Service Test Program Example
 * </pre>
 * 
 * @since 2017年7月20日
 * @author Lancelot
 * @version
 *          <ul>
 *          <li>2017年7月20日,Lancelot,new
 *          </ul>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:admin-test/applicationContext.xml")
public class FunctionSetServiceTest {
    @Autowired
    @Qualifier("txManager")
    private JpaTransactionManager txManager;
    @Autowired
    @Qualifier("capJdbcTxManager")
    private DataSourceTransactionManager capJdbcTxManager;
    @Autowired
    private FunctionSetService functionSetService;

    public FunctionSetServiceTest() {
        try {
            SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
            ComboPooledDataSource dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass("org.h2.Driver");
            dataSource.setJdbcUrl("jdbc:h2:../h2db/capdb");
            dataSource.setUser("sa");
            dataSource.setPassword("");
            builder.bind("java:comp/env/jdbc/capdb", dataSource);
            builder.activate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testProvideService() {
        // functionSetService.findFunctionBySysTypeAndLevel("A", "1");
        // functionSetService.findPage(new SearchSettingImpl(), "A", "1");
        List<String> roles = new ArrayList<String>();
        roles.add("AI0001");
        functionSetService.deleteRfList("939241", roles);
    }
}