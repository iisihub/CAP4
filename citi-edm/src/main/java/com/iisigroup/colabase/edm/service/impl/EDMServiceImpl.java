/* 
 * EDMServiceImpl.java
 * 
 * Copyright (c) 2009-2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.edm.service.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.cap.component.impl.CapSpringMVCRequest;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.edm.model.EdmSetting;
import com.iisigroup.colabase.edm.service.EDMService;
import com.iisigroup.colabase.report.CCBasePageReport;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * EDM Service Implement
 * 
 * @since 2018年4月30日
 * @author Johnson Ho
 * @version
 *          <ul>
 *          <li>2018年4月30日,Johnson Ho,new
 *          </ul>
 */
@Service
public class EDMServiceImpl extends CCBasePageReport implements EDMService {

    private final Logger logRecord = LoggerFactory.getLogger(getClass());

    private static final String DEFAULT_ENCORDING = "UTF-8";

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.edm.service.EDMService#sendEDM(com.iisigroup.colabase.edm.model.EdmSetting)
     */
    @Override
    public void sendEDM(EdmSetting edmSetting) {

        ByteArrayDownloadResult pdfContent = processTemplateEmail(edmSetting.getEdmFtlPath(), edmSetting.getMappingFtlVar());

        String enable = getSysConfig().getProperty("mail.enable", "true");
        logRecord.info("[EDM] mail.enable is : {}", Boolean.valueOf(getSysConfig().getProperty("mail.enable", "true")));

        if (Boolean.valueOf(enable)) {
            String mailAddress = CapString.trimNull(edmSetting.getMailAddress());
            logRecord.info("[EDM] emailAccount is : {}", mailAddress);
            if (!CapString.isEmpty(mailAddress) && pdfContent != null) {
                sendEDM(mailAddress, pdfContent.getByteArray(), edmSetting);
            } else {
                logRecord.error("[EDM] pdfContent is null: {}", (pdfContent == null));
                throw new NullPointerException();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.edm.service.EDMService#sendEDM(java.lang.String, byte[], com.iisigroup.colabase.edm.model.EdmSetting)
     */
    @Override
    public Result sendEDM(String mailAddress, byte[] datas, EdmSetting edmSetting) {
        AjaxFormResult result = new AjaxFormResult();

        try {
            final String FROM_ADDRESS = edmSetting.getFromAddress();
            final String FROM_PERSON = edmSetting.getFromPerson();
            final String EDM_HOST = edmSetting.getEdmHost();
            final String EDM_USR = edmSetting.getEdmUsr();
            final String EDM_PWD = edmSetting.getEdmPwd();
            String edmSubject = edmSetting.getEdmSubject();

            if (CapString.isEmpty(edmSubject)) {
                edmSubject = "Citi Cola Notification";
            }

            Properties props = new Properties();
            props.put("mail.smtp.host", EDM_HOST);

            Session mailSession = null;
            if (!CapString.isEmpty(EDM_USR) && !CapString.isEmpty(EDM_PWD)) {
                /** TEST GMAIL */
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");
                mailSession = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EDM_USR, EDM_PWD);
                    }
                });
            } else {
                mailSession = Session.getDefaultInstance(props, null);
            }
            mailSession.setDebug(Boolean.valueOf(getSysConfig().getProperty("mail.debug", "true")));
            MimeMessage msg = new MimeMessage(mailSession);

            InternetAddress fromAddr = new InternetAddress(FROM_ADDRESS, FROM_PERSON, "BIG5");
            msg.setFrom(fromAddr);
            msg.setSubject(edmSubject, "BIG5");
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(mailAddress, false));
            StringBuilder html = new StringBuilder(new String(datas, DEFAULT_ENCORDING));

            MimeMultipart multipart = new MimeMultipart("related");
            // first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();

            if (html != null) {

                String imagePath = edmSetting.getEdmImageFileFolder();
                messageBodyPart.setContent(html.toString(), "text/html;charset=utf-8");
                // add it
                multipart.addBodyPart(messageBodyPart);
                logRecord.debug("[SendEmailServiceImpl] @ emailing orgStr >>" + html.toString());
                // 處理img src
                String org = html.toString();
                String keyword = "<img src=\"cid:";
                processImage(multipart, org, imagePath, keyword, "\"");

                String keyword2 = "background:url('cid:";
                processImage(multipart, org, imagePath, keyword2, "\'");

                // 處理附加檔案
                if (edmSetting.getEdmAttachedFilePath() != null) {
                    multipart = sendFile(multipart, edmSetting);
                }
            }

            // put everything together
            msg.setContent(multipart);
            msg.setSentDate(new Date());

