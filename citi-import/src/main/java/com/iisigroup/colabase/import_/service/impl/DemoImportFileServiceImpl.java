package com.iisigroup.colabase.import_.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iisigroup.colabase.import_.dao.DemoImportCustomerDao;
import com.iisigroup.colabase.import_.service.DemoImportFileService;
import com.iisigroup.colabase.import_.util.CheckFileUtil;

@Service
public class DemoImportFileServiceImpl implements DemoImportFileService {

    public DemoImportFileServiceImpl(DemoImportCustomerDao custDao){
        this.custDao = custDao;
    }
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DemoImportCustomerDao custDao;
    
    private Map<String, Object> checkFile(String filePath, String fileName, int days) {
        Map<String, Object> map = new HashMap<String, Object> ();
        boolean needRunSP = CheckFileUtil.checkFileModifiedDate(filePath, fileName, days);
        int rows = 0;
        if(needRunSP){
            rows = CheckFileUtil.countLines(filePath, fileName);
        }
        map.put("needRunSP", needRunSP);
        map.put("countRows", rows);
        return map;
    }
    
    public Map<String, Object> importFileProcess(String filePath, String fileName, int days, String importFile, String storedProcedureName) {
        Map<String, Object> map = new HashMap<String, Object> ();
        List<Object> list = new ArrayList<Object>();
        map.putAll(checkFile(filePath, fileName +".bak", days));
        if("true".equals(map.get("needRunSP").toString())){
            list.addAll(custDao.spImport(importFile, storedProcedureName));
        }
        map.put("spResult", list);
        return map;
    }
    
    public List<Object> runSP(String importFile, String storedProcedureName) {
        return custDao.spImport(importFile, storedProcedureName);
    }
    
    public boolean checkTime(String filePath, String fileName, int days) {
        fileName += ".bak";
        return CheckFileUtil.checkFileModifiedDate(filePath, fileName, days);
    }
    
    public boolean checkDate(String filePath, String fileName, String date, String dateFormat) {
        fileName += ".bak";
        return CheckFileUtil.checkFileModifiedDate(filePath, fileName, date, dateFormat);
    }
    
    public int countRows(String filePath, String fileName) {
        fileName += ".bak";
        return CheckFileUtil.countLines(filePath, fileName);
    }

}
