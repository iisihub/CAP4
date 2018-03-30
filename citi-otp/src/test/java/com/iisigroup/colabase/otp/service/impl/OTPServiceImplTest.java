package com.iisigroup.colabase.otp.service.impl;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

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

    @Before
    public void setUp() throws Exception {
        otpService = new OTPServiceImpl();
    }

    @Test
    public void testGenAndSendOTP() {
        // 判斷OTP Map是為空值
        Map<String, String> otpMap = otpService.genAndSendOTP("001231321312", otpTimeoutSeconds);
        assertTrue("otpMap is Empty", otpMap.isEmpty());
        // 判斷OTP Map是否有值
        otpMap = otpService.genAndSendOTP(mobilePhone, otpTimeoutSeconds);
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
        Map<String, String> retryOTPMap = otpService.genAndSendOTP("+88612312312", otpTimeoutSeconds);
        assertTrue("retryOTPMap is Empty", retryOTPMap.isEmpty());
        // 判斷RETRY OTP Map是否有值
        retryOTPMap = otpService.resendOTP(mobilePhone, otpTimeoutSeconds, otpMaxRetry, isResendOTP, retryCount);
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
