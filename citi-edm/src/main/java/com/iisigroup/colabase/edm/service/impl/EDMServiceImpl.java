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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.edm.report.CCBasePageReport;
import com.iisigroup.colabase.edm.service.EDMService;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * <pre>
 * 1.實作html/xml → ftl
 * 2.實作ftl  → 發送EDM
 * </pre>
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

    static final String DEFAULT_ENCORDING = "UTF-8";

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.edm.service.EDMService#sendEDM(com.iisigroup.cap.component.Request)
     */
    @Override
    public void sendEDM(Request request, String edmFtlPath, Map<String, Object> dataMap) {

        ByteArrayDownloadResult pdfContent = processTemplateEmail(request, edmFtlPath, dataMap);

        String enable = getSysConfig().getProperty("mail.enable", "true");
        logRecord.info("[EDM] mail.enable is : {}", Boolean.valueOf(getSysConfig().getProperty("mail.enable", "true")));

        if (Boolean.valueOf(enable)) {
            String mailAddress = CapString.trimNull(request.get("mailAddress"));
            logRecord.info("[EDM] emailAccount is : {}", mailAddress);
            if (!CapString.isEmpty(mailAddress) && pdfContent != null) {
                sendEDM(mailAddress, pdfContent.getByteArray(), request);
            } else {
                logRecord.error("[EDM] pdfContent is null: {}", (pdfContent == null));
                throw new NullPointerException();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.edm.service.EDMService#sendEDM(java.lang.String, byte[], java.lang.String, com.iisigroup.cap.component.Request)
     */
    @Override
    public Result sendEDM(String mailAddress, byte[] datas, Request request) {
        AjaxFormResult result = new AjaxFormResult();

        try {
            final String FROM_ADDRESS = getSysConfig().getProperty("fromAddress", "citi@imta.citicorp.com");
            final String FROM_PERSON = getSysConfig().getProperty("fromPerson", "花旗（台灣）銀行");
            final String EDM_HOST = getSysConfig().getProperty("edmHost", "smtp.gmail.com");
            final String EDM_USR = getSysConfig().getProperty("edmUsr", "css123456tw@gmail.com");
            final String EDM_PWD = getSysConfig().getProperty("edmPwd", "kvzulwkqdoiprtfb");
            String edmSubject = getSysConfig().getProperty("edmSubject", "花旗(台灣)銀行 圓滿貸線上申請確認通知函");

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

                String imagePath = getSysConfig().getProperty("edmImageFileLocation", "/ftl/colabaseDemo/edmImages/");
                imagePath = getClass().getResource(imagePath).getPath();

                messageBodyPart.setContent(html.toString(), "text/html;charset=utf-8");
                // add it
                multipart.addBodyPart(messageBodyPart);

                // 處理img src
                String org = html.toString();
                String keyword = "<img src=\"cid:";
                int index = org.indexOf(keyword);
                int end = 0;
                while (index >= 0) {
                    end = org.indexOf('\"', index + keyword.length());
                    String fileName = org.substring(index + keyword.length(), end);
                    messageBodyPart = new MimeBodyPart();

                    DataSource fds = new FileDataSource(imagePath + File.separator + fileName);
                    messageBodyPart.setDataHandler(new DataHandler(fds));
                    messageBodyPart.setHeader("Content-ID", "<" + fileName + ">");
                    // add image to the multipart

                    multipart.addBodyPart(messageBodyPart);
                    index = org.indexOf(keyword, index + keyword.length());
                }
                String keyword2 = "background:url('cid:";
                index = org.indexOf(keyword2);

                while (index >= 0) {
                    logRecord.info("[EDM] Index is : {}", index);
                    end = org.indexOf('\"', index + keyword2.length());
                    String fileName = org.substring(index + keyword2.length(), end);
                    messageBodyPart = new MimeBodyPart();
                    DataSource fds = new FileDataSource(imagePath + File.separator + fileName);
                    messageBodyPart.setDataHandler(new DataHandler(fds));
                    messageBodyPart.setHeader("Content-ID", "<" + fileName + ">");
                    // add image to the multipart
                    multipart.addBodyPart(messageBodyPart);

                    index = org.indexOf(keyword2, index + keyword2.length());
                }
                // 處理附加檔案
                File sendFile;
                MimeBodyPart filePart = new MimeBodyPart();
                // send file
                try {
                    sendFile = new File(getSysConfig().getProperty("edmSendFileLocation"));
                    filePart.attachFile(sendFile);
                    multipart.addBodyPart(filePart);
                } catch (Exception e) {
                    logRecord.debug("sendEdmFileNotification:" + e.getMessage(), e);
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

    private ByteArrayDownloadResult processTemplateEmail(Request request, String edmFtlPath, Map<String, Object> dataMap) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                OutputStreamWriter wr = new OutputStreamWriter(out, getSysConfig().getProperty(PageReportParam.defaultEncoding.toString(), DEFAULT_ENCORDING));
                Writer writer = new BufferedWriter(wr);
                FileInputStream is = null;) {
            Configuration config = getFmConfg().getConfiguration();
            Template t = config.getTemplate(edmFtlPath);

            Map<String, Object> map = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                map.put(entry.getKey(), CapString.trimNull(entry.getValue()));
            }

            if (logRecord.isDebugEnabled()) {
                logRecord.debug("[EDM] Template name: {}, data: {}", edmFtlPath, map.toString());
            }
            t.process(map, writer);

            return new ByteArrayDownloadResult(request, out.toByteArray(), getSysConfig().getProperty("edmEncoding", "text/html"));
        } catch (Exception e) {
            logRecord.error(e.getMessage(), e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.edm.service.EDMService#ftlToEDM(com.iisigroup.cap.component.Request)
     */
    @Override
    public void htmlToFtl(Request request, String sourceFileName, String ftlDestination) {

        File sourceFile = new File(getClass().getResource(getSysConfig().getProperty("edmFileLocation", "/ftl/report/")).getPath() + sourceFileName);
        File destinationFile = new File(ftlDestination);

        final StringBuffer buffer = new StringBuffer();
        try {
            String str = FileUtils.readFileToString(sourceFile, "MS950");
            int start;
            int pos = 0;

            while ((start = str.indexOf("${", pos)) != -1) {
                buffer.append(str.substring(pos, start));
                if (str.charAt(start + 1) == '$') {
                    buffer.append("$");
                    pos = start + 2;
                    continue;
                }
                pos = start;
                final int startVariableName = start + 2;
                final int endVariableName = str.indexOf('}', startVariableName);

                if (endVariableName != -1) {
                    String variableName = getVariableName(str.substring(startVariableName, endVariableName));
                    boolean fix = variableName.startsWith("<#");
                    buffer.append(fix ? "" : "${").append(variableName).append(fix ? "" : "}");
                    pos = endVariableName + 1;
                } else {
                    break;
                }
            }
            if (pos < str.length()) {
                buffer.append(str.substring(pos));
            }
            String ftl = setListVariable(buffer.toString());
            FileUtils.writeStringToFile(destinationFile, ftl);
        } catch (IOException e) {
            logRecord.error("htmlToFtlNotification:" + e.getMessage(), e);
        }
    }

    public String setListVariable(String xml) {
        final StringBuffer buffer = new StringBuffer();
        try {
            int start, pos = 0;

            while ((start = xml.indexOf("${", pos)) != -1) {

                int startVariableName = start + 2;
                int endVariableName = xml.indexOf('}', startVariableName);
                String variableName = xml.substring(startVariableName, endVariableName);
                if (variableName.startsWith("<")) {
                    buffer.append(xml.substring(pos, start)).append(variableName);
                    pos = endVariableName + 1;
                } else if (variableName.startsWith("w:")) {
                    int p = variableName.indexOf('<');
                    String tag = "<" + variableName.substring(2, p) + " ";
                    startVariableName = xml.lastIndexOf(tag, start);
                    buffer.append(xml.substring(pos, startVariableName));
                    buffer.append(variableName.substring(p));
                    buffer.append(xml.substring(startVariableName, start));
                    pos = endVariableName + 1;
                } else if (variableName.startsWith("/w:")) {
                    buffer.append(xml.substring(pos, start));
                    int p = variableName.indexOf('<');
                    String tag = "</" + variableName.substring(3, p) + ">";
                    startVariableName = xml.indexOf(tag, endVariableName + 1);
                    buffer.append(xml.substring(endVariableName + 1, startVariableName));
                    buffer.append(tag).append(variableName.substring(p));
                    pos = startVariableName + tag.length();
                } else {
                    buffer.append(xml.substring(pos, endVariableName + 1));
                    pos = endVariableName + 1;
                }
            }
            if (pos < xml.length()) {
                buffer.append(xml.substring(pos));
            }
        } catch (Exception e) {
            logRecord.error("setListVariableNotification:" + e.getMessage(), e);
        }
        return buffer.toString();
    }// ;

    public String getVariableName(String str) {
        String temp = "<span>" + str + "</span>";
        final StringBuffer buf1 = new StringBuffer();

        int s1;
        int p1 = 0;
        while ((s1 = temp.indexOf("<span>", p1)) != -1) {
            p1 = temp.indexOf("</span>", s1 + 6);
            if (p1 == -1) {
                break;
            }
            buf1.append(temp.substring(s1 + 6, p1));
            p1 = p1 + 6;
        }
        String rtn = StringEscapeUtils.unescapeXml(buf1.toString());
        rtn = rtn.replaceAll("\r\n", " ");
        logRecord.debug("variable=" + rtn);
        return rtn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.edm.service.EDMService#ftlToEDM(com.iisigroup.cap.component.Request)
     */
    @Override
    public void xmlToFtl(Request request, String sourceFileName, String ftlDestination) {
        // TODO Auto-generated method stub

    }

}
