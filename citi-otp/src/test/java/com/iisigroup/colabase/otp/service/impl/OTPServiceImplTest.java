package com.iisigroup.colabase.otp.service.impl;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.iisigroup.colabase.otp.model.SmsConfig;

public class OTPServiceImplTest {

    private final String mobilePhone = "09001234567";
    private final String userOtp = "123456";
    private final String wrongUserOtp = "123";
    private final String otp = "123456";
    private final int otpTimeoutSeconds = 100;
    private final int sendCount = 4;
    private final int retryCount = 3;
    private final int otpMaxRetry = 3;

    private final boolean isResendOTP = true;
    private OTPServiceImpl otpService;
    private SmsConfig smsConfig;
    // SMS Msg
    private static final String SMS_MSG = "您的「簡訊動態密碼OTP」為{0}，密碼將於{1}秒後失效。請於網頁輸入密碼完成申請。";
    private static final String RETRY_MSG = "若密碼失效請按『重送OTP簡訊動態密碼』重送，最多可重送{0}次，你已重送了{1}次。";
    private static final String MAX_RETRY_MSG = "已達可重送次數{0}次限制。";

    @Before
    public void setUp() throws Exception {
        String host = "sms-pp.sapmobileservices.com";
        String entry = "/citi/citi_tw_ua97201/citi_tw_ua97201.sms";
        String port = "443";
        String username = "citi_tw_ua97201";
        String password = "PeGdmtkD";
        String encoding = "BIG5";
        String proxyEnable = "true";
        String proxyHost = "sgproxy-app.wlb.apac.nsroot.net";
        String proxyPort = "8080";
        smsConfig = new SmsConfig(host, entry, port, username, password, encoding, proxyEnable, proxyHost, proxyPort);
        otpService = new OTPServiceImpl();
    }

    @Test
    public void testGenAndSendOTP() {
        // 判斷OTP Map是為空值
        Map<String, String> otpMap = otpService.genAndSendOTP(smsConfig, "001231321312", SMS_MSG, otpTimeoutSeconds);
        assertTrue("otpMap is Empty", otpMap.isEmpty());
        // 判斷OTP Map是否有值
        otpMap = otpService.genAndSendOTP(smsConfig, mobilePhone, SMS_MSG, otpTimeoutSeconds);
        assertNotNull("otpMap must be not null", otpMap);
        // 判斷是否有產生OTP密碼
        String otp = otpMap.get(OTPServiceImpl.OTP);
        assertNotNull("OTP must be not null", otp);
        // 判斷是否有產生6碼OTP密碼
        boolean otpSixSize = otp.length() == 6 ? true : false;
        assertTrue(otpSixSize);
        // 判斷是否有傳送OTP
        boolean isSendOTP = Boolean.parseBoolean(otpMap.get(OTPServiceImpl.IS_SEND_OTP));
        assertTrue(isSendOTP);
        // 判斷是否有產生OTP SMS 訊息 OTP_SMS_MSG
        String otpSMSMsg = otpMap.get(OTPServiceImpl.OTP_SMS_MSG);
        assertNotNull("OTP SMS Msg must be not null", otpSMSMsg);
    }

    @Test
    public void testResendOTP() {
        // 判斷RETRY Map是為空值
        Map<String, String> retryOTPMap = otpService.genAndSendOTP(smsConfig, "+88612312312", SMS_MSG, otpTimeoutSeconds);
        assertTrue("retryOTPMap is Empty", retryOTPMap.isEmpty());
        // 判斷RETRY OTP Map是否有值
        retryOTPMap = otpService.resendOTP(smsConfig, mobilePhone, SMS_MSG, RETRY_MSG, MAX_RETRY_MSG, otpTimeoutSeconds, otpMaxRetry, isResendOTP, retryCount);
        assertNotNull("retryOTPMap must be not null", retryOTPMap);
        // 判斷是否有產生OTP密碼
        String otp = retryOTPMap.get(OTPServiceImpl.OTP);
        assertNotNull("Retry OTP must be not null", otp);
        // 判斷是否有傳送OTP
        boolean isSendOTP = Boolean.parseBoolean(retryOTPMap.get(OTPServiceImpl.IS_SEND_OTP));
        assertTrue(isSendOTP);
        // 判斷是否有產生OTP重試訊息
        String otpRetryMsg = retryOTPMap.get(OTPServiceImpl.OTP_RETRY_MSG);
        assertNotNull("Retry OTP Msg must be not null", otpRetryMsg);
        // 判斷是否沒有超過傳送次數
        boolean isMaxRetry = Boolean.parseBoolean(retryOTPMap.get(OTPServiceImpl.IS_MAX_RETRY));
        assertFalse(isMaxRetry);
    }

    @Test
    public void testLimitOTPRetryCount() {
        // 判斷是否有超過傳送次數
        boolean isLimitOTPRetryCount = otpService.limitOTPRetryCount(sendCount, otpMaxRetry);
        assertTrue(isLimitOTPRetryCount);
        // 判斷是否沒有超過傳送次數
        isLimitOTPRetryCount = otpService.limitOTPRetryCount(retryCount, otpMaxRetry);
        assertFalse(isLimitOTPRetryCount);
    }

    @Test
    public void testVerifyOTP() {
        // 判斷是否驗證成功
        boolean isLimitOTPRetryCount = otpService.verifyOTP(userOtp, otp);
        assertTrue(isLimitOTPRetryCount);
        // 判斷是否驗證失敗
        isLimitOTPRetryCount = otpService.verifyOTP(wrongUserOtp, otp);
        assertFalse(isLimitOTPRetryCount);
        // 判斷user沒有輸入是否驗證失敗
        isLimitOTPRetryCount = otpService.verifyOTP("", otp);
        assertFalse(isLimitOTPRetryCount);
        // 判斷user沒有產生OTP輸入是否驗證失敗
        isLimitOTPRetryCount = otpService.verifyOTP(wrongUserOtp, "");
        assertFalse(isLimitOTPRetryCount);
    }

}
