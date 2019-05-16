package com.iisigroup.colabase.import_.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Demo Import File Service
 * </pre>
 * 
 * @since 2018年5月14日
 * @author LilyPeng
 * @version <ul>
 *          <li>2018年5月14日,Lily,new
 *          </ul>
 */
public interface DemoImportFileService {

    /**
     * 判斷.bak檔案修改日是否為今日或昨日
     * 
     * @param filePath
     *            檔案路徑
     * @param fileName
     *            檔案名稱
     * @return 檔案修改日是否在今日或昨日
     */
    boolean checkTodayYesterday(String filePath, String fileName);

    /**
     * 判斷.bak檔案是否超過指定的天數
     * 
     * @param filePath
     *            檔案路徑
     * @param fileName
     *            檔案名稱
     * @param days
     *            天數
     * @return 檔案修改日距離今日是否在days天數之內
     */
    boolean checkTime(String filePath, String fileName, int days);

    /**
     * 判斷是否有某一天的檔案
     * 
     * @param filePath
     *            檔案路徑
     * @param fileName
     *            檔案名稱
     * @param date
     *            某一天的日期字串
     * @param dateFormat
     *            日期字串格式
     * @return 是否有某一天的檔案
     */
    boolean checkDate(String filePath, String fileName, String date, String dateFormat);
    
    /**
     * 判斷是否有某一天的檔案
     * 
     * @param filePath
     *            檔案路徑
     * @param fileName
     *            檔案名稱
     * @param date
     *            日期
     * @return 是否有某一天的檔案
     */
    boolean checkDate(String filePath, String fileName, Date date);

    /**
     * 讀檔 看有幾行(總共有幾筆資料應該被匯入)
     * 
     * @param filePath
     *            檔案路徑
     * @param fileName
     *            檔案名稱
     * @return 資料數
     */
    int countRows(String filePath, String fileName);

    /**
     * @param importFile
     *            檔案路徑
     * @param storedProcedureName
     *            預存程序名稱
     * @return resultList
     */
    List runSP(String importFile, String storedProcedureName);

    /**
     * @param filePath
     *            本地檔案所在資料夾
     * @param fileName
     *            檔案名稱
     * @param days
     *            天數
     * @param importFile
     *            網路磁碟機檔案完整路徑
     * @param storedProcedureName
     *            預存程序名稱
     * @return 匯入檔案結果
     */
    Map importFileProcess(String filePath, String fileName, int days, String importFile, String storedProcedureName);
}