            Transport.send(msg);
            logRecord.info("[EDM] email send!");
        } catch (MessagingException e) {
            logRecord.debug("MessagingException:" + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            logRecord.debug("UnsupportedEncodingException:" + e.getMessage(), e);
        } catch (RuntimeException e) {
            logRecord.error("RuntimeException:" + e.getMessage(), e);
        } catch (Exception e) {
            logRecord.error("sendEmailNotification:" + e.getMessage(), e);
        } catch (NoSuchMethodError e) {
            logRecord.error("NoSuchMethodError:" + e.getMessage(), e);
        }

        return result;
    }

    /**
     * @param edmFtlPath
     * @param dataMap
     * @return
     */
    private ByteArrayDownloadResult processTemplateEmail(String edmFtlPath, Map<String, Object> dataMap) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                OutputStreamWriter wr = new OutputStreamWriter(out, getSysConfig().getProperty(PageReportParam.DEFAULT_ENCODING.toString(), DEFAULT_ENCORDING));
                Writer writer = new BufferedWriter(wr)) {
            Configuration config = getFmConfg().getConfiguration();
            Template t = config.getTemplate(edmFtlPath);

            Map<String, Object> map = new HashMap<String, Object>();

            if (dataMap != null) {
                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    map.put(entry.getKey(), CapString.trimNull(entry.getValue()));
                }
            }

            if (logRecord.isDebugEnabled()) {
                logRecord.debug("[EDM] Template name: {}, data: {}", edmFtlPath, map);
            }
            t.process(map, writer);

            return new ByteArrayDownloadResult(new CapSpringMVCRequest(), out.toByteArray(), getSysConfig().getProperty("edmEncoding", "text/html"));
        } catch (Exception e) {
            logRecord.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param multipart
     * @param edmSetting
     * @return
     */
    private MimeMultipart sendFile(MimeMultipart multipart, EdmSetting edmSetting) {
        // 處理附加檔案
        File sendFile;
        MimeBodyPart filePart = new MimeBodyPart();
        // send file
        try {
            String filePath = edmSetting.getEdmAttachedFilePath();
            sendFile = new File(filePath);
            logRecord.debug("[SendEmailServiceImpl] @ attachedFile Found >>>>>> " + sendFile);
            if (sendFile.exists()) {
                logRecord.debug("[SendEmailServiceImpl] @ attachedFile has Found");
                // 要顯示的檔名，檔名使用UTF-8編碼
                filePart.setHeader("Content-Type", "application/octet-stream; charset=\"utf-8\"");
                /*
                 * the legal values for "encoding" are "Q" and "B"... The "Q" encoding is recommended for use when most of the characters to be encoded are in the ASCII character set; otherwise, the
                 * "B" encoding should be used.
                 */
                /** WAS 6.1 不支援 */
                filePart.attachFile(sendFile);
                String fileName = sendFile.getName();
                filePart.setFileName(MimeUtility.encodeText(fileName, "UTF-8", "B"));
                multipart.addBodyPart(filePart);
            } else {
                logRecord.debug("[SendEmailServiceImpl] @ attachedFile not Found");
            }
        } catch (Exception e) {
            logRecord.debug("sendEdmFileNotification:" + e.getMessage(), e);
        }

        return multipart;
    }

    /**
     * @param multipart
     * @param mailContent
     * @param imagePath
     * @param tag
     * @param indexMark
     * @throws MessagingException
     */
    private void processImage(MimeMultipart multipart, String mailContent, String imagePath, String tag, String indexMark) throws MessagingException {
        BodyPart messageBodyPart;
        int index = mailContent.indexOf(tag);
        int end = 0;
        logRecord.debug("[SendEmailServiceImpl] @ {} process >>>>>> ", tag);
        Set<String> imageSet = new HashSet<String>();
        while (index >= 0) {
            int indexOfTag = index + tag.length();
            end = mailContent.indexOf(indexMark, indexOfTag);
            String fileName = mailContent.substring(indexOfTag, end);
            if (!imageSet.contains(fileName)) {
                messageBodyPart = new MimeBodyPart();
                File file = new File(imagePath, fileName);
                if (file.exists()) {
                    DataSource fds = new FileDataSource(file);
                    messageBodyPart.setDataHandler(new DataHandler(fds));
                    messageBodyPart.setHeader("Content-ID", "<" + fileName + ">");
                    if (fileName.endsWith(".gif")) { // /** .gif若Content-Type設IMAGE/JPEG，IE瀏覽器看不到*/
                        messageBodyPart.setHeader("Content-Type", "IMAGE/GIF");
                    } else {
                        messageBodyPart.setHeader("Content-Type", "IMAGE/JPEG");// /** WAS 8.5 Mail 在MAC會發生的問題 */
                    }
                    // add image to the multipart
                    multipart.addBodyPart(messageBodyPart);
                } else {
                    logRecord.debug("[SendEmailServiceImpl] @ File Not Found >>>>>> {}", file.getPath());
                }
            }
            imageSet.add(fileName);
            index = mailContent.indexOf(tag, indexOfTag);
        }
    }

}
