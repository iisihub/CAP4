package com.iisigroup.colabase.net.service;

import com.iisigroup.cap.exception.CapMessageException;


public interface CopyFileService {
    /**
     * 連線網路磁碟機，Copy File
     * 
     * @param path 網路磁碟機路徑
     * @param domain 網域
     * @param userName 使用者名稱
     * @param exportFilePath 搬出路徑(網路磁碟機路徑底下)
     * @param userXwd 密碼
     * @param importFilePath 搬入路徑
     * @param fileName 檔名
     * @throws CapMessageException CapMessageException
     */
    void copyFile(String path, String domain, String userName, String userXwd, String exportFilePath, String importFilePath, String fileName) throws CapMessageException;

}
