package com.iisigroup.colabase.net.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * Net Disk Connect Util
 * </pre>
 * 
 * @author L
 * @version
 *           <ul>
 *           <li>2016/10/26, Tim, add disconnect net disk method, add mapping local path method (檢查是否已掛載目標的網路磁碟機)
 *           <li>2018/03/21, Lily, modify localPath in mappingLocalPath
 *           <li>2018/03/26, Lily, add 可依照業務邏輯參數化"可搜尋的磁碟機代號列表" method getFreeDriveLetter(String all)
 *           </ul>
 */
public class NetUseUtil {
    private static Logger logger = LoggerFactory.getLogger(NetUseUtil.class);
    private static String ALL_FREE_DRIVE_LETTERS = "ZYXWVUTSRQPONMLKJIHGFEDCBA";

    /**
     * 檢查是否已掛載目標的網路磁碟機，回傳其本地路徑
     * @param netPath 目標的網路磁碟機路徑
     * @return 本地路徑
     */
    public static String mappingLocalPath(String netPath) {
        String localPath = "";
        String result = "";
        Process process = null;
        BufferedReader bf = null;
        try {
            String command = "net use";
            String[] cmd = new String[] { "cmd", "/C", command };
            logger.debug("NetUseUtil mappingLocalPath(String netPath) = " + cmd);
            process = Runtime.getRuntime().exec(cmd);
            bf = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS950"));
            String line = "";
            String netServer = netPath.substring(0, netPath.indexOf("\\", 2));
            while ((line = bf.readLine()) != null) {
                result += line + "\n";
                // net use 顯示的資料要包含 netServer，且 netPath 要包含 net use 的遠端路徑資料中
                if (line.toLowerCase().indexOf(netServer.toLowerCase()) > 0) {
                    StringTokenizer st = new StringTokenizer(line, " ", false);
                    st.nextToken();
                    String driver = st.nextToken();
                    String path = st.nextToken();
                    logger.debug("net mapping local path " + driver + " & " + path);
                    logger.debug(result);
                    int index = netPath.toLowerCase().indexOf(path.toLowerCase());
                    if (index >= 0) {
//                        localPath = driver + netPath.substring(index + path.length());
                        localPath = driver;
                        logger.debug("net use result >> " +localPath);
                    }
                }
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage() + "\n" + result, e);
        }
        return localPath;
    }

