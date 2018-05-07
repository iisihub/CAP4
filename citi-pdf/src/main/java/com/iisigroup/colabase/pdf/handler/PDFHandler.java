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
        String templateName = colabaseDemoPath + "PDF_TEMPLETE.ftl";
        Boolean isDownlownPDF = false;
        // 給PDF值
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("custName", custName);
        dataMap.put("idNo", idNo);
        dataMap.put("mPhone", mPhone);
        dataMap.put("applyDate", CapDate.getCurrentDate("yyyy/MM/dd"));
        dataMap.put("onlineSign", "線上簽名驗證完成");
        // PDF產生結果
        if (!CapString.isEmpty(pdfPath)) {
            try {
                pdfService.processPdf(request, dataMap, templateName, pdfPath, pdfName, isDownlownPDF, pdfPwd);
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
        String templateName = colabaseDemoPath + "PDF_TEMPLETE.ftl";
        Boolean isDownlownPDF = true;
        // 給PDF值
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("custName", custName);
        dataMap.put("idNo", idNo);
        dataMap.put("mPhone", mPhone);
        dataMap.put("applyDate", CapDate.getCurrentDate("yyyy/MM/dd"));
        dataMap.put("onlineSign", "線上簽名驗證完成");
        // PDF產生結果
        try {
            return pdfService.processPdf(request, dataMap, templateName, "", pdfName, isDownlownPDF, pdfPwd);
        } catch (Exception e) {
            e.printStackTrace();
            result.set("pdfReslut", e.getMessage());
        }
        return result;
    }

}