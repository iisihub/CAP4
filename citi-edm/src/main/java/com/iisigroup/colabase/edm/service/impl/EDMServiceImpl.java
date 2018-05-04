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

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.colabase.edm.report.CCBasePageReport;
import com.iisigroup.colabase.edm.service.EDMService;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapMath;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.cap.utils.CapSystemConfig;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**<pre>
 * TODO Write a short description on the purpose of the program
 * </pre>
 * @since  2018年4月30日
 * @author Johnson Ho
 * @version <ul>
 *           <li>2018年4月30日,Johnson Ho,new
 *          </ul>
 */
@Service
public class EDMServiceImpl extends CCBasePageReport implements EDMService{
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    final String DEFAULT_ENCORDING = "UTF-8";
    final String EDM_TEMPLATE_1 = "report/edm1.ftl";
    
    /* (non-Javadoc)
     * @see com.iisigroup.colabase.edm.service.EDMService#sendEDM(com.iisigroup.cap.component.Request)
     */
    @Override
    public void sendEDM(Request request) {
        HttpServletRequest sreq = (HttpServletRequest) request.getServletRequest();
        HttpSession session = sreq.getSession(false);

        // TODO choose template
        request.put("edm", EDM_TEMPLATE_1);
        request.put("apUrl", "smtp.gmail.com");

        ByteArrayDownloadResult pdfContent = processTemplate_email(request);

        // mail edm to user
//        logger.info("[pcl][mail] mail.enable is " + Boolean.valueOf(getSysConfig().getProperty("mail.enable", "true")));

        //TODO mail enable getSysConfig().getProperty("mail.enable", "true")
        if (Boolean.valueOf(true)) {
            //TODO custmail
            String eamilAccount = CapString.trimNull(request.get("eamilAccount"));
            final String _AL_MAP = "_al_map";
//            Map<String, Object> map = new HashMap<String, Object>();
//            if (request.containsKey(_AL_MAP)) {
//                map = (Map<String, Object>) request.getObject(_AL_MAP);
//                eamilAccount = (String) map.get("custMail");
//            }
            logger.info("[pcl][mail] emailAccount is '" + eamilAccount + "'");
            if (!CapString.isEmpty(eamilAccount)) {

                sendEDM(eamilAccount, pdfContent.getByteArray(), request.get("apUrl"), request);
            }
        }
//        String ATTR_OTP = "OTP";
//        if (request.containsKey(ATTR_OTP) && (session.getAttribute(ATTR_OTP) != null)) {
//            session.removeAttribute(ATTR_OTP);
//        }
        // if(request.containsKey(ATTR_P5_OTP) && (session.getAttribute(ATTR_P5_OTP) != null)){
        // session.removeAttribute(ATTR_P5_OTP);
        // }
    }
    
