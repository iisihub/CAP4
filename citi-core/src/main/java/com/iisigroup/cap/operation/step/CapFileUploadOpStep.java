/* 
 * CapFileUploadOpStep.java
 * 
 * Copyright (c) 2009-2012 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.operation.step;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.handler.Handler;
import com.iisigroup.cap.model.OpStepContext;
import com.iisigroup.cap.utils.CapMath;

/**
 * <pre>
 * 檔案上傳
 * </pre>
 * 
 * @since 2010/7/23
 * @author iristu
 * @version
 *          <ul>
 *          <li>2010/7/23,iristu,new
 *          <li>2012/2/29,iristu,上傳檔案預設最大size(limitSize)，當接收檔案超出時，則拒絕接收。
 *          <li>2012/9/20,iristu,改用MultipartResolver接收檔用
 *          </ul>
 */
public class CapFileUploadOpStep extends AbstractCustomizeOpStep {

    protected final Logger LOGGER = LoggerFactory.getLogger(CapFileUploadOpStep.class);

    private CommonsMultipartResolver multipartResolver;

    String fileSizeLimitErrorCode;

    public void setMultipartResolver(CommonsMultipartResolver multipartResolver) {
        this.multipartResolver = multipartResolver;
    }

    public void setFileSizeLimitErrorCode(String fileSizeLimitErrorCode) {
        this.fileSizeLimitErrorCode = fileSizeLimitErrorCode;
    }

    protected MultipartHttpServletRequest uploadFile(Request params) {
        if (params.containsKey("limitSize")) {
            multipartResolver.setMaxUploadSize(params.getParamsAsInteger("limitSize"));
        }
        if (params.containsKey("fileEncoding")) {
            multipartResolver.setDefaultEncoding(params.get("fileEncoding"));
        }

        try {
            return multipartResolver.resolveMultipart((HttpServletRequest) params.getServletRequest());
        } catch (MaxUploadSizeExceededException fe) {
            CapMessageException me = new CapMessageException(fileSizeLimitErrorCode, getClass());
            Object[] extra = new Object[] { CapMath.divide(String.valueOf(fe.getMaxUploadSize()), "1048576", 2) };
            me.setMessageKey(fileSizeLimitErrorCode);
            me.setExtraInformation(extra);
            throw me;
        } catch (MultipartException e) {
            throw new CapException(e, getClass());
        }

    }

    @Override
    public OpStepContext execute(OpStepContext ctx, Request params, Handler handler) {
        MultipartHttpServletRequest req = uploadFile(params);
        params.setRequestObject(req);
        return ctx.setGoToStep(NEXT);
    }
}
