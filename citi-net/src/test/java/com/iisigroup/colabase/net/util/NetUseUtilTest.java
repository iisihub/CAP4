package com.iisigroup.colabase.net.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NetUseUtilTest {

    // 測試前請建立共用資料夾，並設定netPath, domain, uName, uXwd

    // 遠端資料夾路徑
    private final String netPath = ""; // 自訂
    // 遠端網域
    private final String domain = ""; // 自訂
    // 遠端使用者帳號
    private final String uName = ""; // 自訂
    // 遠端使用者密碼
    private final String uXwd = ""; // 自訂
    // 冒號
    private final String colon = ":";
    // 本地指定連線磁碟機代號
    private String drive = "";
    // 本地free連線磁碟機代號
    private String freeDrive = "";
    // 自訂連線磁碟機代號列表
    private String driveLetters = "PONMLK";

    @Before
    public void setUp() throws Exception {
        NetUseUtil.disconnectAllNetworkPath();
        drive = NetUseUtil.getFreeDriveLetter();
        NetUseUtil.connectNetworkDrive(netPath, drive, domain, uName, uXwd);
        freeDrive = NetUseUtil.getFreeDriveLetter();
    }

    @Test
    public void testGetFreeDriveLetter() throws Exception {
        String result = NetUseUtil.getFreeDriveLetter();
        assertEquals(freeDrive, result.toUpperCase());
    }

    @Test
    public void testGetFreeCustomDriveLetter() throws Exception {
        String result = NetUseUtil.getFreeDriveLetter(driveLetters);
        assertEquals("P", result.toUpperCase());
    }

    @Test
    public void testMappingLocalPath() throws Exception {
        String result = NetUseUtil.mappingLocalPath(netPath);
        // assertEquals("", result.toUpperCase());
        assertEquals(drive + colon, result.toUpperCase());
    }

    @Test
    public void testConnectNetwork() throws Exception {
        int result = NetUseUtil.connectNetworkDrive(netPath, domain, uName, uXwd);
        // assertTrue(-1 < result);
        assertEquals(0, result);
    }

    @Test
    public void testConnectNetworkDrive() throws Exception {
        int result = NetUseUtil.connectNetworkDrive(netPath, freeDrive, domain, uName, uXwd);
        // assertTrue(-1 < result);
        assertEquals(0, result);
    }

    @Test
    public void testDisConnectNetworkPath() throws Exception {
        int result = NetUseUtil.disconnectNetworkPath(drive);
        // assertTrue(-1 < result);
        assertEquals(0, result);
    }

    @Test
    public void testDisConnectAllNetworkPath() throws Exception {
        int result = NetUseUtil.disconnectAllNetworkPath();
        assertEquals(0, result);
    }

}