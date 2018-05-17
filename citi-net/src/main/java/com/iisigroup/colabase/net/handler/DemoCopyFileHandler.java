package com.iisigroup.colabase.net.handler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.net.service.CopyFileService;
import com.iisigroup.colabase.net.util.NetUseUtil;

@Controller("democopyfilehandler")
public class DemoCopyFileHandler extends MFormHandler {

    @Autowired
    private CopyFileService copyFileSrv;
    
    private static final String PATH_1 = "path1";
    private static final String DOMAIN_1 = "domain1";
    private static final String USER_NAME_1 = "userName1";
    private static final String USER_XWD_1 = "userXwd1";
    private static final String EXPORT_PATH_1 = "exportFilePath1";
    private static final String IMPORT_PATH_1 = "importFilePath1";
    private static final String FILE_NAME_1 = "fileName1";
    private static final String DRIVE_1 = "drive1";
    private static final String DRIVE_LETTERS_1 = "driveLetters1";
    
    private static final String RESULT_MSG = "result";
    private static final String ERROR_MSG = "error";

    public Result copyFileProcess(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String path = request.get(PATH_1);
        String domain = request.get(DOMAIN_1);
        String userName = request.get(USER_NAME_1);
        String userXwd = request.get(USER_XWD_1);
        String exportFilePath = request.get(EXPORT_PATH_1);
        String importFilePath = request.get(IMPORT_PATH_1);
        String fileName = request.get(FILE_NAME_1);
        try {
            copyFileSrv.copyFile(path, domain, userName, userXwd, exportFilePath, importFilePath, fileName);
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("Copy File Process Error", e);
        }
        return result;
    }

    public Result mappingLocalPath(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String path = request.get(PATH_1);
        try {
            String pathValue = NetUseUtil.mappingLocalPath(path);
            if (!CapString.isEmpty(pathValue)) {
                File diskDrive = new File(pathValue + File.separator);
                if (diskDrive != null && diskDrive.canRead() && diskDrive.canWrite()) {
                    logger.debug("*****網路磁碟機已掛載於：" + pathValue.substring(0, 1) + ":" + File.separator);
                    result.set(RESULT_MSG, "網路磁碟機已掛載於：" + pathValue.substring(0, 1) + ":" + File.separator);
                }
            }
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("mappingLocalPath Error", e);
        }
        return result;
    }

    public Result connectDiskWithDrive(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String path = request.get(PATH_1);
        String domain = request.get(DOMAIN_1);
        String userName = request.get(USER_NAME_1);
        String userXwd = request.get(USER_XWD_1);
        String diskLtr = request.get(DRIVE_1);
        int contNetDiskStat = -1;
        try {
            contNetDiskStat = NetUseUtil.connectNetworkDrive(path, diskLtr, domain, userName, userXwd);
            logger.debug("contNetDiskStat:" + contNetDiskStat);
            if (contNetDiskStat == 0) {
                logger.debug("*****成功連線至網路磁碟機，掛載於：" + diskLtr + ":" + File.separator);
                result.set(RESULT_MSG, "成功連線至網路磁碟機，掛載於：" + diskLtr + ":" + File.separator);
            } else if (contNetDiskStat != -1) {
                result.set(RESULT_MSG, "網路磁碟機連線已執行，請檢查網路磁碟機連線狀況。");
            } else {
                logger.debug("*****網路磁碟機連線失敗");
                result.set(RESULT_MSG, "網路磁碟機連線失敗");
            }
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("connectDiskWithDrive Error", e);
        }
        return result;
    }

