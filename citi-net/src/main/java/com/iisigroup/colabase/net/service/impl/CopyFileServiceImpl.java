package com.iisigroup.colabase.net.service.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iisigroup.colabase.net.service.CopyFileService;
import com.iisigroup.colabase.net.util.NetUseUtil;
import com.iisigroup.cap.annotation.NonTransactional;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.utils.CapString;

@Service
public class CopyFileServiceImpl implements CopyFileService {

    private static Logger logger = LoggerFactory.getLogger(CopyFileServiceImpl.class);
    private static String COLA_FREE_DRIVE_LETTERS = "YXWVUTSRQPONMLKJIHGFEDCBA";
    
    /* (non-Javadoc)
     * @see com.iisigroup.colabase.net.service.CopyFileService#copyFile(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @NonTransactional
    public void copyFile(String path, String domain, String userName, String userXwd, String exportFilePath, String importFilePath, String fileName) {
        
        // TODOed 寫文件，描寫運用在專案要注意的事項(2個)
        
    	// 最早的邏輯是全部先卸載，再掛載需要的磁碟機，然後最後全部卸載，但會發生卸載到正在使用的磁碟機，所以改成會先去找本地是否已掛載相同目的地的磁碟機，最後再卸載此磁碟機
    	// 但若在job A先掛載了磁碟機，job B使用，而在其中一個做完，另一個尚未做完的情況下，disconnect，仍會影響另一個job
    	// 所以改成跟現有邏輯相同的，不先判斷是否有已連接目的地的磁碟機代號(因之前這段沒作用)，自己建自己關
    	// 修復之前的邏輯有不在最後disconnect的情形(若拋exception)，讓job最後都會disconnect，避免一直占用磁碟機代號。
    	// ps.原本也想說改成掛載了就不要卸載，但不確定這樣一直處在掛載狀態有沒有其他議題，所以用不影響原本邏輯的方式做修改。
        String diskLtr = "";
        boolean needConnect = true;
        int contNetDiskStat = -1;
        if (needConnect) {
            try {
                diskLtr = NetUseUtil.getFreeDriveLetter(COLA_FREE_DRIVE_LETTERS);
            } catch (Exception e) {
                logger.debug("GET Free Drive Letter Error", e);
                try {
                    NetUseUtil.disconnectAllNetworkPath();
                    logger.debug("*****已卸載所有網路磁碟機");
                } catch (Exception ex) {
                    logger.debug("Disconnect All Network Path", ex);
                }
            }
            if (CapString.isEmpty(diskLtr)) {
                diskLtr = NetUseUtil.getFreeDriveLetter(COLA_FREE_DRIVE_LETTERS);
            }
            contNetDiskStat = NetUseUtil.connectNetworkDrive(path, diskLtr, domain, userName, userXwd);
            logger.debug("contNetDiskStat:{}", contNetDiskStat);
            if (contNetDiskStat == 0) {
                logger.debug("*****成功連線至網路磁碟機，掛載於：{}:{}", diskLtr, File.separator);
            } else {
                logger.debug("*****網路磁碟機連線失敗");
            }
        }
        if (contNetDiskStat != -1 || !needConnect) {
            logger.debug("*****連線網路磁碟機於@{}:{}", diskLtr, File.separator);
            if (CapString.isEmpty(fileName)) {
            	disconnect(diskLtr);
                throw new CapMessageException("讀取本機匯出的檔案名稱失敗", getClass());
            }
            File diskDrive = new File(diskLtr + ":" + File.separator);
            File hostFilePath = null; // new File(diskDrive + File.separator + hostName);
            if (diskDrive != null && diskDrive.canRead()) {
                hostFilePath = new File(exportFilePath + File.separator + fileName);
                logger.debug("HOSTFILEPATH>>>{}", hostFilePath.getAbsolutePath());
            } else {
            	disconnect(diskLtr);
                throw new CapMessageException("讀取網路磁碟機路徑(netdisk folder)錯誤", getClass());
            }
            if (!hostFilePath.canRead()) {
            	disconnect(diskLtr);
                throw new CapMessageException("讀取本機匯出的檔案錯誤", getClass());
            }
            logger.debug("*****連線網路磁碟機IMPORT_PATH@{}{}{}{}", diskDrive, File.separator, importFilePath, File.separator);
            File importFolder = new File(diskDrive + File.separator + importFilePath + File.separator);
            // 2018/03/22 建立子資料夾
            if (importFolder != null && !importFolder.exists()) {
                // 建立目錄結構
                importFolder.mkdirs();
                logger.debug("*****遠端匯入資料夾不存在，建立匯入資料夾");
            }
            if (importFolder == null || !importFolder.canRead() || !importFolder.canWrite()) {
                // 讀取匯入資料夾(import folder)失敗
                throw new CapMessageException("讀取遠端匯入資料夾(import folder)失敗", getClass());
            }
            try {
                File existFile = new File(diskDrive + File.separator + importFilePath + File.separator + fileName);
                if (existFile != null && existFile.exists()) {
                    FileUtils.forceDelete(existFile);
                    logger.debug("*****刪除遠端重複名稱檔案:{}{}{}{}{}", diskDrive, File.separator, importFilePath, File.separator, fileName);
                }
                FileUtils.copyFileToDirectory(hostFilePath, importFolder, true);

                // 備份檔案
                File backupFile = new File(exportFilePath + File.separator + fileName + ".bak");
                if (backupFile != null && backupFile.exists()) {
                    FileUtils.forceDelete(backupFile);
                }
                File newBackupFile = new File(exportFilePath + File.separator + fileName + ".bak");
                FileUtils.moveFile(hostFilePath, newBackupFile);

            } catch (IOException e) {
                logger.error("*****無法搬檔案 IOException:", e);
                throw new CapMessageException("複製匯入資料錯誤" + e.getLocalizedMessage(), getClass());
            } finally {
            	disconnect(diskLtr);
            }
        }
    }
    
    private void disconnect(String diskLtr){
    	try {
            NetUseUtil.disconnectNetworkPath(diskLtr);
        } catch (Exception e) {
            logger.debug("Disconnect Network Path", e);
        }
    }

}