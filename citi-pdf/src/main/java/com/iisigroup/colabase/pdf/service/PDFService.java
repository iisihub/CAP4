package com.iisigroup.colabase.pdf.service;

import java.io.IOException;
import java.util.Map;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.cap.exception.CapException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;

public interface PDFService {

    /**
     * 產生PDF；可則自行處理processPDFContent後將PDF內容放置
     * 
     * @param pdfContent
     *            PDF內容
     * @param pdfPath
     *            PDF路徑；有值，則PDF產生於路徑下
     * @param pdfName
     *            PDF檔案名稱 (不用帶.pdf副檔名)
     * @param encryptPassword
     *            PDF加密密碼，不加密則空
     * @param fontName
     *            PDF字型名稱
     * @return
     * @throws CapException
     */
    public Result processPdf(ByteArrayDownloadResult pdfContent, String pdfPath, String pdfName, String encryptPassword, String font);

    /**
     * 產生PDF；若為FTL樣版可一起將PDF欄位值資料dataMap處理
     * 
     * @param dataMap
     *            PDF欄位值資料Map
     * @param ftLTemplateName
     *            FTL樣版名稱
     * @param pdfPath
     *            PDF路徑；有值，則PDF產生於路徑下
     * @param pdfName
     *            PDF檔案名稱 (不用帶.pdf副檔名)
     * @param encryptPassword
     *            PDF加密密碼；不加密則空
     * @param fontName
     *            PDF字型名稱
     * @return
     */
    public Result processPdfByFtl(Map<String, Object> dataMap, String ftLTemplateName, String pdfPath, String pdfName, String encryptPassword, String font);

    /**
     * 下載PDF；可則自行處理processPDFContent後將PDF內容放置
     * 
     * @param request
     *            request傳送前端資訊供下載使用
     * @param pdfContent
     *            PDF內容
     * @param pdfName
     *            PDF檔案名稱 (不用帶.pdf副檔名)
     * @param encryptPassword
     *            PDF加密密碼，不加密則空
     * @param fontName
     *            PDF字型名稱
     * @return
     * @throws CapException
     */
    public Result downloadPdf(Request request, ByteArrayDownloadResult pdfContent, String pdfName, String encryptPassword, String font);

    /**
     * 下載PDF；讀取已產生PDF路徑下載PDF
     * 
     * @param request
     * @param pdfPath
     *            PDF抓取路徑
     * @return
     */
    public Result downloadPdf(Request request, String pdfPath);

    /**
     * Process FTL PDF Content
     * 
     * @param request
     * @param dataMap
     *            PDF欄位值資料Map
     * @param ftLTemplateName
     *            FTL樣版名稱
     * @return
     */
    public ByteArrayDownloadResult processPdfContent(Map<String, Object> dataMap, String ftLTemplateName);

    /**
     * Merge PDF Files
     * 
     * @param filesPath
     *            要合併多個PDF路徑位置
     * @param mergerPDFPath
     *            合併後PDF的路徑位置
     * @param mergerPDFName
     *            合併後PDF名稱
     * @return
     */
    public boolean mergePdfFiles(String[] filesPath, String mergerPDFPath, String mergerPDFName);

    /**
     * 將Pdf檔案分割為多頁
     * 
     * @param filePath
     *            欲分割PDF檔案路徑
     * @param partPDFOutputPath
     *            分割後PDF檔案路徑
     * @param partitionPageNum
     *            分割頁數
     * @return
     */
    public boolean partitionPdfFile(String filePath, String partPDFOutputPath, int partitionPageNum);

    /**
     * PDF 加入文字浮水印
     * 
     * @param inputFilePath
     *            加入浮水印PDF路徑
     * @param outputFilePath
     *            加入浮水印後PDF產出路徑
     * @param textWatermark
     *            文字浮水印
     * @throws IOException
     * @throws DocumentException
     * @throws Exception
     */
    public void addTextWatermark(String inputFilePath, String outputFilePath, String textWatermark) throws DocumentException, IOException;

    /**
     * PDF 加入圖片浮水印
     * 
     * @param inputFilePath
     *            加入浮水印PDF路徑
     * @param outputFilePath
     *            加入浮水印後PDF產出路徑
     * @param imgWatermarkPath
     *            圖片浮水印路徑
     * @throws IOException
     * @throws DocumentException
     * @throws Exception
     */
    public void addImgWatermark(String inputFilePath, String outputFilePath, String imgWatermarkPath) throws DocumentException, IOException;

    /**
     * PDF 加入浮水印
     * 
     * @param inputFilePath
     *            加入浮水印PDF路徑
     * @param outputFilePath
     *            加入浮水印後PDF產出路徑
     * @param textWatermark
     *            浮水印文字
     * @param imgWatermarkPath
     *            圖片浮水印路徑
     * @param font
     *            字形
     * @param fontSize
     *            字形大小
     * @param opacity
     *            透明度
     * @param rotationDegree
     *            浮水印旋轉角度
     * @param textWMRepeatNum
     *            文字浮水印重複次數
     * @throws IOException
     * @throws DocumentException
     * @throws Exception
     */
    public boolean addWatermark(String inputFilePath, String outputFilePath, String textWatermark, String imgWatermarkPath, BaseFont font, float fontSize, Float opacity, int rotationDegree,
            int textWMRepeatNum) throws DocumentException, IOException;

}
