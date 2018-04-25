/*
 * Copyright (c) 2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.admin.test;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iisigroup.cap.base.model.CodeType;
import com.iisigroup.cap.base.service.CodeTypeService;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.operation.simple.SimpleContextHolder;
import com.iisigroup.cap.utils.CapWebUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * <pre>
 * CodeTypeService test case
 * </pre>
 * 
 * @since 2018年4月24日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2018年4月24日,bob peng,new
 *          </ul>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:admin-test/applicationContext.xml")
public class CodeTypeServiceTest {
    @Autowired
    @Qualifier("txManager")
    private JpaTransactionManager txManager;
    @Autowired
    @Qualifier("capJdbcTxManager")
    private DataSourceTransactionManager capJdbcTxManager;
    @Autowired
    private CodeTypeService codeTypeService;

    private static final String CODETYPE = "test";
    private static final String CODEVALUE = "0";
    private static final String CODEDESC = "測試";
    private static final String MODIFIED_CODEDESC = "測試修改";
    private static final String LOCALE = "zh_TW";

    public CodeTypeServiceTest() {
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

    @Before
    public void before() {
        SimpleContextHolder.put(CapWebUtil.localeKey, LOCALE);
        // add codetype
        codeTypeService.addCodeType(CODETYPE, CODEVALUE, CODEDESC, 0, LOCALE);
    }

    @After
    public void after() {
        // delete codetype
        CodeType codetype = codeTypeService.getByCodeTypeAndValue(CODETYPE, CODEVALUE);
        codeTypeService.deleteById(codetype.getOid());
        Assert.assertNull(codeTypeService.getByCodeTypeAndValue(CODETYPE, CODEVALUE));
    }

    @Test
    public void test1() {
        CodeType codetype_1 = codeTypeService.getByCodeTypeAndValue(CODETYPE, CODEVALUE);
        Assert.assertNotNull(codetype_1);
        Assert.assertEquals(CODEDESC, codetype_1.getCodeDesc());
        CodeType codetype_2 = codeTypeService.getById(codetype_1.getOid());
        Assert.assertNotNull(codetype_2);
        Assert.assertEquals(CODEDESC, codetype_2.getCodeDesc());
    }

    @Test
    public void test2() {
        Map<String, String> map = codeTypeService.findByCodeType(CODETYPE);
        Assert.assertEquals(CODEDESC, map.get(CODEVALUE));
    }

    @Test
    public void test3() {
        Map<String, Map<String, String>> map = codeTypeService.findByCodeTypes(new String[] { CODETYPE });
        Assert.assertEquals(CODEDESC, map.get(CODETYPE).get(CODEVALUE));
    }

    @Test
    public void test4() {
        Map<String, AjaxFormResult> map = codeTypeService.getCodeTypeByTypes(new String[] { CODETYPE });
        Assert.assertEquals(CODEDESC, map.get(CODETYPE).get(CODEVALUE));
    }

    @Test
    public void test5() {
        CodeType codetype_1 = codeTypeService.getByCodeTypeAndValue(CODETYPE, CODEVALUE);
        // modify
        codeTypeService.modifyCodeType(codetype_1.getOid(), CODETYPE, CODEVALUE, MODIFIED_CODEDESC, 0, LOCALE);
        CodeType codetype_2 = codeTypeService.getByCodeTypeAndValue(CODETYPE, CODEVALUE);
        Assert.assertEquals(MODIFIED_CODEDESC, codetype_2.getCodeDesc());
    }

}