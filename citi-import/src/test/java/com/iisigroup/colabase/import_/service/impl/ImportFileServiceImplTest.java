package com.iisigroup.colabase.import_.service.impl;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.iisigroup.colabase.import_.dao.ImportCustomerDao;


import com.iisigroup.colabase.import_.dao.impl.ImportCustomerDaoImpl;
import com.iisigroup.colabase.import_.service.ImportFileService;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import static org.junit.Assert.*;

@RunWith(value = MockitoJUnitRunner.class)
public class ImportFileServiceImplTest {

    @Spy
    private ImportCustomerDao custDao;
    
    @Spy
    private ImportFileService importFileService;

    @Before
    public void setUp() throws Exception {
        
//        Properties jndiProps = new Properties();
//        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
//        jndiProps.put(Context.PROVIDER_URL,"remote://localhost:4447");
//        Context ctx=new InitialContext(jndiProps);
////        DataSource ds = (DataSource) ctx.lookup("jdbc/ccdb3");
//        DataSource ds = (DataSource) ctx.lookup("jdbc:sqlserver://localhost:1433;DatabaseName=XCOLA;SelectMethod=direct");
        
        SQLServerDataSource ds = new SQLServerDataSource();  
        ds.setUser("sa");  
        ds.setPassword("P@ssw0rd");
        ds.setServerName("localhost");
        ds.setPortNumber(1433);
        ds.setDatabaseName("XCOLA");
        ds.setSelectMethod("direct");
        
        custDao = new ImportCustomerDaoImpl(ds);
//        custDao.setDataSource(ds);
        importFileService = new ImportFileServiceImpl(custDao);

    }

    @Test
    public void testCheckTime() throws Exception {
        ImportFileServiceImpl test = new ImportFileServiceImpl();
        String filePath = "D:/TEST/TEST_EXPORT/";
        String fileName = "demo_customer_gen_data.txt";
        int days = 30;
        boolean result = test.checkTime(filePath, fileName, days);
        assertEquals(true, result);
    }
    
    @Test
    public void testCheckDate() throws Exception {
        ImportFileServiceImpl test = new ImportFileServiceImpl();
        String filePath = "D:/TEST/TEST_EXPORT/";
        String fileName = "demo_customer_gen_data.txt";
        String date = "20180417";
        String dateFormat = "yyyyMMdd";
        boolean result = test.checkDate(filePath, fileName, date, dateFormat);
        assertEquals(true, result);
    }
    
    @Test
    public void testCountRows() throws Exception {
        ImportFileServiceImpl test = new ImportFileServiceImpl();
        String filePath = "D:/TEST/TEST_EXPORT/";
        String fileName = "demo_customer_gen_data.txt";
        int result = test.countRows(filePath, fileName);
        assertEquals(3, result);
    }
    
    @Test
    public void testRunSP() throws Exception {
//        ImportFileServiceImpl test = new ImportFileServiceImpl();
        String importFile = "D:/data/Demo_Import_AP/demo_customer_gen_data.txt";
        String storedProcedureName = "DEMO_IMPORT_CUSTOMER_DATA";
        List result = importFileService.runSP(importFile, storedProcedureName);
        assertEquals("2", result.get(0));

    }
}