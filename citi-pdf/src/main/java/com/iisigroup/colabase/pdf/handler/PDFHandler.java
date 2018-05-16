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
import com.iisigroup.cap.report.factory.ItextFontFactory;
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
    @Autowired
    private ItextFontFactory fontFactory;
    private static final String DEFAULT_FONT = "MSJH.TTF";// 微軟正黑體
    private static final String TEST_SIGN_WORDING = "線上簽名驗證完成";
    private static final String DATE_FORMAT = "yyyy/MM/dd";
    private static final String FTL_TEMPLETE_NAME = "PDF_TEMPLETE.ftl";

    /**
     * 產生PDF
     * 
     * @param request
     * @return
     */
    public Result generatePDF(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String pdfPath = request.get("PDF_PATH", "");
        String pdfName = request.get("PDF_NAME", "");
        String pdfPwd = request.get("PDF_PASSWORD", "");
        String custName = request.get("custName", "");
        String idNo = request.get("idNo", "");
        String mPhone = request.get("mPhone", "");
        String colabaseDemoPath = "colabaseDemo" + File.separator;
        String templateName = colabaseDemoPath + FTL_TEMPLETE_NAME;
        Boolean isDownlownPDF = false;
        String font = "";
        // 給PDF值
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("custName", custName);
        dataMap.put("idNo", idNo);
        dataMap.put("mPhone", mPhone);
        dataMap.put("applyDate", CapDate.getCurrentDate(DATE_FORMAT));
        dataMap.put("onlineSign", TEST_SIGN_WORDING);
        // PDF產生結果
        if (!CapString.isEmpty(pdfPath)) {
            try {
                font = fontFactory.getFontPath(DEFAULT_FONT, "");
                pdfService.processPdf(request, dataMap, templateName, pdfPath, pdfName, isDownlownPDF, pdfPwd, font);
                result.set("pdfReslut", "ok");
            } catch (Exception e) {
                e.printStackTrace();
                result.set("pdfReslut", e.getMessage());
            }
        } else {
            result.set("pdfReslut", "No PDF Generate Path.");
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
        String pdfName = request.get("PDF_NAME", "DWN_PDF");
        String pdfPwd = request.get("PDF_PASSWORD", "");
        String custName = request.get("custName", "");
        String idNo = request.get("idNo", "");
        String mPhone = request.get("mPhone", "");
        String colabaseDemoPath = "colabaseDemo" + File.separator;
        String templateName = colabaseDemoPath + FTL_TEMPLETE_NAME;
        Boolean isDownlownPDF = true;
        String font = "";
        // 給PDF值
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("custName", custName);
        dataMap.put("idNo", idNo);
        dataMap.put("mPhone", mPhone);
        dataMap.put("applyDate", CapDate.getCurrentDate(DATE_FORMAT));
        dataMap.put("onlineSign", TEST_SIGN_WORDING);
        // PDF產生結果
        try {
            font = fontFactory.getFontPath(DEFAULT_FONT, "");
            return pdfService.processPdf(request, dataMap, templateName, "", pdfName, isDownlownPDF, pdfPwd, font);
        } catch (Exception e) {
            e.printStackTrace();
            result.set("pdfReslut", e.getMessage());
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
                pdfService.mergePDFFiles(filesPath, genMgPDFPath, genMgPDFName);
                result.set("pdfReslut", "ok");
            } catch (Exception e) {
                e.printStackTrace();
                result.set("pdfReslut", e.getMessage());
            }
        } else {
            result.set("pdfReslut", "No Merge PDF Path.");
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
        int partPDFStartPage = Integer.parseInt(request.get("partPDFStartPage", "0"));
        if (!CapString.isEmpty(partPDFPath)) {
            try {
                pdfService.partitionPdfFile(partPDFPath, partPDFStartPage);
                result.set("pdfReslut", "ok");
            } catch (Exception e) {
                e.printStackTrace();
                result.set("pdfReslut", e.getMessage());
            }
        } else {
            result.set("pdfReslut", "No Partition PDF Path.");
        }
        return result;
    }

}