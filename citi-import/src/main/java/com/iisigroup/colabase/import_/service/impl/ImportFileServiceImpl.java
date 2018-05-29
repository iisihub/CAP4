package com.iisigroup.colabase.import_.service.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.colabase.import_.dao.ImportCustomerDao;
import com.iisigroup.colabase.import_.service.ImportFileService;

@Service
public class ImportFileServiceImpl implements ImportFileService {

    
    public ImportFileServiceImpl(){

    }

    public ImportFileServiceImpl(ImportCustomerDao custDao){
        this.custDao = custDao;
    }
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ImportCustomerDao custDao;
    
    private static final String fileDateFormat = "yyyyMMdd";

    /**
     * 判斷.bak檔案是否超過指定的天數
     * 
     * @param File importFile
     * @param int days
     * @return 檔案修改日距離今日是否在days天數之內
     */
    private boolean checkFileModifiedDate(String filePath, String fileName, int days) {
        File file = new File(filePath, fileName);
        if (file.exists() && file.canRead()) {
            long modifiedTime = file.lastModified();
            logger.debug("checkFileModifiedDate>>>>modifiedTime:" + modifiedTime);
            logger.debug("checkFileModifiedDate>>>>modifiedTime:" + new Timestamp(modifiedTime));
            
            long nowTime = CapDate.getCurrentTimestamp().getTime();
            logger.debug("checkFileModifiedDate>>>>nowTime:" + nowTime);
            logger.debug("checkFileModifiedDate>>>>nowTime:" + new Timestamp(nowTime));
            long shiftDay = (nowTime - modifiedTime)/CapDate.ONE_DAY_TIME_MILLIS;

            // 2018/4/3 在days天內，return ture
            if ((nowTime > modifiedTime) && shiftDay < days) {
                logger.debug("checkFileModifiedDate>>>>true");
                return true;
            }            
        }else{
            throw new CapMessageException("讀取不存在或無法讀取，無法檢查檔案時間", getClass());
        }
        return false;
    }
    
    /**
     * 判斷是否有某一天的檔案
     * 
     * @param filePath
     * @param fileName
     * @param date
     * @param dateFormat
     * @return
     */
    private boolean checkFileModifiedDate(String filePath, String fileName, String date, String dateFormat) {
        File file = new File(filePath, fileName);
        if (file.exists() && file.canRead()) {
            long modifiedTime = file.lastModified();
            Timestamp tsp = new Timestamp(modifiedTime);
            String fileDate = CapDate.convertTimestampToString(tsp, fileDateFormat);
            logger.debug("checkFileModifiedDate>>>>檢查日期(原格式):" + date);
            logger.debug("checkFileModifiedDate>>>>檔案日期(yyyyMMdd):" + fileDate);
            if(CapDate.validDate(date, dateFormat)){
                String checkDate = CapDate.convertDateTimeFromF1ToF2(date, dateFormat, fileDateFormat);
                logger.debug("checkFileModifiedDate>>>>檢查日期(yyyyMMdd):" + checkDate);
                if(checkDate!=null && checkDate.equals(fileDate)){
                    logger.debug("checkFileModifiedDate>>>>true");
                    return true;
                }
            }
        }else{
            throw new CapMessageException("讀取不存在或無法讀取，無法檢查檔案時間", getClass());
        }
        return false;
    }
    
    /**
     * 讀檔 看有幾行(總共有幾筆資料應該被匯入)
     * 
     * @param File importFile
     * @return 資料數
     */
    private Integer countLines(String filePath, String fileName){
        File file = new File(filePath, fileName);
        Integer lineNumber = 0;
        if(file.exists() && file.canRead()){
            FileReader fr = null;
            LineNumberReader lnr = null;
            try {
                fr = new FileReader(file);
                lnr = new LineNumberReader(fr);
                String line = "";
                while ((line = lnr.readLine()) != null && !"".equals(line)) { // 除了判斷讀取到的行訊息是否為null之外，最好也加上是否為空白的判斷。
                    lineNumber = lnr.getLineNumber();
//                    logger.debug("lineNumber:" + lineNumber + " Msg:" + line);
                }
            } catch (IOException e) {
                logger.error("countLines fail >>>" + e.getMessage(), e);
            } finally {
                if (lnr != null) {
                    try {
                        lnr.close();
                    } catch (IOException e) {
                        logger.error("close LineNumberReader fail >>>" + e.getMessage(), e);
                    }
                }
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (IOException e) {
                        logger.error("close FileReader fail >>>" + e.getMessage(), e);
                    }
                }
            }
        }else{
            throw new CapMessageException("讀取不存在或無法讀取，無法算資料筆數", getClass());
        }
        logger.debug("countLines >>> lineNumber:" + lineNumber);
        return lineNumber;
    }
    
    private Map<String, Object> checkFile(String filePath, String fileName, int days) {
        Map<String, Object> map = new HashMap<String, Object> ();
        boolean needRunSP = checkFileModifiedDate(filePath, fileName, days);
        int rows = 0;
        if(needRunSP){
            rows = countLines(filePath, fileName);
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
        return checkFileModifiedDate(filePath, fileName, days);
    }
    
    public boolean checkDate(String filePath, String fileName, String date, String dateFormat) {
        fileName += ".bak";
        return checkFileModifiedDate(filePath, fileName, date, dateFormat);
    }
    
    public int countRows(String filePath, String fileName) {
        fileName += ".bak";
        return countLines(filePath, fileName);
    }

}