    /**
     * 關閉所有Net Disk連線
     * @return the exit value for the subprocess
     */
    public static int disconnectAllNetworkPath() {
        String result = "";
        Process process = null;
        BufferedReader bf = null;
        try {
            String command = "net use * /delete /y";
            String[] cmd = new String[] { "cmd", "/C", command };
            process = Runtime.getRuntime().exec(cmd);
            bf = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS950"));
            String line = "";
            while ((line = bf.readLine()) != null) {
                result += line + "\n";
            }
            bf.close();
            logger.debug(result);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage() + "\n" + result, e);
        }
        return process == null ? -1 : process.exitValue();
    }
    
    /**
     * 關閉Net Disk連線
     * @param diskLetter 連線磁碟機代號
     * @return the exit value for the subprocess
     */
    public static int disconnectNetworkPath(String diskLetter) {
        String result = "";
        Process process = null;
        BufferedReader bf = null;
        try {
            logger.debug("disconnectNetworkPath diskLetter >> " + diskLetter);
            String command = "net use "+diskLetter+": /delete /y";
            String[] cmd = new String[] { "cmd", "/C", command };
            logger.info("disConnect:" + command.toString());
            process = Runtime.getRuntime().exec(cmd);
            bf = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS950"));
            String line = "";
            while ((line = bf.readLine()) != null) {
                result += line + "\n";
            }
            bf.close();
            logger.debug(result);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage() + "\n" + result, e);
        }
        return process == null ? -1 : process.exitValue();
    }

    /**
     * 連接網路磁碟機，不指定本地磁碟機代號
     * @param netPath 網路路徑
     * @param domain 網域
     * @param uName 使用者名稱
     * @param uXwd 密碼(未加密)
     * @return the exit value for the subprocess
     */
    public static int connectNetworkDrive(String netPath, String domain, String uName, String uXwd) {
        netPath = netPath.trim();
        if (netPath.lastIndexOf("\\") == netPath.length() - 1) {
            netPath = netPath.substring(0, netPath.length() - 1);
        }
        String result = "";
        Process process = null;
        BufferedReader bf = null;
        try {
            StringBuffer command = new StringBuffer();
            command.append("net use ").append(getFreeDriveLetter()).append(": ").append(netPath).append(" /user:");
            if (!"".equals(domain)) {
                command.append(domain).append("\\");
            }
            //2015/8/18,處理會記錄連線磁碟機紀錄,跳出詢問訊息
            command.append(uName).append(" ").append(uXwd).append(" /y");
            logger.info("connectDrive:" + command.toString());
            String[] cmd = new String[] { "cmd", "/C", command.toString() };
            process = Runtime.getRuntime().exec(cmd);
            bf = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS950"));
            String line = "";
            while ((line = bf.readLine()) != null) {
                result += line + "\n";
            }
            bf.close();
            logger.debug(result);
        } catch (Exception e) {
            logger.debug(e.getLocalizedMessage(), e.getLocalizedMessage(), e);
            e.printStackTrace();
            throw new RuntimeException(e.getMessage() + "\n" + result, e);
        }
        return process == null ? -1 : process.exitValue();
    }
    
    /**
     * 連接網路磁碟機，指定本地磁碟機代號
     * @param netPath 網路路徑
     * @param drive 本地磁碟機代號
     * @param domain 網域
     * @param uName 使用者名稱
     * @param uXwd 密碼(未加密)
     * @return the exit value for the subprocess
     */
    public static int connectNetworkDrive(String netPath, String drive, String domain, String uName, String uXwd) {
        netPath = netPath.trim();
        if (netPath.lastIndexOf("\\") == netPath.length() - 1) {
            netPath = netPath.substring(0, netPath.length() - 1);
        }
        String result = "";
        Process process = null;
        BufferedReader bf = null;
        try {
            StringBuffer command = new StringBuffer();
            command.append("net use ").append(drive).append(": ").append(netPath).append(" /user:");
            if (!"".equals(domain)) {
                command.append(domain).append("\\");
            }
            //2015/8/18,處理會記錄連線磁碟機紀錄,跳出詢問訊息
            command.append(uName).append(" ").append(uXwd).append(" /y");
            String[] cmd = new String[] { "cmd", "/C", command.toString() };
//             logger.debug("*******test connectNetDisk *****" + cmd[0]);
//             logger.debug("*******test connectNetDisk *****" + cmd[1]);
//             logger.debug("*******test connectNetDisk *****" + cmd[2]);
//             System.out.println("*******test connectNetDisk *****" + cmd[0]);
//             System.out.println("*******test connectNetDisk *****" + cmd[1]);
//             System.out.println("*******test connectNetDisk *****" + cmd[2]);
            process = Runtime.getRuntime().exec(cmd);
            bf = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS950"));
            String line = "";
            while ((line = bf.readLine()) != null) {
                result += line + "\n";
            }
            bf.close();
            logger.debug(result);
            System.out.println(result);
        } catch (Exception e) {
            logger.debug(e.getLocalizedMessage(), e.getClass(), e);
            System.out.println(e);
            e.printStackTrace();
            throw new RuntimeException(e.getMessage() + "\n" + result, e);
        }
        return process == null ? -1 : process.exitValue();
    }

    /**
     * 取得可使用磁碟機代號，沒有自訂磁碟機代號列表
     * @return 磁碟機代號
     */
    public static String getFreeDriveLetter() {
        String all = ALL_FREE_DRIVE_LETTERS;
        return getFreeDriveLetter(all);
    }
    
    /**
     * 取得可使用磁碟機代號，自訂磁碟機代號列表
     * @param 自訂搜尋代號列表
     * @return 磁碟機代號
     */
    public static String getFreeDriveLetter(String all) {
        if(StringUtils.isEmpty(all)){
            all = ALL_FREE_DRIVE_LETTERS;
        }
        File[] roots = File.listRoots();
        for (File root : roots) {
            String s = root.toString().toUpperCase();
            s = s.substring(0, s.indexOf(':'));
            all = all.replace(s, "");
        }
        return String.valueOf(all.charAt(0));
    }
}