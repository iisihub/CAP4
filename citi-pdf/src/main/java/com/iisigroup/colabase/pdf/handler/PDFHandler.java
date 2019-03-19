/* 
 * PDFHandler.java
 * 
 * Copyright (c) 2009-2013 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.pdf.handler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.pdf.service.PDFService;

/**
 * Generate PDF
 * 
 * @since May 2, 2018
 * @author Cathy
 * @version
 *          <ul>
 *          <li>May 2, 2018,Cathy,new
 *          </ul>
 */
@Controller("demopdfhandler")
public class PDFHandler extends MFormHandler {

    @Autowired
    private PDFService pdfService;
    private static final String DEFAULT_FONT = "MSJH.TTF";// 微軟正黑體
    private static final String DATE_FORMAT = "yyyy/MM/dd";
    private static final String FTL_TEMPLETE_NAME = "PDF_TEMPLETE.ftl";
    // generatePDF parm
    private static final String PDF_NAME = "PDF_NAME";
    private static final String PDF_PATH = "PDF_PATH";
    private static final String PDF_PASS_WORD = "PDF_PASSWORD";
    private static final String CUST_NAME = "custName";
    private static final String M_PHONE = "mPhone";
    private static final String ID_NO = "idNo";
    private static final String APPLY_DATE = "applyDate";
    private static final String ONLINE_SIGN = "onlineSign";
    private static final String TEST_SIGN_WORDING = "線上簽名驗證完成";
    // Result key id
    private static final String PDF_RESULT = "pdfReslut";

    /**
     * 產生PDF
     * 
     * @param request
     * @return
     */
    public Result generatePDF(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String pdfPath = request.get(PDF_PATH, "");
        String pdfName = request.get(PDF_NAME, "");
        String pdfPwd = request.get(PDF_PASS_WORD, "");
        String custName = request.get(CUST_NAME, "");
        String idNo = request.get(ID_NO, "");
        String mPhone = request.get(M_PHONE, "");
        String colabaseDemoPath = "colabaseDemo" + File.separator;
        String templateName = colabaseDemoPath + FTL_TEMPLETE_NAME;
        // 給PDF值
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(CUST_NAME, custName);
        dataMap.put(ID_NO, idNo);
        dataMap.put(M_PHONE, mPhone);
        dataMap.put(APPLY_DATE, CapDate.getCurrentDate(DATE_FORMAT));
        dataMap.put(ONLINE_SIGN, TEST_SIGN_WORDING);
        // PDF產生結果
        if (!CapString.isEmpty(pdfPath)) {
            try {
                pdfService.processPdfByFtl(dataMap, templateName, pdfPath, pdfName, pdfPwd, DEFAULT_FONT);
                result.set(PDF_RESULT, "ok");
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
                result.set(PDF_RESULT, e.getMessage());
            }
        } else {
            result.set(PDF_RESULT, "No PDF Generate Path.");
        }
        return result;
    }

    /**
     * 下載PDF
     * 
     * @param request
     * @return
     */
    public Result downloadPDF(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String pdfName = request.get(PDF_NAME, "DWN_PDF");
        String pdfPwd = request.get(PDF_PASS_WORD, "");
        String custName = request.get(CUST_NAME, "");
        String idNo = request.get(ID_NO, "");
        String mPhone = request.get(M_PHONE, "");
        String colabaseDemoPath = "colabaseDemo" + File.separator;
        String templateName = colabaseDemoPath + FTL_TEMPLETE_NAME;
        // 給PDF值
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(CUST_NAME, custName);
        dataMap.put(ID_NO, idNo);
        dataMap.put(M_PHONE, mPhone);
        dataMap.put(APPLY_DATE, CapDate.getCurrentDate(DATE_FORMAT));
        dataMap.put(ONLINE_SIGN, TEST_SIGN_WORDING);
        // PDF產生結果
        try {
            byte[] pdfContent = pdfService. processPdfContent(dataMap, templateName);
            return pdfService.downloadPdf(request, pdfContent, pdfName, pdfPwd, DEFAULT_FONT);
            // 用路徑位置下載PDF
            // String pdfPath = "/Users/cathy/Downloads/test13.pdf";
            // return pdfService.downloadPdf(request, pdfPath);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            result.set(PDF_RESULT, e.getMessage());
        }
        return result;
    }

    /**
     * 合併PDF
     * 
     * @param request
     * @return
     */
    public Result mergePDF(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String mgPDFPath1 = request.get("mgPDFPath1", "");
        String mgPDFPath2 = request.get("mgPDFPath2", "");
        String genMgPDFPath = request.get("genMgPDFPath", "");
        String genMgPDFName = request.get("genMgPDFName", "");
        String[] filesPath = { mgPDFPath1, mgPDFPath2 };
        if (!CapString.isEmpty(genMgPDFPath)) {
            try {
                pdfService.mergePdfFiles(filesPath, genMgPDFPath, genMgPDFName);
                result.set(PDF_RESULT, "ok");
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
                result.set(PDF_RESULT, e.getMessage());
            }
        } else {
            result.set(PDF_RESULT, "No Merge PDF Input Path.");
        }
        return result;
    }

    /**
     * 分割PDF
     * 
     * @param request
     * @return
     */
    public Result partitionPDF(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String partPDFPath = request.get("partPDFPath", "");
        String partPDFOutputPath = request.get("partPDFOutputPath", "");
        int partPDFStartPage = Integer.parseInt(request.get("partPDFStartPage", "0"));
        if (!CapString.isEmpty(partPDFPath)) {
            try {
                pdfService.partitionPdfFile(partPDFPath, partPDFOutputPath, partPDFStartPage);
                result.set(PDF_RESULT, "ok");
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
                result.set(PDF_RESULT, e.getMessage());
            }
        } else {
            result.set(PDF_RESULT, "No Partition PDF Input Path.");
        }
        return result;
    }

    /**
     * PDF加入浮水印
     * 
     * @param request
     * @return
     */
    public Result pdfAddWatermark(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String wmPDFInputPath = request.get("wmPDFInputPath", "");
        String wmPDFOutputPath = request.get("wmPDFOutputPath", "");
        String wmNamePDF = request.get("wmNamePDF", "");
        String wmPDFImgPath = request.get("wmPDFImgPath", "");
        if (!CapString.isEmpty(wmPDFInputPath)) {
            try {
                if (!CapString.isEmpty(wmNamePDF)) {
                    pdfService.addTextWatermark(wmPDFInputPath, wmPDFOutputPath, wmNamePDF);
                } else if (!CapString.isEmpty(wmPDFImgPath)) {
                    pdfService.addImgWatermark(wmPDFInputPath, wmPDFOutputPath, wmPDFImgPath);
                }
                result.set(PDF_RESULT, "ok");
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
                result.set(PDF_RESULT, e.getMessage());
            }
        } else {
            result.set(PDF_RESULT, "No PDF Add WaterMark Input Path.");
        }
        return result;
    }

}