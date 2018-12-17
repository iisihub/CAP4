package com.iisigroup.colabase.import_.service;

import java.util.List;
import java.util.Map;

public interface DemoImportFileService {
    
	boolean checkTodayYesterday(String filePath, String fileName);
	
    boolean checkTime(String filePath, String fileName, int days);
    
    boolean checkDate(String filePath, String fileName, String date, String dateFormat);
    
    int countRows(String filePath, String fileName);
    
    List runSP(String importFile, String storedProcedureName);
    
    Map importFileProcess(String filePath, String fileName, int days, String importFile, String storedProcedureName);
}
