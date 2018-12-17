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

import java.util.Map;

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
import com.iisigroup.cap.utils.CapSystemConfig;
import com.iisigroup.colabase.otp.model.SmsConfig;
import com.iisigroup.colabase.otp.service.OTPService;
import com.iisigroup.colabase.otp.service.impl.OTPServiceImpl;

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
    @Autowired
    private CapSystemConfig sysConfig;

    // 前端送來的參數
    public static final String USER_OTP = "USER_OTP";// USER輸入的OTP
    public static final String MOBILE_PHONE = "MOBILE_PHONE";// 手機號碼
    public static final String OTP_TIMOUT_SECONDS = "OTP_TIMOUT_SECONDS";// OTP TIME OUT 秒數
    public static final String IS_RESEND_OTP = "IS_RESEND_OTP";// OTP TIME OUT 秒數
    public static final String OTP_MAX_RETRY = "OTP_MAX_RETRY";// OTP 重送最大次數
    // 回前端的參數
    private static final String IS_VERIFY = "isVerify";
    // Session 計算OTP傳送次數
    public static final String SESSION_RETRY_COUNT = "retryCount";
    // OTP回傳的訊息
    private static final String RETRY_MSG = "若密碼失效請按『重送OTP簡訊動態密碼』重送，最多可重送{0}次，你已重送了{1}次。";
    private static final String MAX_RETRY_MSG = "已達可重送次數{0}次限制。";
    // SMS Msg
    private static final String SMS_MSG = "您的「簡訊動態密碼OTP」為{0}，密碼將於{1}秒後失效。請於網頁輸入密碼完成申請。";

    /**
     * 發/重送OTP密碼
     * 
     * @param request
     * @return
     * @throws CapException
     */
    public Result sendOTP(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        Map<String, String> otpMap = null;
        String mobilePhone = request.get(MOBILE_PHONE);
        int otpTimeoutSeconds = Integer.parseInt(request.get(OTP_TIMOUT_SECONDS, "0"));
        boolean isResendOTP = Boolean.parseBoolean(request.get(IS_RESEND_OTP, "false"));
        HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession(false);
        int retryCount = 0;
        // 紀錄於Session 開始重送次數
        if (session.getAttribute(SESSION_RETRY_COUNT) == null) {
            session.setAttribute(SESSION_RETRY_COUNT, retryCount);
        } else {
            retryCount = (int) session.getAttribute(SESSION_RETRY_COUNT);
        }
        // SMS Config
        String host = sysConfig.getProperty("sms.host");
        String entry = sysConfig.getProperty("sms.entry");
        String port = sysConfig.getProperty("sms.port");
        String username = sysConfig.getProperty("sms.username");
        String password = sysConfig.getProperty("sms.pass.word");
        String encoding = sysConfig.getProperty("sms.encoding");
        String proxyEnable = sysConfig.getProperty("proxy.enable");
        String proxyHost = sysConfig.getProperty("proxy.host");
        String proxyPort = sysConfig.getProperty(" proxy.port");
        SmsConfig smsConfig = new SmsConfig(host, entry, port, username, password, encoding, proxyEnable, proxyHost, proxyPort);
        // 重送OTP
        if (isResendOTP) {
            int otpMaxRetry = CapString.isEmpty(request.get(OTP_MAX_RETRY)) ? 0 : Integer.parseInt(request.get(OTP_MAX_RETRY));
            otpMap = otpService.resendOTP(smsConfig, mobilePhone, SMS_MSG, RETRY_MSG, MAX_RETRY_MSG, otpMaxRetry, otpMaxRetry, isResendOTP, retryCount);
            String retryMsg = otpMap.get(OTPServiceImpl.OTP_RETRY_MSG);
            String isMaxRetry = otpMap.get(OTPServiceImpl.IS_MAX_RETRY);
            result.set(OTPServiceImpl.OTP_RETRY_MSG, retryMsg);
            if (!CapString.isEmpty(isMaxRetry) && "true".equals(isMaxRetry)) {
                return result;
            }
        } else {
            otpMap = otpService.genAndSendOTP(smsConfig, mobilePhone, SMS_MSG, otpTimeoutSeconds);
        }
        // OTP Msg
        String otp = otpMap.get(OTPServiceImpl.OTP);
        String otpSmsMsg = otpMap.get(OTPServiceImpl.OTP_SMS_MSG);
        String otpRetryMsg = otpMap.get(OTPServiceImpl.OTP_RETRY_MSG);
        if (!CapString.isEmpty(otp)) {
            retryCount += 1;
            session.setAttribute(SESSION_RETRY_COUNT, retryCount);// 紀錄於Session 重送次數
            session.setAttribute(OTPServiceImpl.OTP, otp);// 紀錄於Session Verify OTP 用
        }
        if (!CapString.isEmpty(otpSmsMsg)) {
            result.set(OTPServiceImpl.OTP_RETRY_MSG, otpSmsMsg);
        }
        if (!CapString.isEmpty(otpRetryMsg)) {
            result.set(OTPServiceImpl.OTP_RETRY_MSG, otpRetryMsg);
        }
        return result;
    }

    /**
     * Invalidate Session
     * 
     * @param request
     * @return
     * @throws CapException
     */
    public Result invalidateSession(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        otpService.invalidateSession(request, SESSION_RETRY_COUNT);
        return result;
    }

    /**
     * 驗證OTP密碼
     * 
     * @param request
     * @return
     * @throws CapException
     */
    public Result verifyOTP(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession(false);
        String otp = (String) session.getAttribute(OTPServiceImpl.OTP);
        String userOtp = request.get(USER_OTP);
        if (!CapString.isEmpty(otp)) {
            boolean isVerify = otpService.verifyOTP(userOtp, otp);
            result.set(IS_VERIFY, isVerify);
        }
        return result;
    }

}