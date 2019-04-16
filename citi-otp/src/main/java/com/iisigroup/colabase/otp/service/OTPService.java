package com.iisigroup.colabase.otp.service;

import java.util.Map;

import com.iisigroup.cap.component.Request;
import com.iisigroup.colabase.otp.model.SmsConfig;

/**
 * OTP Service
 * 
 * @since Apr 10, 2019
 * @author Cathy
 * @version
 *          <ul>
 *          <li>Apr 10, 2019,Cathy,new
 *          </ul>
 */
public interface OTPService {

    /**
     * 產生及傳送OTP密碼
     * 
     * @param smsConfig
     *            SMS Config設定
     * @param mobilePhone
     *            手機電話，若手機格式為09開頭，則會去0格式化為+886
     * @param smsMsg
     *            SMS簡訊文字
     * @param otpTimeoutSeconds
     *            OTP Timeout秒數
     * @return 產生與傳送OTP密碼結果
     */
    Map<String, String> genAndSendOTP(SmsConfig smsConfig, String mobilePhone, String smsMsg, int otpTimeoutSeconds);

    /**
     * 重新產生及傳送OTP密碼，若超過限制次數則不重新發送OTP密碼
     * 
     * @param smsConfig
     *            SMS Config設定
     * @param mobilePhone
     *            手機電話，若手機格式為09開頭，則會去0格式化為+886
     * @param smsMsg
     *            SMS簡訊文字
     * @param retryMsg
     *            重送SMS時訊息文字
     * @param maxRetryMsg
     *            最大重送SMS次數時訊息文字
     * @param otpTimeoutSeconds
     *            OTP timeout秒數
     * @param otpMaxRetry
     *            OTP 最多重送次數
     * @param isResendOTP
     *            是否重送OTP
     * @param retryCount
     *            目前已重送次數
     * @return 重新傳送OTP密碼結果
     */
    Map<String, String> resendOTP(SmsConfig smsConfig, String mobilePhone, String smsMsg, String retryMsg, String maxRetryMsg, int otpTimeoutSeconds, int otpMaxRetry, boolean isResendOTP,
            int retryCount);

    /**
     * 產生6碼OTP密碼
     * 
     * @return 回傳6碼OTP密碼
     */
    String generateOTP();

    /**
     * 傳送 SMS
     * 
     * @param smsConfig
     *            SMS Config設定
     * @param mobilePhone
     *            手機電話，若手機格式為09開頭，則會去0格式化為+886
     * @param message
     *            SMS簡訊文字
     * @return SMS Server 回傳的結果，記在 AP log 即可，無論成功失敗都不影響交易。
     */
    String sendOTPbySMS(SmsConfig smsConfig, String mobilePhone, String message);

    /**
     * 限制OTP密碼重送次數
     * 
     * @param sendCount
     *            已傳送密碼次數
     * @param otpMaxRetry
     *            OTP最多重送次數
     * @return 是否已達OTP傳送限制次數
     */
    boolean limitOTPRetryCount(int sendCount, int otpMaxRetry);

    /**
     * 驗證使用者輸入OTP密碼是否與系統相符
     * 
     * @param userOtp
     *            使用者輸入OTP密碼
     * @param otp
     *            系統產生OTP密碼
     * @return OTP密碼是否驗證成功
     */
    boolean verifyOTP(String userOtp, String otp);

    /**
     * Invalidate Session中指定的Key
     * 
     * @param request
     *            Request
     * @param vaildateSessionKey
     *            vaildate session key name
     */
    void invalidateSession(Request request, String vaildateSessionKey);

}