    /* (non-Javadoc)
     * @see com.iisigroup.colabase.edm.service.EDMService#sendEDM(java.lang.String, byte[], java.lang.String, com.iisigroup.cap.component.Request)
     */
    @Override
    public Result sendEDM(String mailAddress, byte[] datas, String apUrl, Request request) {
        AjaxFormResult result = new AjaxFormResult();

        try {
            //TODO 客製化參數
            final String FROM_ADDRESS = "citi@imta.citicorp.com"; // "eService@mail.booc.com.tw";
            final String FROM_PERSON = "花旗（台灣）銀行"; // "花旗（台灣）銀行";
            final String EDM_HOST = "smtp.gmail.com"; // "imta.citicorp.com";
            final String EDM_USR = "css123456tw@gmail.com"; // "";
            final String EDM_PWD = "kvzulwkqdoiprtfb"; // "";
            String EDM_SUBJECT = "花旗(台灣)銀行 圓滿貸線上申請確認通知函"; // "花旗（台灣）銀行 信用卡申請通知";
            if (CapString.isEmpty(EDM_SUBJECT)) {
                EDM_SUBJECT = "Citi Cola Notification";
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
            msg.setSubject(EDM_SUBJECT, "BIG5");
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(mailAddress, false));
            StringBuilder html = new StringBuilder(new String(datas, "UTF-8"));

            MimeMultipart multipart = new MimeMultipart("related");
            // first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();

            if (html != null) {
                String htmlTxt = html.toString();
                // Replace WEBCONTEXT & CBOL_COLA_HOST
                if (!CapString.isEmpty(apUrl)) {
                    htmlTxt = htmlTxt.replace("{WEBCONTEXT}", apUrl);
                    htmlTxt = htmlTxt.replace("{PCL_CBOL_HOST}", apUrl);
                }

                /* TODO Process Image path */
                String imagePath = getSysConfig().getProperty("emailFileLocation", "images");

                messageBodyPart.setContent(html.toString(), "text/html;charset=utf-8");
                // add it
                multipart.addBodyPart(messageBodyPart);

                // 處理img src
                String org = html.toString();
                String keyword = "<img src=\"cid:";
                int index = org.indexOf(keyword);
                int end = 0;
                while (index >= 0) {
                    System.out.println("Index : " + index);
                    end = org.indexOf("\"", index + keyword.length());
                    String fileName = org.substring(index + keyword.length(), end);
                    messageBodyPart = new MimeBodyPart();
                    // DataSource fds = new URLDataSource(new URL(
                    // "http://localhost:8082/cola/static/images/logo.jpg"));

                    DataSource fds = new FileDataSource(imagePath + File.separator + fileName);
                    messageBodyPart.setDataHandler(new DataHandler(fds));
                    messageBodyPart.setHeader("Content-ID", "<" + fileName + ">");
                    // add image to the multipart
                    
                    multipart.addBodyPart(messageBodyPart);
                    index = org.indexOf(keyword, index + keyword.length());
                }
                String keyword2 = "background:url('cid:";
                index = org.indexOf(keyword2);
                end = 0;
                while (index >= 0) {
                    System.out.println("Index : " + index);
                    end = org.indexOf("\"", index + keyword2.length());
                    String fileName = org.substring(index + keyword2.length(), end);
                    messageBodyPart = new MimeBodyPart();
                    // DataSource fds = new URLDataSource(new URL(
                    // "http://localhost:8082/cola/static/images/logo.jpg"));
                    DataSource fds = new FileDataSource(imagePath + File.separator + fileName);
                    messageBodyPart.setDataHandler(new DataHandler(fds));
                    messageBodyPart.setHeader("Content-ID", "<" + fileName + ">");
                    // add image to the multipart
                    multipart.addBodyPart(messageBodyPart);

                    index = org.indexOf(keyword2, index + keyword2.length());
                }
                //處理附加檔案
                HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession();
                File sendFile;
                MimeBodyPart filePart = new MimeBodyPart();
                if (true) {
                    //TODO send file
                    sendFile = new File("C:/Users/KaiYu/Desktop/EI follow-up.txt");
                    filePart.attachFile(sendFile);
                    multipart.addBodyPart(filePart);
                } 
//                else {
//                  //PDF加密
//                    ByteArrayOutputStream buffOutputStream = null;
//                    String idNO = (String) session.getAttribute(CCConstants.SESSION_CUSTID);
//                    byte[] userPass = idNO.getBytes();
//                    byte[] ownerPass = null;
//
//                    PdfReader reader = new PdfReader(String.valueOf(session.getAttribute(CCConstants.TEMP_PDF_PATH)));
//                    PdfReader.unethicalreading = true;
//                    PdfStamper stamper;
//                    buffOutputStream = new ByteArrayOutputStream();
//
//                    try {
//                        /*
//                         * Reader Temp-PDF to set encryption.
//                         */
//                        stamper = new PdfStamper(reader, buffOutputStream);
//                        stamper.setEncryption(userPass, ownerPass, PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
//                        stamper.close();
//                        reader.close();
//                    } catch (DocumentException e) {
//                        // TODO Auto-generated catch block
//                        logger.debug("==== Download PDF is not success ====");
//                        e.printStackTrace();
//                    }
//                    byte[] bytes = buffOutputStream.toByteArray();
//                    DataSource ds = new ByteArrayDataSource(bytes, "application/pdf");
//                    filePart.setDataHandler(new DataHandler(ds));
//                    //TODO file name
//                    filePart.setFileName(MimeUtility.encodeText("圓滿貸申請書暨約定書.pdf", "UTF-8", "B"));
//                    multipart.addBodyPart(filePart);
//                }
            }

            // put everything together
            msg.setContent(multipart);
            msg.setSentDate(new Date());

            Transport.send(msg);
            logger.info("[pcl][mail] email send!");
        } catch (MessagingException me) {
            logger.debug("sendEmailNotification:" + me.getMessage(), me);
        } catch (UnsupportedEncodingException e) {
            logger.debug("sendEmailNotification:" + e.getMessage(), e);
        } catch (RuntimeException e) {
            logger.error("sendEmailNotification:" + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("sendEmailNotification:" + e.getMessage(), e);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }

        return result;
    }
    
    private ByteArrayDownloadResult processTemplate_email(Request request) {
        ByteArrayOutputStream out = null;
        Writer writer = null;
        OutputStreamWriter wr = null;
        FileInputStream is = null;
        try {
            String templateKeyId = request.get("edm", EDM_TEMPLATE_1);

            Configuration config = getFmConfg().getConfiguration();

            Template t = config.getTemplate(templateKeyId);
            out = new ByteArrayOutputStream();
            wr = new OutputStreamWriter(out, getSysConfig().getProperty(PageReportParam.defaultEncoding.toString(), DEFAULT_ENCORDING));
            writer = new BufferedWriter(wr);
            final String _AL_MAP = "_al_map";
            // Map<String, Object> map = new HashMap<String, Object>();
            Map<String, Object> map = excute_email(request);
            // if (request.containsKey(_AL_MAP)) {
            // map = (Map<String, Object>) request.getObject(_AL_MAP);
            // } else {
            // map = excute_email(request);
            // request.put(_AL_MAP, map);
            // }
            if (logger.isDebugEnabled()) {
                logger.debug("[freemarker] Template name: " + templateKeyId + ", data: " + map.toString());
            }
            t.process(map, writer);

            //TODO encoding parameter
            return new ByteArrayDownloadResult(request, out.toByteArray(), "text/html");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }
    
    /**
     *
     * @param request
     * @return
     */
    private Map<String, Object> excute_email(Request request) {

        Map<String, Object> m = new HashMap<String, Object>();
        HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession();
        Map<String, Object> _ar = new HashMap<String, Object>();
        // session可能是null
//        if (session != null && session.getAttribute(CCConstants.ATTR_REDIRECT) != null) {
//            _ar = (Map<String, Object>) session.getAttribute(CCConstants.ATTR_REDIRECT);
//        }

        // put session parameter.
        for (Map.Entry<String, Object> entry : _ar.entrySet()) {
            m.put(entry.getKey(), CapString.trimNull(entry.getValue()));
        }

        // 難字處理，改成圖片呈現
        // m.put("chineseNameMask", CapString.trimNull(m.get("firstNM"), " ").substring(0, 1));
//        String feeFormate = amountFormat(String.valueOf(_ar.get("fee")));
//        m.put("fee", feeFormate);
        if ("0".equals(m.get("acChoose"))) {
//            m.put("otherAccountTitleMask", "花旗(台灣)銀行 " + m.get("nowBranchCode"));
//            String otherAccountNumber = CapString.trimNull(m.get("nowAccountNumber"), "------------    ");
//            m.put("otherAccountNumberMask", "************" + otherAccountNumber.substring(otherAccountNumber.length() - 4, otherAccountNumber.length()));
//            m.put("otherFeeMask", "$ 0");
        } else {
             // NTB不會有花旗本行
//             m.put("edmChineseName",m.get("firstNM").toString().substring(0,1));
//             m.put("otherAccountTitleMask",m.get("otherAccountTitle").toString().concat(" ").concat(m.get("otherBranchCode").toString()));
//             // m.put("otherAccountTitleMask", m.get("otherAccountTitle"));
//             String otherAccountNumber = CapString.trimNull(m.get("otherAccountNumber"), "------------    ");
//             m.put("otherAccountNumberMask", "************" + otherAccountNumber.substring(otherAccountNumber.length() - 4, otherAccountNumber.length()));
//             if (CapString.trimNull(m.get("otherAccountTitle")).startsWith("021")) {
//             m.put("otherFeeMask", "$ 0");
//             } else {
//             m.put("otherFeeMask", "$ 50");
//             }
        }

        return m;
    }

    /* (non-Javadoc)
     * @see com.iisigroup.colabase.edm.service.EDMService#ftlToEDM(com.iisigroup.cap.component.Request)
     */
    @Override
    public void ftlToEDM(Request request) {
        // TODO Auto-generated method stub
        
    }

}
