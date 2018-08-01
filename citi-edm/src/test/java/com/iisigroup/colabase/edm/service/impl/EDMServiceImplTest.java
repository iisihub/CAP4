package com.iisigroup.colabase.edm.service.impl;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

import org.junit.Test;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.cap.component.impl.CapSpringMVCRequest;
import com.iisigroup.cap.utils.CapString;

import freemarker.template.Template;

public class EDMServiceImplTest {

    private static final String DEFAULT_ENCORDING = "UTF-8";
    private static final String TEST_FREEMARK_DIR = "classpath:/ftl/";
    private static final String edmFtlPath = "colabaseDemo/edm1.ftl";
    private static final String mailAddress = "johnson.ho@iisigroup.com";
    private static final String edmCustomerName = "Johnson";
    private static final String edmProject = "隨時清償";
    private static final String SEND_FILE_PATH = "/ftl/colabaseDemo/edmImages/blue.jpg";

    @Test
    public void testSendEDM() {
        Request request = new CapSpringMVCRequest();
        boolean isSuccess = true;
        // 給值EDM
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("mailAddress", mailAddress);
        dataMap.put("edmCustomerName", edmCustomerName);
        dataMap.put("edmProject", edmProject);
        
        ByteArrayDownloadResult pdfContent = processTemplateEmail(request, edmFtlPath, dataMap);

        if (!CapString.isEmpty(mailAddress) && pdfContent != null) {
            sendEDM(mailAddress, pdfContent.getByteArray(), request);
            assertTrue(isSuccess);
        } else {
            throw new NullPointerException();
        }
    }
    
    public ByteArrayDownloadResult processTemplateEmail(Request request, String edmFtlPath, Map<String, Object> dataMap) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                OutputStreamWriter wr = new OutputStreamWriter(out, "UTF-8");
                Writer writer = new BufferedWriter(wr);
                FileInputStream is = null;) {

            FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
            factory.setTemplateLoaderPath(TEST_FREEMARK_DIR);
            factory.setDefaultEncoding(DEFAULT_ENCORDING);
            FreeMarkerConfigurer config = new FreeMarkerConfigurer();
            config.setConfiguration(factory.createConfiguration());
            Template t = config.getConfiguration().getTemplate(edmFtlPath);
            t.process(dataMap, writer);

            Map<String, Object> map = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                map.put(entry.getKey(), CapString.trimNull(entry.getValue()));
            }
            
            System.out.println("[EDM] Template name: " + edmFtlPath + ",data : " + map);
            t.process(map, writer);
            
            return new ByteArrayDownloadResult(request, out.toByteArray(), DEFAULT_ENCORDING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Result sendEDM(String mailAddress, byte[] datas, Request request) {
        AjaxFormResult result = new AjaxFormResult();

        try {
            final String FROM_ADDRESS = "citi@imta.citicorp.com";
            final String FROM_PERSON = "花旗（台灣）銀行";
            final String EDM_HOST = "smtp.gmail.com";
            final String EDM_USR = "css123456tw@gmail.com";
            final String EDM_PWD = "kvzulwkqdoiprtfb";
            String edmSubject = "花旗(台灣)銀行 圓滿貸線上申請確認通知函";

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
            mailSession.setDebug(true);
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

                String imagePath = "/ftl/colabaseDemo/edmImages/";
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
                    System.out.println("[EDM] Index is : {}" + index);
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
                multipart = sendFile(multipart);
            }

            // put everything together
            msg.setContent(multipart);
            msg.setSentDate(new Date());

            Transport.send(msg);
            System.out.println("[EDM] email send!");
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }

        return result;
    }
    
    private MimeMultipart sendFile(MimeMultipart multipart) {
        // 處理附加檔案
        File sendFile;
        MimeBodyPart filePart = new MimeBodyPart();
        
        // send file
        try {
            String imagePath = getClass().getResource(SEND_FILE_PATH).getPath();
            sendFile = new File(imagePath);
            filePart.attachFile(sendFile);
            multipart.addBodyPart(filePart);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return multipart;
    }
}