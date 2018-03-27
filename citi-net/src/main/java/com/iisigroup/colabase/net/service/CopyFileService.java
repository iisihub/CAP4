package com.iisigroup.colabase.net.service;

public interface CopyFileService {
    /**
     * 連線網路磁碟機，Copy File
     * 
     * @param path網路磁碟機路徑
     * @param domain網域
     * @param userName使用者名稱
     * @param exportFilePath搬出路徑(網路磁碟機路徑底下)
     * @param userXwd密碼
     * @param importFilePath搬入路徑
     * @param fileName檔名
     * @throws CapMessageException
     */
    void copyFile(String path, String domain, String userName, String userXwd, String exportFilePath, String importFilePath, String fileName);

}
