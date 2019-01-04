package com.iisigroup.colabase.otp.service;

import java.net.ProtocolException;
import java.util.Map;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.colabase.otp.model.SmsConfig;

public interface OTPService {

    /**
     * 產生及傳送OTP密碼
     * 
     * @param smsConfig
     * @param mobilePhone
     * @param smsMsg
     * @param otpTimeoutSeconds
     * @return
     */
    Map<String, String> genAndSendOTP(SmsConfig smsConfig, String mobilePhone, String smsMsg, int otpTimeoutSeconds);

    /**
     * 重新產生及傳送OTP密碼，若超過限制次數則不重新發送OTP密碼
     * 
     * @param smsConfig
     * @param mobilePhone
     * @param smsMsg
     *            SMS簡訊文字
     * @param retryMsg
     *            重送SMS時訊息文字
     * @param maxRetryMsg
     *            達重送SMS次數時訊息文字
     * @param otpTimeoutSeconds
     *            OTP timeout秒數
     * @param otpMaxRetry
     *            OTP 最多重送次數
     * @param isResendOTP
     *            是否重送OTP
     * @param retryCount
     *            目前已重送次數
     * @return
     */
    Map<String, String> resendOTP(SmsConfig smsConfig, String mobilePhone, String smsMsg, String retryMsg, String maxRetryMsg, int otpTimeoutSeconds, int otpMaxRetry, boolean isResendOTP,
            int retryCount);

    /**
     * 產生6碼OTP密碼
     * 
     * @return
     */
    String generateOTP();

    /**
     * 傳送 SMS。
     * 
     * @param smsConfig
     *            sms config 設定
     * @param mobilePhone
     *            需要去 0 加 +886 , 若為09開頭則會格式化為+886
     * @param message
     *            要傳送的訊息
     * @return SMS Server 回傳的結果，記在 AP log 即可，無論成功失敗都不影響交易。
     * @throws ProtocolException 
     * @throws CapException
     */
    String sendOTPbySMS(SmsConfig smsConfig, String mobilePhone, String message) throws ProtocolException;

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
    void invalidateSession(Request request, String vaildateSessionKey);

}
