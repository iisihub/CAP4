package com.iisigroup.colabase.otp.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.otp.service.OTPService;
import com.iisigroup.colabase.va.crypto.HttpsConnectionOpener;

@Service
public class OTPServiceImpl implements OTPService {

    private static final DecimalFormat OTP_DECIMAL_FMT = new DecimalFormat("000000");
    private final Logger logger = LoggerFactory.getLogger(getClass());

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
    // SMS Message
    private static String SMS_MSG = "您的「簡訊動態密碼OTP」為{0}，密碼將於{1}秒後失效。請於網頁輸入密碼完成申請。";

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#sendOTPbySMS(java.lang.String, java.lang.String)
     */
    public String sendOTPbySMS(String mobilePhone, String message) throws CapException {
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
        StringBuffer answer = new StringBuffer();
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
            s.disconnect();
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
            if (recv != null) {
                try {
                    recv.close();
                } catch (IOException e) {
                }
            }
        }

        return answer.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#generateOTP(javax.servlet.http.HttpSession, java.lang.String, int)
     */
    @Override
    public String generateOTP(HttpSession session, String smsMsg, int otpTimeOutSec) {
        Random rnd = new Random();
        String otpMsg = OTP_DECIMAL_FMT.format(rnd.nextInt(999999) + 1);
        session.setAttribute("OTP", otpMsg);
        logger.debug("=========OTP number =========" + otpMsg);
        otpMsg = MessageFormat.format(smsMsg, new Object[] { otpMsg, otpTimeOutSec });
        logger.debug("=========OTP message=========" + otpMsg);
        return otpMsg;
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
     * @see com.iisigroup.colabase.otp.service.OTPService#genAndSendOTP(javax.servlet.http.HttpSession, java.lang.String, int)
     */
    @Override
    public String genAndSendOTP(HttpSession session, String mobilePhone, int otpTimeoutSeconds) {
        String otpSmsMsg = "";
        try {
            otpSmsMsg = generateOTP(session, SMS_MSG, otpTimeoutSeconds);
            if (!CapString.isEmpty(mobilePhone) && mobilePhone.startsWith("09") && !CapString.isEmpty(otpSmsMsg)) {
                sendOTPbySMS(mobilePhone, otpSmsMsg);
                session.setAttribute("hasSendOTP", true);
            }
        } catch (Exception e) {
            logger.warn("Generate & Send OTP password error.", e);
        }
        return otpSmsMsg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.otp.service.OTPService#verifyOTP(java.lang.String, java.lang.String)
     */
    @Override
    public boolean verifyOTP(String userOtp, String otp) {
        boolean isVerofy = false;
        if (otp.equals(userOtp)) {
            isVerofy = true;
        }
        return isVerofy;
    }

}
