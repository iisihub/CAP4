package com.iisigroup.colabase.va.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetUseUtil {
    private static Logger logger = LoggerFactory.getLogger(NetUseUtil.class);

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
                    int index = netPath.toLowerCase().indexOf(path.toLowerCase());
                    if (index >= 0) {
                        localPath = driver + netPath.substring(index + path.length());
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
            command.append(uName).append(" ").append(uXwd);
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

    public static int connectNetworkDrive(String netPath, String Drive, String domain, String uName, String uXwd) {
        netPath = netPath.trim();
        if (netPath.lastIndexOf("\\") == netPath.length() - 1) {
            netPath = netPath.substring(0, netPath.length() - 1);
        }
        String result = "";
        Process process = null;
        BufferedReader bf = null;
        try {
            StringBuffer command = new StringBuffer();
            command.append("net use ").append(Drive).append(": ").append(netPath).append(" /user:");
            if (!"".equals(domain)) {
                command.append(domain).append("\\");
            }
            command.append(uName).append(" ").append(uXwd);
            String[] cmd = new String[] { "cmd", "/C", command.toString() };
            // logger.debug("*******test connectNetDisk *****" + cmd[0]);
            // logger.debug("*******test connectNetDisk *****" + cmd[1]);
            // logger.debug("*******test connectNetDisk *****" + cmd[2]);
            // System.out.println("*******test connectNetDisk *****" + cmd[0]);
            // System.out.println("*******test connectNetDisk *****" + cmd[1]);
            // System.out.println("*******test connectNetDisk *****" + cmd[2]);
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

    // public static int testOSXNetMount(String netPath, String Drive, String domain, String uName, String uXwd) {
    // // /Applications/Utilities
    // netPath = netPath.trim();
    // if (netPath.lastIndexOf("\\") == netPath.length() - 1) {
    // netPath = netPath.substring(0, netPath.length() - 1);
    // }
    // String result = "";
    // Process process = null;
    // BufferedReader bf = null;
    // try {
    // StringBuffer command = new StringBuffer();
    // command.append("mount_smbfs //"+uName+":"+uXwd+"@localhost/testCola /Volumes/RamDisk/temp/test");
    // String[] cmd = new String[] {"mount_smbfs", command.toString() };
    // logger.debug("*******test connectNetDisk *****" + cmd[0]);
    // process = Runtime.getRuntime().exec(cmd);
    // bf = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
    // String line = "";
    // while ((line = bf.readLine()) != null) {
    // result += line + "\n";
    // }
    // bf.close();
    // logger.debug(result);
    // System.out.println(result);
    // } catch (Exception e) {
    // e.printStackTrace();
    // logger.debug(e.getLocalizedMessage(), e.getClass(), e);
    // System.out.println(e);
    // throw new RuntimeException(e.getMessage() + "\n" + result, e);
    // }
    // return process == null ? -1 : process.exitValue();
    // }

    public static String getFreeDriveLetter() {
        // String all = "ZYXWVUTSRQPONMLKJIHGFEDCBA";
        // 2012-02-04 1300 compile log detail，請不要使用Z糟，(1) 使用HOST NAME or (2)使用Y糟。(因為Z糟平時連的是PROD，怕容易搞錯，Y是UAT)
        String all = "YXWVUTSRQPONMLKJIHGFEDCBA";
        File[] roots = File.listRoots();
        for (File root : roots) {
            String s = root.toString().toUpperCase();
            s = s.substring(0, s.indexOf(':'));
            all = all.replace(s, "");
        }
        return String.valueOf(all.charAt(0));
    }
}
