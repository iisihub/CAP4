package com.iisigroup.colabase.otp.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.otp.service.OTPService;
import com.iisigroup.colabase.otp.va.crypto.HttpsConnectionOpener;

@Service
public class OTPServiceImpl implements OTPService {

    private static final DecimalFormat OTP_DECIMAL_FMT = new DecimalFormat("000000");
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Session 計算OTP傳送次數
    public static final String SESSION_RETRY_COUNT = "retryCount";
    // OTP回傳參數
    public static final String OTP = "otp";
    public static final String OTP_SMS_MSG = "otpSmsMsg";
    public static final String OTP_RETRY_MSG = "retryMsg";
    public static final String IS_MAX_RETRY = "isMaxRetry";
    public static final String IS_SEND_OTP = "isSendOTP";
    // OTP回傳的訊息
    private static final String RETRY_MSG = "若密碼失效請按『重送OTP簡訊動態密碼』重送，最多可重送{0}次，你已重送了{1}次。";
    private static final String MAX_RETRY_MSG = "已達可重送次數{0}次限制。";
    // SMS Msg
    private static final String SMS_MSG = "您的「簡訊動態密碼OTP」為{0}，密碼將於{1}秒後失效。請於網頁輸入密碼完成申請。";
    // SMS Setting
    private static final String SMS_HOST = "sms-pp.sapmobileservices.com";
    private static final String SMS_ENTRY = "/citi/citi_tw_ua97201/citi_tw_ua97201.sms";
    private static final String SMS_PORT = "443";
    private static final String SMS_USERNAME = "citi_tw_ua97201";
    private static final String SMS_PASSWORD = "PeGdmtkD";
    private static final String SMS_ENCODING = "BIG5";
    private static final String PROXY_ENABLE = "true";
    private static final String PROXY_HOST = "sgproxy-app.wlb.apac.nsroot.net";
    private static final String PROXY_PORT = "8080";

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#genAndSendOTP(java.lang.String, int)
     */
    @Override
    public Map<String, String> genAndSendOTP(String mobilePhone, int otpTimeoutSeconds) {
        Map<String, String> otpMap = new HashMap<>();
        try {
            if (!CapString.isEmpty(mobilePhone) && mobilePhone.startsWith("09")) {
                String otp = generateOTP();
                otpMap.put(OTP, otp);
                String otpSmsMsg = MessageFormat.format(SMS_MSG, new Object[] { otp, otpTimeoutSeconds });
                otpMap.put(OTP_SMS_MSG, otpSmsMsg);
                logger.debug("=========OTP message=========" + otpSmsMsg);
                if (!CapString.isEmpty(otp) && !CapString.isEmpty(otpSmsMsg)) {
                    String msg = sendOTPbySMS(mobilePhone, otpSmsMsg);
                    otpMap.put(IS_SEND_OTP, "true");
                    logger.debug("=========send OTP by SMS=========" + msg);
                }
            }
        } catch (Exception e) {
            logger.warn("Generate & Send OTP password error.", e);
        }
        return otpMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#resendOTP(java.lang.String, int, int, boolean, int)
     */
    @Override
    public Map<String, String> resendOTP(String mobilePhone, int otpTimeoutSeconds, int otpMaxRetry, boolean isResendOTP, int retryCount) {
        Map<String, String> resendOtpMap = new HashMap<>();
        boolean isMaxRetry = false;
        try {
            // 重送OTP
            if (isResendOTP) {
                resendOtpMap.put(OTP_RETRY_MSG, MessageFormat.format(RETRY_MSG, new Object[] { otpMaxRetry, retryCount }));
                resendOtpMap.put(IS_MAX_RETRY, String.valueOf(isMaxRetry));
                // 限制重送次數
                isMaxRetry = limitOTPRetryCount(retryCount, otpMaxRetry);
                if (isMaxRetry) {
                    String retryMsg = MessageFormat.format(MAX_RETRY_MSG, new Object[] { otpMaxRetry });
                    resendOtpMap.put(IS_MAX_RETRY, String.valueOf(isMaxRetry));
                    resendOtpMap.put(OTP_RETRY_MSG, retryMsg);
                    return resendOtpMap;
                }
                resendOtpMap.putAll(genAndSendOTP(mobilePhone, otpTimeoutSeconds));
            }
        } catch (Exception e) {
            logger.warn("Resend OTP password error.", e);
        }
        return resendOtpMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#generateOTP()
     */
    @Override
    public String generateOTP() {
        Random rnd = new Random();
        String otp = OTP_DECIMAL_FMT.format(rnd.nextInt(999999) + 1);
        logger.debug("=========OTP number =========" + otp);
        return otp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#sendOTPbySMS(java.lang.String, java.lang.String)
     */
    @Override
    public String sendOTPbySMS(String mobilePhone, String message) {
        if (!StringUtils.isEmpty(mobilePhone) && mobilePhone.startsWith("09")) {
            mobilePhone = "+886" + mobilePhone.substring(1, mobilePhone.length());
            logger.debug("send SMS mobile phone number:" + mobilePhone);
        } else if (StringUtils.isEmpty(mobilePhone)) {
            throw new CapException("There is no mobile phone number.", getClass());
        } else if (!mobilePhone.startsWith("+886")) {
            throw new CapException("There is wrong mobile phone number:" + mobilePhone, getClass());
        }
        if (StringUtils.isBlank(message)) {
            throw new CapException("Message is blank.", getClass());
        }
        StringBuilder answer = new StringBuilder();
        String host = SMS_HOST;
        String entry = SMS_ENTRY;
        String port = SMS_PORT;
        String username = SMS_USERNAME;
        String password = SMS_PASSWORD;
        String encoding = SMS_ENCODING;
        String proxyEnable = PROXY_ENABLE;
        String proxyHost = PROXY_HOST;
        String proxyPort = PROXY_PORT;

        int timeout = 3000;
        BufferedReader recv = null;
        HttpsURLConnection s = null;
        BufferedWriter writer = null;
        if (StringUtils.isBlank(host)) {
            throw new CapException("sms.host is blank.", getClass());
        }
        if (StringUtils.isBlank(port)) {
            throw new CapException("sms.port is blank.", getClass());
        }
        if (StringUtils.isBlank(username)) {
            throw new CapException("sms.username is blank.", getClass());
        }
        if (StringUtils.isBlank(password)) {
            throw new CapException("sms.password is blank.", getClass());
        }
        if (StringUtils.isBlank(entry)) {
            throw new CapException("sms.entry is blank.", getClass());
        }
        if (StringUtils.isBlank(encoding)) {
            encoding = "BIG5";
        }
        if ("true".equalsIgnoreCase(proxyEnable)) {
            if (StringUtils.isBlank(proxyHost) || StringUtils.isBlank(proxyPort)) {
                logger.error("proxy doesn't set.", getClass());
            }
        } else {
            proxyHost = null;
            proxyPort = "-1";
        }

        try {
            String tempMessage = "[MSISDN]\n";
            tempMessage += "List=" + mobilePhone + "\n";
            tempMessage += "[MESSAGE]\nText=";
            message = tempMessage + ("UTF8".equalsIgnoreCase(encoding) ? new String(Base64.encodeBase64(message.getBytes("BIG5"))) : message);
            message += "\n[SETUP]\n";
            message += "DCS=" + encoding + "\n";
            message += "[END]";
            // HTTPS
            s = HttpsConnectionOpener.openConnection("https", host, port, entry, timeout, "true".equalsIgnoreCase(proxyEnable), proxyHost, proxyPort);
            if (s == null) {
                throw new CapException("The httpd connection couldn't be opened ", getClass());
            }
            s.setRequestMethod("POST");
            s.addRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64((username + ":" + password).getBytes())));
            s.addRequestProperty("CONTENT-LENGTH", Integer.toString(message.getBytes().length));
            int count = 0;
            int length = message.length() + count;
            logger.debug("length=" + length);
            logger.debug("sending message:\n" + message);
            writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "BIG5"));
            writer.write(message);
            writer.flush();
            recv = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line = null;
            while ((line = recv.readLine()) != null) {
                answer.append(line).append("\n");
            }
        } catch (Exception e) {
            logger.error("proxy doesn't set.", e);
        } finally {
            if (s != null) {
                try {
                    s.disconnect();
                } catch (Exception e) {
                    logger.error("sendOTPbySMS error", e);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("sendOTPbySMS error", e);
                }
            }
            if (recv != null) {
                try {
                    recv.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("sendOTPbySMS error", e);
                }
            }
        }

        return answer.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#limitOTPRetryCount(int, int)
     */
    @Override
    public boolean limitOTPRetryCount(int sendCount, int otpMaxRetry) {
        boolean isMaxRetry = true;
        if (otpMaxRetry >= sendCount) {
            isMaxRetry = false;
        }
        return isMaxRetry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#verifyOTP(java.lang.String, java.lang.String)
     */
    @Override
    public boolean verifyOTP(String userOtp, String otp) {
        boolean isVerofy = false;
        if (!CapString.isEmpty(userOtp) && !CapString.isEmpty(otp) && otp.equals(userOtp)) {
            isVerofy = true;
        }
        return isVerofy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#invalidateSession(com.iisigroup.cap.component.Request)
     */
    @Override
    public void invalidateSession(Request request) {
        HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession(false);
        if (session != null) {
            if (session.getAttribute(SESSION_RETRY_COUNT) != null) {
                session.removeAttribute(SESSION_RETRY_COUNT);
            }
            session.invalidate();
        }
    }

}
