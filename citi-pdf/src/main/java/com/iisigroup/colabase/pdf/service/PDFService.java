package com.iisigroup.colabase.pdf.service;

import java.util.Map;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.cap.exception.CapException;
import com.lowagie.text.pdf.BaseFont;

public interface PDFService {

    public enum PDFType {
        FTL("ftl"),
        HTML("html");
        private String pdfType;

        public String getPdfType() {
            return pdfType;
        }

        private PDFType(String pdfType) {
            this.pdfType = pdfType;
        }
    }

    /**
     * 產生申請書PDF；可則自行處理processPDFContent後將PDF內容放置
     * 
     * @param request
     * @param pdfPath
     *            PDF於路徑；若路徑有值，則產生PDF於路徑下
     * @param pdfName
     *            PDF檔案名稱
     * @param pdfContent
     *            PDF內容
     * @param isDownlownPDF
     *            是否為直接下載PDF
     * @param encryptPassword
     *            PDF加密密碼，不加密則空
     * @param font
     *            PDF字型
     * @return
     * @throws CapException
     */
    public Result processPdf(Request request, String pdfPath, String pdfName, ByteArrayDownloadResult pdfContent, Boolean isDownlownPDF, String encryptPassword, String font);

    /**
     * 產生申請書PDF；若為FTL樣版可一起將PDF欄位值資料dataMap處理
     * 
     * @param request
     * @param dataMap
     *            PDF欄位值資料Map
     * @param templateName
     *            FTL樣版名稱
     * @param pdfPath
     *            PDF於路徑；若路徑有值，則產生PDF於路徑下
     * @param pdfName
     *            PDF檔案名稱
     * @param isDownloadPDF
     *            是否為直接下載PDF
     * @param encryptPassword
     *            PDF加密密碼；不加密則空
     * @param font
     *            PDF字型
     * @return
     */
    public Result processPdf(Request request, Map<String, Object> dataMap, String templateName, String pdfPath, String pdfName, Boolean isDownloadPDF, String encryptPassword, String font);

    /**
     * Process PDF Content
     * 
     * @param request
     * @param dataMap
     *            PDF欄位值資料Map
     * @param templateName
     *            FTL樣版名稱
     * @param pdfType
     *            PDF套入樣板格式
     * @return
     */
    public ByteArrayDownloadResult processPDFContent(Request request, Map<String, Object> dataMap, String templateName, PDFType pdfType);

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
    public boolean mergePDFFiles(String[] filesPath, String mergerPDFPath, String mergerPDFName);

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
     * @throws Exception
     */
    public void addTextWatermark(String inputFilePath, String outputFilePath, String textWatermark) throws Exception;

    /**
     * PDF 加入圖片浮水印
     * 
     * @param inputFilePath
     *            加入浮水印PDF路徑
     * @param outputFilePath
     *            加入浮水印後PDF產出路徑
     * @param imgWatermarkPath
     *            圖片浮水印路徑
     * @throws Exception
     */
    public void addImgWatermark(String inputFilePath, String outputFilePath, String imgWatermarkPath) throws Exception;

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
     * @throws Exception
     */
    public boolean addWatermark(String inputFilePath, String outputFilePath, String textWatermark, String imgWatermarkPath, BaseFont font, float fontSize, Float opacity, int rotationDegree)
            throws Exception;

}
