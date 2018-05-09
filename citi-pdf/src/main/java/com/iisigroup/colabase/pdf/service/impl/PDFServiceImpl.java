package com.iisigroup.colabase.pdf.service.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.PDFEncryption;
import org.xhtmlrenderer.resource.XMLResource;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.report.constants.ContextTypeEnum;
import com.iisigroup.cap.report.factory.ItextFontFactory;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.pdf.report.CCBasePageReport;
import com.iisigroup.colabase.pdf.service.PDFService;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class PDFServiceImpl extends CCBasePageReport implements PDFService {

    private static Logger logger = LoggerFactory.getLogger(PDFServiceImpl.class);
    private static final String UNDER_LINE = "_";
    private static final String DEFAULT_ENCORDING = "utf-8";

    @Autowired
    private ItextFontFactory fontFactory;

    @Override
    public Result processPdf(Request request, Map<String, Object> dataMap, String templateName, String pdfPath, String pdfName, Boolean isDownloadPDF, String encryptPassword) {
        ByteArrayDownloadResult pdfContent = processPDFContent(request, dataMap, templateName, PDFType.FTL);
        return processPdf(request, pdfPath, pdfName, pdfContent, isDownloadPDF, encryptPassword);
    }

    @Override
    public Result processPdf(Request request, String pdfPath, String pdfName, ByteArrayDownloadResult pdfContent, Boolean isDownloadPDF, String encryptPassword) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream os = null;
        // PDF名稱
        String convertTimestampToString = CapDate.convertTimestampToString(CapDate.getCurrentTimestamp(), "yyyyMMdd_hhmm_ss");
        String outputFileName = "PDF" + UNDER_LINE + convertTimestampToString + ".pdf";
        if (!CapString.isEmpty(pdfName)) {
            outputFileName = pdfName + ".pdf";
        }
        try {
            // 加密密碼，建立PDF名稱並產出
            String encrypt = CapString.trimNull(encryptPassword, "");
            genByRender(out, pdfContent.getByteArray(), encrypt);// gen encrypt Pdf

            if (!isDownloadPDF && !CapString.isEmpty(pdfPath)) {// 產生實體PDF至pdfPath
                // 產出有加密PDF
                File tempDir = new File(pdfPath);
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                String encryptPdfOutputFilename = pdfPath + File.separator + outputFileName;
                File encryptPdfFile = new File(encryptPdfOutputFilename);
                os = new FileOutputStream(encryptPdfFile);
                out.writeTo(os);
            } else if (isDownloadPDF) {
                // 直接下載PDF
                ByteArrayDownloadResult downloadResult = new ByteArrayDownloadResult(request, out.toByteArray(), ContextTypeEnum.pdf.toString(), outputFileName);
                return downloadResult;
            }
            return new AjaxFormResult();

        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            throw new CapException(e.getMessage(), e.getClass());
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(os);
        }
    }

    /**
     * Process PDF Content
     * 
     * @param request
     * @param templateName
     *            FTL 樣版名稱；若為FTL格式則需填入樣板名稱
     * @return
     */
    @Override
    public ByteArrayDownloadResult processPDFContent(Request request, Map<String, Object> dataMap, String templateName, PDFType pdfType) {
        ByteArrayDownloadResult pdfContent = null;
        if (pdfType.equals(PDFType.FTL) && !CapString.isEmpty(templateName)) {
            // process ftl template
            Map<String, Object> contentMap = getPDFContent(request, dataMap);
            pdfContent = processPDFTemplate(request, templateName, contentMap);
        } else if (pdfType.equals(PDFType.HTML)) {
            // process HTML
            pdfContent = (ByteArrayDownloadResult) request.getObject(PDFType.HTML.getPdfType());
        }
        return pdfContent;
    }

    /**
     * 針對要帶入pdf中的值做處理
     * 
     * @param request
     * @param dataMap
     *            PDF欄位值資料Map
     * @return
     */
    private Map<String, Object> getPDFContent(Request request, Map<String, Object> dataMap) {
        // pur image path
        final String PDF_IMG_PATH_KEY = "imgPath";
        final String PDF_IMG_FILE_LOCATION = "pdfImageFileLocation";
        String imagesPath = getSysConfig().getProperty(PDF_IMG_FILE_LOCATION);
        URL pdfImgPath = getClass().getResource(imagesPath);
        dataMap.put(PDF_IMG_PATH_KEY, pdfImgPath);
        // put parameter.
        String logData = "";
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            dataMap.put(entry.getKey(), CapString.trimNull(entry.getValue()));
            logData = logData.concat(entry.getKey() + "=" + MapUtils.getString(dataMap, entry.getKey(), "") + " , ");
        }
        logger.debug("excute >>> all data : " + logData);
        return dataMap;
    }

    /**
     * process Template
     * 
     * @param request
     * @param templateName
     *            樣版名稱
     * @param contentMap
     *            PDF內容Map
     * @return
     */
    private ByteArrayDownloadResult processPDFTemplate(Request request, String templateName, Map<String, Object> contentMap) {
        ByteArrayOutputStream out = null;
        Writer writer = null;
        OutputStreamWriter wr = null;
        FileInputStream is = null;
        try {
            if (templateName != null) {
                Configuration config = getFmConfg().getConfiguration();
                Template t = config.getTemplate(templateName);
                out = new ByteArrayOutputStream();
                wr = new OutputStreamWriter(out, getSysConfig().getProperty(PageReportParam.defaultEncoding.toString(), DEFAULT_ENCORDING));
                writer = new BufferedWriter(wr);
                if (logger.isDebugEnabled()) {
                    logger.debug("[processPDFTemplate] freemarker template name: " + templateName + ", content data: " + contentMap.toString());
                }
                t.process(contentMap, writer);
            }
            return new ByteArrayDownloadResult(request, out.toByteArray(), ContextTypeEnum.text.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }

    /**
     * 產出PDF
     * 
     * @param out
     * @param pdfContent
     * @param encrypt
     * @throws Exception
     */
    private void genByRender(OutputStream out, byte[] pdfContent, String encrypt) throws Exception {
        // local test plz unMarked this code, but no commit to SVN
        // System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.xsltc.trax.TransformerFactoryImpl");
        /**
         * just start your local test use VM Arguments : -Djavax.xml.transform.TransformerFactory="org.apache.xalan.xsltc.trax.TransformerFactoryImpl"
         */
        // System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

        Document document = XMLResource.load(new ByteArrayInputStream(pdfContent)).getDocument();
        ITextRenderer iTextRenderer = new ITextRenderer();
        ITextFontResolver fontResolver = iTextRenderer.getFontResolver();
        fontResolver.addFont(fontFactory.getFontPath("MSJH.TTF", ""), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);//// 微軟正黑體

        if (!CapString.isEmpty(encrypt)) {
            // 只有列印權限 加密
            PDFEncryption pdfEncryption = new PDFEncryption();
            pdfEncryption.setEncryptionType(PdfWriter.ENCRYPTION_AES_128);// 花旗內部要求PDF加密格式為AES_128
            pdfEncryption.setOwnerPassword(encrypt.getBytes());
            pdfEncryption.setUserPassword(encrypt.getBytes());
            pdfEncryption.setAllowedPrivileges(PdfWriter.ALLOW_PRINTING);
            iTextRenderer.setPDFEncryption(pdfEncryption);
            logger.debug("[genByRender] create pdf with password: " + encrypt);
        } else {
            logger.debug("[genByRender] create pdf with no password");
        }

        // iTextRenderer.setDocumentFromString(new String(pdfContent, "UTF-8"));
        iTextRenderer.setDocument(document, null);
        iTextRenderer.layout();
        iTextRenderer.createPDF(out);
    }

}
