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

/**
 * <pre>
 * Demo Import File Service Implement
 * </pre>
 * 
 * @since 2018年5月14日
 * @author LilyPeng
 * @version <ul>
 *          <li>2018年5月14日,Lily,new
 *          </ul>
 */
@Service
public class DemoImportFileServiceImpl implements DemoImportFileService {

    public DemoImportFileServiceImpl(DemoImportCustomerDao custDao) {
        this.custDao = custDao;
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DemoImportCustomerDao custDao;

    private Map<String, Object> checkFile(String filePath, String fileName, int days) {
        Map<String, Object> map = new HashMap<String, Object>();
        boolean needRunSP = CheckFileUtil.checkFileModifiedDate(filePath, fileName, days);
        int rows = 0;
        if (needRunSP) {
            rows = CheckFileUtil.countLines(filePath, fileName);
        }
        map.put("needRunSP", needRunSP);
        map.put("countRows", rows);
        return map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.import_.service.DemoImportFileService#importFileProcess(java.lang.String, java.lang.String, int, java.lang.String, java.lang.String)
     */
    public Map<String, Object> importFileProcess(String filePath, String fileName, int days, String importFile, String storedProcedureName) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Object> list = new ArrayList<Object>();
        map.putAll(checkFile(filePath, fileName + ".bak", days));
        if ("true".equals(map.get("needRunSP").toString())) {
            list.addAll(custDao.spImport(importFile, storedProcedureName));
        }
        map.put("spResult", list);
        return map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.import_.service.DemoImportFileService#runSP(java.lang.String, java.lang.String)
     */
    public List<Object> runSP(String importFile, String storedProcedureName) {
        return custDao.spImport(importFile, storedProcedureName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.import_.service.DemoImportFileService#checkTodayYesterday(java.lang.String, java.lang.String)
     */
    public boolean checkTodayYesterday(String filePath, String fileName) {
        fileName += ".bak";
        return CheckFileUtil.checkFileModifiedDate(filePath, fileName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.import_.service.DemoImportFileService#checkTime(java.lang.String, java.lang.String, int)
     */
    public boolean checkTime(String filePath, String fileName, int days) {
        fileName += ".bak";
        return CheckFileUtil.checkFileModifiedDate(filePath, fileName, days);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.import_.service.DemoImportFileService#checkDate(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean checkDate(String filePath, String fileName, String date, String dateFormat) {
        fileName += ".bak";
        return CheckFileUtil.checkFileModifiedDate(filePath, fileName, date, dateFormat);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.import_.service.DemoImportFileService#countRows(java.lang.String, java.lang.String)
     */
    public int countRows(String filePath, String fileName) {
        fileName += ".bak";
        return CheckFileUtil.countLines(filePath, fileName);
    }

}
