package com.iisigroup.colabase.pdf.service;

import java.util.Map;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.cap.exception.CapException;

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
     * @return
     * @throws CapException
     */
    public Result processPdf(Request request, String pdfPath, String pdfName, ByteArrayDownloadResult pdfContent, Boolean isDownlownPDF, String encryptPassword);

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
     * @return
     */
    public Result processPdf(Request request, Map<String, Object> dataMap, String templateName, String pdfPath, String pdfName, Boolean isDownloadPDF, String encryptPassword);

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
    public Result mergePDFFiles(String[] filesPath, String mergerPDFPath, String mergerPDFName);
}
