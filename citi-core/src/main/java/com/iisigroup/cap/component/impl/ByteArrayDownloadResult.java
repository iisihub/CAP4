/* 
 * ByteArrayDownloadResult.java
 * 
 * Copyright (c) 2009-2011 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.component.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.utils.CapWebUtil;

/**
 * <pre>
 * 資料下載
 * 當outputName有值時，browser會以檔案下載的方式呈現
 * 當outputName無值時，browser則會先以直接開始檔案的方式呈現
 * </pre>
 * 
 * @since 2011/11/15
 * @author iristu
 * @version
 *          <ul>
 *          <li>2011/11/15,iristu,new
 *          <li>2012/2/3,rodeschen,copy from cap
 *          <li>2013/4/15,iristu,修正IE7下載時錯誤
 *          </ul>
 */
@SuppressWarnings("serial")
public class ByteArrayDownloadResult extends FileDownloadResult {

    private byte[] _byteArray = null;
    private String outputName;
    private String contentType;

    public ByteArrayDownloadResult() {
    }

    public ByteArrayDownloadResult(Request request, byte[] byteArray, String contentType, String outputName) {
        this._request = request;
        this._byteArray = byteArray;
        this.contentType = contentType;
        this.outputName = CapWebUtil.encodeFileName(_request, outputName);
    }

    public ByteArrayDownloadResult(Request request, byte[] byteArray, String contentType) {
        this._request = request;
        this._byteArray = byteArray;
        this.contentType = contentType;
    }

    @Override
    public String getLogMessage() {
        if (outputName == null) {
            return contentType + " byteArrayDownload complete!!";
        } else {
            return new StringBuffer("Download file:").append(outputName).toString();
        }
    }

    public byte[] getByteArray() {
        return _byteArray;
    }

    public String getOutputName() {
        return outputName;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public void add(Result result) {
        if (result instanceof ByteArrayDownloadResult) {
            ByteArrayDownloadResult r = (ByteArrayDownloadResult) result;
            this._request = r._request;
            this.contentType = r.contentType;
            this._byteArray = r._byteArray;
            this.outputName = CapWebUtil.encodeFileName(_request, r.outputName);
        }
    }

    @Override
    public void respondResult(ServletResponse response) {
        int length = -1;
        InputStream in = null;
        OutputStream output = null;
        try {
            response.setContentType(getContentType());
            response.setContentLength(_byteArray.length);
            if (getOutputName() != null && response instanceof HttpServletResponse) {
                HttpServletResponse resp = (HttpServletResponse) response;
                HttpServletRequest req = (HttpServletRequest) _request.getServletRequest();
                String userAgent = req.getHeader("USER-AGENT");
                if (StringUtils.contains(userAgent, "MSIE")) {
                    resp.setHeader("Content-Disposition", "attachment; filename=\"" + outputName + "\"");
                } else {
                    resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + outputName);
                }
                resp.setHeader("Cache-Control", "public");
                resp.setHeader("Pragma", "public");
            }
            output = response.getOutputStream();
            // Stream to the requester.
            byte[] bbuf = new byte[1024 * 1024];

            in = new ByteArrayInputStream(_byteArray);
            while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                output.write(bbuf, 0, length);
            }
            output.flush();
        } catch (IOException e) {
            e.getMessage();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(output);
        }
    }
}// ~