    public Result connectDisk(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String path = request.get(PATH_1);
        String domain = request.get(DOMAIN_1);
        String userName = request.get(USER_NAME_1);
        String userXwd = request.get(USER_XWD_1);
        String diskLtrs = request.get(DRIVE_LETTERS_1);
        String diskLtr = "";
        int contNetDiskStat = -1;
        try {
            try {
                diskLtr = NetUseUtil.getFreeDriveLetter(diskLtrs);
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
                diskLtr = NetUseUtil.getFreeDriveLetter(diskLtrs);
            }
            contNetDiskStat = NetUseUtil.connectNetworkDrive(path, diskLtr, domain, userName, userXwd);
            logger.debug("contNetDiskStat:" + contNetDiskStat);
            if (contNetDiskStat == 0) {
                logger.debug("*****成功連線至網路磁碟機，掛載於：" + diskLtr + ":" + File.separator);
                result.set(RESULT_MSG, "成功連線至網路磁碟機，掛載於：" + diskLtr + ":" + File.separator);
            } else if (contNetDiskStat != -1) {
                result.set(RESULT_MSG, "網路磁碟機連線已執行，請檢查網路磁碟機連線狀況。");
            } else {
                logger.debug("*****網路磁碟機連線失敗");
                result.set(RESULT_MSG, "網路磁碟機連線失敗");
            }
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("connectDisk Error", e);
        }
        return result;
    }

    public Result copyFile(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String exportFilePath = request.get(EXPORT_PATH_1);
        String importFilePath = request.get(IMPORT_PATH_1);
        String fileName = request.get(FILE_NAME_1);
        String diskLtr = request.get(DRIVE_1);
        try {
            if (CapString.isEmpty(fileName)) {
                throw new CapMessageException("讀取本機匯出的檔案名稱失敗", getClass());
            }
            File diskDrive = new File(diskLtr + ":" + File.separator);
            File hostFilePath = null;
            if (diskDrive != null && diskDrive.canRead()) {
                hostFilePath = new File(exportFilePath + File.separator + fileName);
                logger.debug("HOSTFILEPATH>>>" + hostFilePath.getAbsolutePath());
            } else {
                throw new CapMessageException("讀取網路磁碟機路徑(netdisk folder)錯誤", getClass());
            }
            if (!hostFilePath.canRead()) {
                throw new CapMessageException("讀取本機匯出的檔案錯誤", getClass());
            }
            logger.debug("*****連線網路磁碟機IMPORT_PATH@" + diskDrive + File.separator + importFilePath + File.separator);
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
                    logger.debug("*****刪除遠端重複名稱檔案::" + diskDrive + File.separator + importFilePath + File.separator + fileName);
                }
                FileUtils.copyFileToDirectory(hostFilePath, importFolder, true);
                // 備份檔案
                File backupFile = new File(exportFilePath + File.separator + fileName + ".bak");
                if (backupFile != null && backupFile.exists()) {
                    FileUtils.forceDelete(backupFile);
                }
                File newBackupFile = new File(exportFilePath + File.separator + fileName + ".bak");
                FileUtils.moveFile(hostFilePath, newBackupFile);
                result.set(RESULT_MSG, "success !");
            } catch (IOException e) {
                logger.debug("*****無法取得搬檔案 IOException");
                e.printStackTrace();
                throw new CapMessageException("複製匯入資料錯誤" + e.getLocalizedMessage(), getClass());
            }
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("Copy File Error", e);
        }
        return result;
    }

    public Result disconnectNetworkPath(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String diskLtr = request.get(DRIVE_1);
        int disContNetDiskStat = -1;
        try {
            disContNetDiskStat = NetUseUtil.disconnectNetworkPath(diskLtr);
            logger.debug("disContNetDiskStat:" + disContNetDiskStat);
            if (disContNetDiskStat == 0) {
                result.set(RESULT_MSG, "成功卸載網路磁碟機：" + diskLtr + ":" + File.separator);
            } else if (disContNetDiskStat != -1) {
                result.set(RESULT_MSG, "您可能未在" + diskLtr + ":" + File.separator + "掛載網路磁碟機");
            }
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("disconnectNetworkPath Error", e);
        }
        return result;
    }

    public Result disconnectAllNetworkPath(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        int disContNetDiskStat = -1;
        try {
            disContNetDiskStat = NetUseUtil.disconnectAllNetworkPath();
            logger.debug("disContNetDiskStat:" + disContNetDiskStat);
            if (disContNetDiskStat == 0) {
                result.set(RESULT_MSG, "成功卸載所有網路磁碟機");
            }
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("disconnectAllNetworkPath Error", e);
        }
        return result;
    }

}