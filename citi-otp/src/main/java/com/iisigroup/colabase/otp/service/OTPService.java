package com.iisigroup.colabase.otp.service;

import javax.servlet.http.HttpSession;

import com.iisigroup.cap.exception.CapException;

public interface OTPService {

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
    String sendOTPbySMS(String mobilePhone, String message) throws CapException;

    /**
     * 
     * @param sendCount
     * @param otpMaxRetry
     * @return
     */
    boolean limitOTPRetryCount(int sendCount, int otpMaxRetry);

    /**
     * 
     * @param session
     * @param smsMsg
     * @param otpTimeOutSec
     * @return
     */
    String generateOTP(HttpSession session, String smsMsg, int otpTimeOutSec);

    /**
     * 
     * @param userOtp
     * @param OTP
     * @return
     */
    boolean verifyOTP(String userOtp, String OTP);

    /**
     * 
     * @param session
     * @param mobilePhone
     * @param otpTimeoutSeconds
     * @return
     */
    String genAndSendOTP(HttpSession session, String mobilePhone, int otpTimeoutSeconds);

}
