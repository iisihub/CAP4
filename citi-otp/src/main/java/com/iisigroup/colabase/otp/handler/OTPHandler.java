/* 
 * OtpHandler.java
 * 
 * Copyright (c) 2009-2013 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.otp.handler;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.otp.service.OTPService;

/**
 * Generate & Send OTP password
 * 
 * @since Mar 13, 2018
 * @author Cathy
 * @version
 *          <ul>
 *          <li>Mar 13, 2018,Cathy,new
 *          </ul>
 */
@Controller("demootphandler")
public class OTPHandler extends MFormHandler {

    @Autowired
    private OTPService otpService;
    private static int SEND_COUNT = 0;
    private static String RETRY_MSG = "若密碼失效請按『重送OTP簡訊動態密碼』重送，最多可重送{0}次，你已重送了{1}次。";
    private static String MAX_RETRY_MSG = "已達可重送次數{0}次限制。";

    /**
     * 
     * @param request
     * @return
     * @throws CapException
     */
    public Result sendOTP(Request request) throws CapException {
        HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession(false);
        AjaxFormResult result = new AjaxFormResult();
        String mobilePhone = request.get("MOBILE_PHONE");// 手機號碼
        int otpTimeoutSeconds = Integer.parseInt(request.get("OTP_TIMOUT_SECONDS", "0"));// OTP TIME OUT 秒數
        boolean isResendOTP = Boolean.parseBoolean(request.get("IS_RESEND_OTP", "false"));// 是否為重送OTP
        if (isResendOTP) {// 重送OTP
            int otpMaxRetry = CapString.isEmpty(request.get("OTP_MAX_RETRY")) ? 0 : Integer.parseInt(request.get("OTP_MAX_RETRY"));// OTP 重送最大次數
            result.set("retryMsg", MessageFormat.format(RETRY_MSG, new Object[] { otpMaxRetry, SEND_COUNT }));
            boolean isMaxRetry = otpService.limitOTPRetryCount(SEND_COUNT, otpMaxRetry);
            if (isMaxRetry) {// 限制重送次數
                String retryMsg = MessageFormat.format(MAX_RETRY_MSG, new Object[] { otpMaxRetry });
                return result.set("retryMsg", retryMsg);
            }
        }
        String otpSmsMsg = otpService.genAndSendOTP(session, mobilePhone, otpTimeoutSeconds);
        if (!CapString.isEmpty(otpSmsMsg)) {
            SEND_COUNT += 1;
            result.set("otpSmsMsg", otpSmsMsg);
        }
        return result;
    }

    /**
     * 
     * @param request
     * @return
     * @throws CapException
     */
    public Result invalidateSession(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession(false);
        if (session != null) {
            session.invalidate();
            SEND_COUNT = 0;
        }
        return result;
    }

    /**
     * 
     * @param request
     * @return
     * @throws CapException
     */
    public Result verifyOTP(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession(false);
        String OTP = (String) session.getAttribute("OTP");
        String userOtp = request.get("USER_OTP");
        if (!CapString.isEmpty(OTP)) {
            boolean isVerify = otpService.verifyOTP(userOtp, OTP);
            result.set("isVerify", isVerify);
        }
        return result;
    }

}