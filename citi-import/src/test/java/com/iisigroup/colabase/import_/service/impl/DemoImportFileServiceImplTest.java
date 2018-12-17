package com.iisigroup.colabase.import_.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.iisigroup.colabase.import_.dao.DemoImportCustomerDao;
import com.iisigroup.colabase.import_.dao.impl.DemoImportCustomerDaoImpl;
import com.iisigroup.colabase.import_.service.DemoImportFileService;

import static org.junit.Assert.*;

@RunWith(value = MockitoJUnitRunner.class)
public class DemoImportFileServiceImplTest {
    
    // 測試前請設定filePath, fileName, days, date, dateFormat, count

    // 檔案資料夾路徑
    private final String filePath = "D:/TEST/TEST_EXPORT/"; // 自訂
    // 檔案名稱
    private final String fileName = "demo_customer_gen_data.txt"; // 自訂
    // 指定天數
    private final int days = 50; // 自訂
    // 指定天
    private final String date = "20181211"; // 自訂
    // 指定天格式(上面date的格式，例：yyyyMMdd)
    private final String dateFormat = "yyyyMMdd"; // 自訂
    // 檔案筆數
    private final int count = 3; // 自訂

    @Spy
    private DemoImportCustomerDao custDao;
    
    @Spy
    private DemoImportFileService importFileService;

    @Before
    public void setUp() throws Exception {
        custDao = new DemoImportCustomerDaoImpl();
        importFileService = new DemoImportFileServiceImpl(custDao);
    }

    @Test
    public void testCheckTodayYesterday() throws Exception {
        boolean result = importFileService.checkTodayYesterday(filePath, fileName);
        assertEquals(true, result);
    }
    
    @Test
    public void testCheckTime() throws Exception {
        boolean result = importFileService.checkTime(filePath, fileName, days);
        assertEquals(true, result);
    }
    
    @Test
    public void testCheckDate() throws Exception {
        boolean result = importFileService.checkDate(filePath, fileName, date, dateFormat);
        assertEquals(true, result);
    }
    
    @Test
    public void testCountRows() throws Exception {
        int result = importFileService.countRows(filePath, fileName);
        assertEquals(count, result);
    }

}