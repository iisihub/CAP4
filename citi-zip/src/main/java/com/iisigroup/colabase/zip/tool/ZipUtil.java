package com.iisigroup.colabase.zip.tool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.unzip.UnzipUtil;
import net.lingala.zip4j.util.CRCUtil;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iisigroup.cap.exception.CapException;

/**
 * <pre>
 * 實作ZIP功能
 * </pre>
 * 
 * @since 2018年3月25日
 * @author JohnsonHo
 * @version
 *          <ul>
 *          <li>2018年3月25日,JohnsonHo,new
 *          </ul>
 */
public class ZipUtil {

    private static final Logger logRecord = LoggerFactory.getLogger(ZipUtil.class);
    private static final int BUFFER_SIZE = 8192;

    private ZipUtil() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * 壓縮檔案
     * @param destination 輸出zip的位置
     * @param overwrite 是否覆寫
     * @param password 加密密碼，空值則不加密
     * @param unzipFiles 輸入欲zip的檔案位置
     * @throws IOException
     */
    public static void zip(File destination, boolean overwrite, String password, File... unzipFiles) throws IOException {

        if (destination.isDirectory()) {
            throw new IOException(destination + " is a directory");
        }
        if (destination.exists()) {
            if (overwrite) {
                // 覆寫檔案
                boolean isDeleteSuccess = destination.delete();
                if (!isDeleteSuccess) {
                    logRecord.error("[ZIP] Delete file error!");
                }
            } else {
                // 備份原本檔案
                StringBuilder bckFileName = new StringBuilder();
                String fileName = destination.getName();
                bckFileName.append(fileName.substring(0, fileName.lastIndexOf('.')));
                bckFileName.append('_');
                bckFileName.append(System.currentTimeMillis());
                bckFileName.append(fileName.substring(fileName.lastIndexOf('.')));
                boolean isRenameSuccess = destination.renameTo(new File(destination.getParentFile(), bckFileName.toString()));
                if (!isRenameSuccess) {
                    logRecord.error("[ZIP] Rename file error!");
                }
            }
        }

        byte[] buffer = new byte[BUFFER_SIZE];
        int length = -1;

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
        if (StringUtils.isNotEmpty(password)) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
            parameters.setPassword(password);
        }

        for (File unzipFile : unzipFiles) {
            try (FileOutputStream fileOutput = new FileOutputStream(destination);
                    BufferedOutputStream buffOutput = new BufferedOutputStream(fileOutput, BUFFER_SIZE);
                    ZipOutputStream out = new ZipOutputStream(buffOutput);
                    FileInputStream fileInput = new FileInputStream(unzipFile);
                    BufferedInputStream buffInput = new BufferedInputStream(fileInput);
                    InputStream inputStream = new BufferedInputStream(buffInput);) {

                parameters.setSourceFileCRC((int) CRCUtil.computeFileCRC(unzipFile.getAbsolutePath()));
                out.putNextEntry(unzipFile, parameters);

                while ((length = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.closeEntry();
                out.finish();
            } catch (ZipException e) {
                throw new CapException(e, ZipUtil.class);
            }
        }
    }

    /**
     * 解壓縮檔案
     * @param unzipFile 輸入壓縮檔位置
     * @param password 壓縮檔密碼，無則放空值
     * @param destination 解完壓縮的檔案位置
     * @throws IOException
     * @throws ZipException
     */
    public static void unzip(File unzipFile, String password, File destination) throws IOException, ZipException {
        if (!unzipFile.exists()) {
            throw new IOException(unzipFile + " is not exist.");
        }

        if (destination == null) {
            // 未指定 destination，則解壓縮至當前路徑
            destination = new File(unzipFile.getParentFile(), unzipFile.getName().substring(0, unzipFile.getName().lastIndexOf('.')));
        }

        if (!destination.exists()) {
            destination.mkdirs();
        } else {
            if (!destination.isDirectory()) {
                throw new IOException(destination + " is not a directory.");
            } else if (!(destination.canRead() && destination.canWrite())) {
                throw new IOException(destination + " is read only.");
            }
        }

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(unzipFile);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
        } catch (Exception e) {
            throw new CapException(e, ZipUtil.class);
        }

        // get the header information for all the files in the ZipFile
        @SuppressWarnings("unchecked")
        List<FileHeader> fileHeaderList = zipFile.getFileHeaders();

        for (FileHeader fileHeader : fileHeaderList) {
            if (fileHeader != null) {
                File outputFile = new File(destination, fileHeader.getFileName());

                try (ZipInputStream inputStream = zipFile.getInputStream(fileHeader); OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile), BUFFER_SIZE);) {

                    int length = -1;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((length = inputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, length);
                    }
                    // restore file attributes
                    UnzipUtil.applyFileAttributes(fileHeader, outputFile);

                } catch (ZipException e) {
                    throw new CapException(e, ZipUtil.class);
                }

            }
        }
    }

    /**
     * 是否為空的資料夾，只要有任一個檔案存在即回傳true
     * @param isDeleteEmptyFolder 是否刪除空的資料夾
     * @param fileList 可輸入多個資料夾位置
     * @return
     */
    public static Boolean isEmptyFolder(Boolean isDeleteEmptyFolder, String... fileList) {

        boolean hasFile = true;

        for (int i = 0; i < fileList.length; i++) {
            // 資料夾只要有一個檔案就判斷為檔案存在
            if (new File(fileList[i]).list() != null && new File(fileList[i]).list().length > 0) {
                hasFile = false;
            } else {
                // 若資料夾內沒檔案且delete參數為true，將會刪除資料夾
                if (isDeleteEmptyFolder) {
                    boolean isDeleteSuccess = new File(fileList[i]).delete();
                    if (!isDeleteSuccess) {
                        logRecord.error("[ZIP] Delete empty folder error!");
                    }

                }
            }
        }
        return hasFile;
    }

    /**
     * 查看是否存在該資料夾，不存在的話，可創建該資料夾。
     * @param folder 資料夾位置
     * @param isCreate 是否創建該資料夾
     */
    public static void isExistsFolder(File folder, boolean isCreate) {

        if (!folder.exists()) {
            logRecord.debug("[ZIP] Folder not exists!");
            try {
                if (isCreate) {
                    FileUtils.forceMkdir(folder);
                    logRecord.debug("[ZIP] Folder Create!");
                }
            } catch (IOException e) {
                throw new CapException(e, ZipUtil.class);
            }
        } else {
            logRecord.debug("[ZIP] Folder exists!");
        }
    }

}
