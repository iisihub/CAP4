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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

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

    private static final int BUFFER_SIZE = 8192;

    public void zip(File destination, boolean overwrite, String password, File... unzipFiles) throws IOException {

        if (destination.isDirectory()) {
            throw new IOException(destination + " is a directory");
        }
        if (destination.exists()) {
            if (overwrite) {
                // 覆寫檔案
                destination.delete();
            } else {
                // 備份原本檔案
                StringBuilder bckFileName = new StringBuilder();
                String fileName = destination.getName();
                bckFileName.append(fileName.substring(0, fileName.lastIndexOf('.')));
                bckFileName.append('_');
                bckFileName.append(System.currentTimeMillis());
                bckFileName.append(fileName.substring(fileName.lastIndexOf('.')));
                destination.renameTo(new File(destination.getParentFile(), bckFileName.toString()));
            }
        }

        byte[] buffer = new byte[BUFFER_SIZE];
        int length = -1;
        ZipOutputStream out = null;
        BufferedOutputStream buffOutput = null;
        FileOutputStream fileOutput = null;
        FileInputStream fileInput = null;
        BufferedInputStream buffInput = null;
        InputStream inputStream = null;
        try {
            fileOutput = new FileOutputStream(destination);
            buffOutput = new BufferedOutputStream(fileOutput, BUFFER_SIZE);
            out = new ZipOutputStream(buffOutput);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
            if (StringUtils.isNotEmpty(password)) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
                parameters.setPassword(password);
            }

            for (File unzipFile : unzipFiles) {
                parameters.setSourceFileCRC((int) CRCUtil.computeFileCRC(unzipFile.getAbsolutePath()));
                out.putNextEntry(unzipFile, parameters);
                
                fileInput = new FileInputStream(unzipFile);
                buffInput = new BufferedInputStream(fileInput);
                inputStream = new BufferedInputStream(buffInput);
                
                while ((length = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.closeEntry();
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(buffInput);
                IOUtils.closeQuietly(fileInput);
            }
            // output the zip file
            out.finish();
        } catch (ZipException e) {
            throw new CapException(e, getClass());
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(buffInput);
            IOUtils.closeQuietly(fileInput);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(buffOutput);
            IOUtils.closeQuietly(fileOutput);
        }
    }

    public void unzip(File unzipFile, String password, File destination) throws IOException {
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

        ZipInputStream inputStream = null;
        OutputStream out = null;

        try {
            ZipFile zipFile = new ZipFile(unzipFile);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }

            // get the header information for all the files in the ZipFile
            @SuppressWarnings("unchecked")
            List<FileHeader> fileHeaderList = zipFile.getFileHeaders();

            for (FileHeader fileHeader : fileHeaderList) {
                if (fileHeader != null) {
                    File outputFile = new File(destination, fileHeader.getFileName());

                    // Get the InputStream from the ZipFile
                    inputStream = zipFile.getInputStream(fileHeader);
                    // Initialize the output stream
                    out = new BufferedOutputStream(new FileOutputStream(outputFile), BUFFER_SIZE);

                    int length = -1;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((length = inputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, length);
                    }
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(out);

                    // restore file attributes
                    UnzipUtil.applyFileAttributes(fileHeader, outputFile);
                }
            }
        } catch (ZipException e) {
            throw new CapException(e, getClass());
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(inputStream);
        }

    }

    public Boolean isEmptyFolder(Boolean isDeleteEmptyFolder, String... fileList) {

        boolean hasFile = true;

        for (int i = 0; i < fileList.length; i++) {
            // 資料夾只要有一個檔案就判斷為檔案存在
            if (new File(fileList[i]).list() != null && new File(fileList[i]).list().length > 0) {
                hasFile = false;
            } else {
                // 若資料夾內沒檔案且delete參數為true，將會刪除資料夾
                if (isDeleteEmptyFolder) {
                    new File(fileList[i]).delete();
                }
            }
        }
        return hasFile;
    }

    public Boolean isExistsFile(File file) {

        if (!file.exists()) {
            return false;
        }
        return true;
    }

}
