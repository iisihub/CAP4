package com.iisigroup.colabase.import_.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.iisigroup.colabase.import_.dao.ImportCustomerDao;
import com.iisigroup.colabase.import_.dao.impl.ImportCustomerDaoImpl;
import com.iisigroup.colabase.import_.service.ImportFileService;

import static org.junit.Assert.*;

@RunWith(value = MockitoJUnitRunner.class)
public class ImportFileServiceImplTest {
    
    // 測試前請設定netPath, domain, uName, uXwd，

    // 檔案資料夾路徑
    private final String filePath = "D:/TEST/TEST_EXPORT/"; // 自訂
    // 檔案名稱
    private final String fileName = "demo_customer_gen_data.txt"; // 自訂
    // 指定天數
    private final int days = 50; // 自訂
    // 指定天
    private final String date = "20180417"; // 自訂
    // 指定天格式(上面date的格式，例：yyyyMMdd)
    private final String dateFormat = "yyyyMMdd"; // 自訂
    // 檔案筆數
    private final int count = 3; // 自訂

    @Spy
    private ImportCustomerDao custDao;
    
    @Spy
    private ImportFileService importFileService;

    @Before
    public void setUp() throws Exception {
        custDao = new ImportCustomerDaoImpl();
        importFileService = new ImportFileServiceImpl(custDao);
    }

    @Test
    public void testCheckTime() throws Exception {
        ImportFileServiceImpl test = new ImportFileServiceImpl();
//        String filePath = "D:/TEST/TEST_EXPORT/";
//        String fileName = "demo_customer_gen_data.txt";
//        int days = 50;
        boolean result = test.checkTime(filePath, fileName, days);
        assertEquals(true, result);
    }
    
    @Test
    public void testCheckDate() throws Exception {
        ImportFileServiceImpl test = new ImportFileServiceImpl();
//        String filePath = "D:/TEST/TEST_EXPORT/";
//        String fileName = "demo_customer_gen_data.txt";
//        String date = "20180417";
//        String dateFormat = "yyyyMMdd";
        boolean result = test.checkDate(filePath, fileName, date, dateFormat);
        assertEquals(true, result);
    }
    
    @Test
    public void testCountRows() throws Exception {
        ImportFileServiceImpl test = new ImportFileServiceImpl();
//        String filePath = "D:/TEST/TEST_EXPORT/";
//        String fileName = "demo_customer_gen_data.txt";
        int result = test.countRows(filePath, fileName);
        assertEquals(count, result);
    }

}