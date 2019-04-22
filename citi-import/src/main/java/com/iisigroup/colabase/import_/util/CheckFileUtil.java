package com.iisigroup.colabase.import_.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.utils.CapDate;

/**
 * <pre>
 * Check File Utility
 * </pre>
 * 
 * @since 2018年5月14日
 * @author LilyPeng
 * @version <ul>
 *          <li>2018年5月14日,Lily,new
 *          </ul>
 */
public class CheckFileUtil {

    private static Logger logger = LoggerFactory.getLogger(CheckFileUtil.class);

    private static final String FILE_DATE_FORMAT = "yyyyMMdd";

    /**
     * 判斷.bak檔案修改日是否為今日或昨日
     * 
     * @param filePath
     *            檔案路徑
     * @param fileName
     *            檔案名稱
     * @return 檔案修改日是否在今日或昨日
     */
    public static boolean checkFileModifiedDate(String filePath, String fileName) {
        File file = new File(filePath, fileName);
        if (file.exists() && file.canRead()) {
            // 2015/9/21,Tim,修改計算單位為天
            long modifiedTime = file.lastModified();
            logger.debug("checkFileModifiedDate>>>>modifiedTime:" + modifiedTime);
            logger.debug("checkFileModifiedDate>>>>modifiedTime:" + new Timestamp(modifiedTime));
            Timestamp tsp = new Timestamp(modifiedTime);
            String modDay = CapDate.convertTimestampToString(tsp, "yyyyMMdd");
            String nowDay = CapDate.getCurrentDate("yyyyMMdd");
            int shiftDay = CapDate.calculateDays(modDay, nowDay);
            // 2017/1/24,修改為搬檔案完，隔天或當天就執行
            if (shiftDay == -1 || shiftDay == 0) {
                return true;
            }
        } else {
            throw new CapMessageException("讀取不存在或無法讀取，無法檢查檔案時間", CheckFileUtil.class);
        }
        return false;
    }

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
    public static boolean checkFileModifiedDate(String filePath, String fileName, int days) {
        File file = new File(filePath, fileName);
        if (file.exists() && file.canRead()) {
            long modifiedTime = file.lastModified();
            logger.debug("checkFileModifiedDate>>>>modifiedTime:" + modifiedTime);
            logger.debug("checkFileModifiedDate>>>>modifiedTime:" + new Timestamp(modifiedTime));

            long nowTime = CapDate.getCurrentTimestamp().getTime();
            logger.debug("checkFileModifiedDate>>>>nowTime:" + nowTime);
            logger.debug("checkFileModifiedDate>>>>nowTime:" + new Timestamp(nowTime));
            long shiftDay = (nowTime - modifiedTime) / CapDate.ONE_DAY_TIME_MILLIS;

            // 2018/4/3 在days天內，return ture
            if ((nowTime > modifiedTime) && shiftDay < days) {
                logger.debug("checkFileModifiedDate>>>>true");
                return true;
            }
        } else {
            throw new CapMessageException("讀取不存在或無法讀取，無法檢查檔案時間", CheckFileUtil.class);
        }
        return false;
    }

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
    public static boolean checkFileModifiedDate(String filePath, String fileName, String date, String dateFormat) {
        File file = new File(filePath, fileName);
        if (file.exists() && file.canRead()) {
            long modifiedTime = file.lastModified();
            Timestamp tsp = new Timestamp(modifiedTime);
            String fileDate = CapDate.convertTimestampToString(tsp, FILE_DATE_FORMAT);
            logger.debug("checkFileModifiedDate>>>>檢查日期(原格式):" + date);
            logger.debug("checkFileModifiedDate>>>>檔案日期(yyyyMMdd):" + fileDate);
            if (CapDate.validDate(date, dateFormat)) {
                String checkDate = CapDate.convertDateTimeFromF1ToF2(date, dateFormat, FILE_DATE_FORMAT);
                logger.debug("checkFileModifiedDate>>>>檢查日期(yyyyMMdd):" + checkDate);
                if (checkDate != null && checkDate.equals(fileDate)) {
                    logger.debug("checkFileModifiedDate>>>>true");
                    return true;
                }
            }
        } else {
            throw new CapMessageException("讀取不存在或無法讀取，無法檢查檔案時間", CheckFileUtil.class);
        }
        return false;
    }

    /**
     * 讀檔 看有幾行(總共有幾筆資料應該被匯入)
     * 
     * @param filePath
     *            檔案路徑
     * @param fileName
     *            檔案名稱
     * @return 資料數
     */
    public static Integer countLines(String filePath, String fileName) {
        File file = new File(filePath, fileName);
        Integer lineNumber = 0;
        if (file.exists() && file.canRead()) {
            try (LineNumberReader lnr = new LineNumberReader(new FileReader(file))) {
                String line = "";
                while ((line = lnr.readLine()) != null && !"".equals(line)) { // 除了判斷讀取到的行訊息是否為null之外，最好也加上是否為空白的判斷。
                    lineNumber = lnr.getLineNumber();
                    // logger.debug("lineNumber:" + lineNumber + " Msg:" + line);
                }
            } catch (IOException e) {
                logger.error("countLines fail >>>" + e.getMessage(), e);
            }
        } else {
            throw new CapMessageException("讀取不存在或無法讀取，無法算資料筆數", CheckFileUtil.class);
        }
        logger.debug("countLines >>> lineNumber:" + lineNumber);
        return lineNumber;
    }

}