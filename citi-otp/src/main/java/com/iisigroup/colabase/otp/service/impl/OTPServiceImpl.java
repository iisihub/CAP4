package com.iisigroup.colabase.otp.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iisigroup.cap.base.CapSystemProperties;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.cap.utils.CapSystemConfig;
import com.iisigroup.colabase.otp.model.SmsConfig;
import com.iisigroup.colabase.otp.service.OTPService;
import com.iisigroup.colabase.otp.va.crypto.HttpsConnectionOpener;

@Service
public class OTPServiceImpl implements OTPService {

    @Autowired
    private CapSystemConfig sysConfig;
    @Autowired
    private CapSystemProperties sysProp;

    private static final DecimalFormat OTP_DECIMAL_FMT = new DecimalFormat("000000");
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // OTP回傳參數
    public static final String OTP = "otp";
    public static final String OTP_SMS_MSG = "otpSmsMsg";
    public static final String OTP_RETRY_MSG = "retryMsg";
    public static final String IS_MAX_RETRY = "isMaxRetry";
    public static final String IS_SEND_OTP = "isSendOTP";

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#genAndSendOTP(com.iisigroup.colabase.otp.model.SmsConfig, java.lang.String, java.lang.String, int)
     */
    @Override
    public Map<String, String> genAndSendOTP(SmsConfig smsConfig, String mobilePhone, String smsMsg, int otpTimeoutSeconds) {
        Map<String, String> otpMap = new HashMap<>();
        try {
            if (!CapString.isEmpty(mobilePhone) && mobilePhone.startsWith("09")) {
                String otp = generateOTP();
                otpMap.put(OTP, otp);
                String otpSmsMsg = MessageFormat.format(smsMsg, otp, otpTimeoutSeconds);
                otpMap.put(OTP_SMS_MSG, otpSmsMsg);
                logger.debug("=========OTP message========={}", otpSmsMsg);
                if (!CapString.isEmpty(otp) && !CapString.isEmpty(otpSmsMsg)) {
                    String msg = sendOTPbySMS(smsConfig, mobilePhone, otpSmsMsg);
                    otpMap.put(IS_SEND_OTP, "true");
                    logger.debug("=========send OTP by SMS========={}", msg);
                }
            }
        } catch (Exception e) {
            logger.error("Generate & Send OTP password error {}", e);
        }
        return otpMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#resendOTP(com.iisigroup.colabase.otp.model.SmsConfig, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int,
     * boolean, int)
     */
    @Override
    public Map<String, String> resendOTP(SmsConfig smsConfig, String mobilePhone, String smsMsg, String retryMsg, String maxRetryMsg, int otpTimeoutSeconds, int otpMaxRetry, boolean isResendOTP,
            int retryCount) {
        Map<String, String> resendOtpMap = new HashMap<>();
        boolean isMaxRetry = false;
        try {
            // 重送OTP
            if (isResendOTP) {
                resendOtpMap.put(OTP_RETRY_MSG, MessageFormat.format(retryMsg, otpMaxRetry, retryCount));
                resendOtpMap.put(IS_MAX_RETRY, String.valueOf(isMaxRetry));
                // 限制重送次數
                isMaxRetry = limitOTPRetryCount(retryCount, otpMaxRetry);
                if (isMaxRetry) {
                    resendOtpMap.put(IS_MAX_RETRY, String.valueOf(isMaxRetry));
                    resendOtpMap.put(OTP_RETRY_MSG, MessageFormat.format(retryMsg, otpMaxRetry));
                    return resendOtpMap;
                }
                resendOtpMap.putAll(genAndSendOTP(smsConfig, mobilePhone, smsMsg, otpTimeoutSeconds));
            }
        } catch (Exception e) {
            logger.error("Resend OTP password error.", e);
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
        long nextInt = rnd.nextInt(999999) + (long) 1;
        String otp = OTP_DECIMAL_FMT.format(nextInt);
        logger.debug("=========OTP number ========= {} ", otp);
        return otp;
    }

    public String getDbConfigOrSysConfigProperty(Object config, String configKey) {
        if (config instanceof CapSystemConfig) {
            return sysConfig.getProperty(configKey);
        } else if (config instanceof CapSystemProperties) {
            return sysProp.get(configKey);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#sendOTPbySMS(com.iisigroup.colabase.otp.model.SmsConfig, java.lang.String, java.lang.String)
     */
    @Override
    public String sendOTPbySMS(SmsConfig smsConfig, String mobilePhone, String message) throws ProtocolException {
        if (!StringUtils.isEmpty(mobilePhone) && mobilePhone.startsWith("09")) {
            mobilePhone = "+886" + mobilePhone.substring(1, mobilePhone.length());
            logger.debug("send SMS mobile phone number: {}", mobilePhone);
        } else if (StringUtils.isEmpty(mobilePhone)) {
            throw new CapException("There is no mobile phone number.", getClass());
        } else if (!mobilePhone.startsWith("+886")) {
            throw new CapException("There is wrong mobile phone number:" + mobilePhone, getClass());
        }
        if (StringUtils.isBlank(message)) {
            throw new CapException("Message is blank.", getClass());
        }
        StringBuilder answer = new StringBuilder();
        String host = smsConfig.getHost();
        String entry = smsConfig.getEntry();
        String port = smsConfig.getPort();
        String username = smsConfig.getUsername();
        String password = smsConfig.getPassword();
        String encoding = smsConfig.getEncoding();
        String proxyEnable = smsConfig.getProxyEnable();
        String proxyHost = smsConfig.getProxyHost();
        String proxyPort = smsConfig.getProxyPort();

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
                logger.warn("proxy doesn't set.{}", getClass());
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
            if (length > 0) {
                logger.debug("length={}", length);
            }
            logger.debug("sending message:\n{}", message);
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
                s.disconnect();
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
     * @see com.iisigroup.colabase.otp.service.OTPService#invalidateSession(com.iisigroup.cap.component.Request, java.lang.String)
     */
    @Override
    public void invalidateSession(Request request, String vaildateSessionKey) {
        HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession(false);
        if (session != null) {
            if (session.getAttribute(vaildateSessionKey) != null) {
                session.removeAttribute(vaildateSessionKey);
            }
            session.invalidate();
        }
    }

}
