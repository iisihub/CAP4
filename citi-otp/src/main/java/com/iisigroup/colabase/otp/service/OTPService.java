package com.iisigroup.colabase.otp.service;

import java.util.Map;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.exception.CapException;

public interface OTPService {

    /**
     * 產生及傳送OTP密碼
     * 
     * @param mobilePhone
     * @param otpTimeoutSeconds
     * @return
     */
    Map<String, String> genAndSendOTP(String mobilePhone, int otpTimeoutSeconds);

    /**
     * 重新產生及傳送OTP密碼，若超過限制次數則不重新發送OTP密碼
     * 
     * @param mobilePhone
     * @param otpTimeoutSeconds
     * @param otpMaxRetry
     * @param isResendOTP
     * @param retryCount
     * @return
     */
    Map<String, String> resendOTP(String mobilePhone, int otpTimeoutSeconds, int otpMaxRetry, boolean isResendOTP, int retryCount);

    /**
     * 產生6碼OTP密碼
     * 
     * @return
     */
    String generateOTP();

    /**
     * 傳送 SMS。
     * 
     * @param mobilePhone
     *            需要去 0 加 +886 , 若為09開頭則會格式化為+886
     * @param message
     *            要傳送的訊息
     * @return SMS Server 回傳的結果，記在 AP log 即可，無論成功失敗都不影響交易。
     * @throws CapException
     */
    String sendOTPbySMS(String mobilePhone, String message);

    /**
     * 限制OTP密碼重送次數
     * 
     * @param sendCount
     * @param otpMaxRetry
     * @return
     */
    boolean limitOTPRetryCount(int sendCount, int otpMaxRetry);

    /**
     * 驗證OTP密碼
     * 
     * @param userOtp
     * @param OTP
     * @return
     */
    boolean verifyOTP(String userOtp, String otp);

    /**
     * Invalidate Session
     * 
     * @param request
     */
    void invalidateSession(Request request);

}
