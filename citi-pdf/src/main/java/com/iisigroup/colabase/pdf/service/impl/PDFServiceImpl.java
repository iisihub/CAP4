package com.iisigroup.colabase.pdf.service.impl;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.PDFEncryption;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.report.constants.ContextTypeEnum;
import com.iisigroup.cap.report.factory.ItextFontFactory;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.pdf.service.PDFService;
import com.iisigroup.colabase.report.CCBasePageReport;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * PDF Service Implement
 * 
 * @since Apr 18, 2019
 * @author Cathy
 * @version
 *          <ul>
 *          <li>Apr 18, 2019,Cathy,new
 *          </ul>
 */
@Service
public class PDFServiceImpl extends CCBasePageReport implements PDFService {

    @Autowired
    private ItextFontFactory fontFactory;

    private static final String DEFAULT_FONT = "MSJH.TTF";// 微軟正黑體
    private static final String UNDER_LINE = "_";
    private static final String DEFAULT_ENCORDING = "utf-8";

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#processPdfByFtl(java.util.Map, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Result processPdfByFtl(Map<String, Object> dataMap, String ftlTemplateName, String pdfPath, String pdfName, String encryptPassword, String fontName) {
        byte[] pdfContent = processPdfContent(dataMap, ftlTemplateName);
        return processPdf(pdfContent, pdfPath, pdfName, encryptPassword, fontName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#processPdf(byte[], java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Result processPdf(byte[] pdfContent, String pdfPath, String pdfName, String encryptPassword, String fontName) {
        AjaxFormResult result = new AjaxFormResult();
        // PDF名稱
        String font = "";
        String outputFileName = "";
        if (!CapString.isEmpty(pdfName)) {
            outputFileName = pdfName + ".pdf";
        } else {
            outputFileName = defalutPDFName();
        }
        try {
            if (CapString.isEmpty(fontName)) {
                fontName = DEFAULT_FONT;
            }
            font = font = fontFactory.getFontPath(fontName, "");
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        }
        String encryptPdfOutputFilename = pdfPath + File.separator + outputFileName;
        File encryptPdfFile = new File(encryptPdfOutputFilename);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            // 加密密碼，建立PDF名稱並產出
            String encrypt = CapString.trimNull(encryptPassword, "");
            genByRender(out, pdfContent, encrypt, font);// gen encrypt Pdf
            if (!CapString.isEmpty(pdfPath)) {// 產生實體PDF至pdfPath
                // 產出有加密PDF
                File tempDir = new File(pdfPath);
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                try (OutputStream os = new FileOutputStream(encryptPdfFile);) {
                    out.writeTo(os);
                } catch (Exception e) {
                    logger.debug(e.getMessage(), e);
                    result.set("isSuccess", false);
                }
            }
            result.set("isSuccess", true);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            result.set("isSuccess", false);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#processPdf(java.util.ArrayList, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Result processPdf(ArrayList<byte[]> pdfContent, String pdfPath, String pdfName, String encryptPassword, String fontName) {
        AjaxFormResult result = new AjaxFormResult();
        // PDF名稱
        String font = "";
        String outputFileName = "";
        if (!CapString.isEmpty(pdfName)) {
            outputFileName = pdfName + ".pdf";
        } else {
            outputFileName = defalutPDFName();
        }
        try {
            if (CapString.isEmpty(fontName)) {
                fontName = DEFAULT_FONT;
            }
            font = fontFactory.getFontPath(fontName, "");
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        }
        String encryptPdfOutputFilename = pdfPath + File.separator + outputFileName;
        File encryptPdfFile = new File(encryptPdfOutputFilename);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            // 加密密碼，建立PDF名稱並產出
            String encrypt = CapString.trimNull(encryptPassword, "");
            genByRender(out, pdfContent, encrypt, font);// gen encrypt Pdf
            if (!CapString.isEmpty(pdfPath)) {// 產生實體PDF至pdfPath
                // 產出有加密PDF
                File tempDir = new File(pdfPath);
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                try (OutputStream os = new FileOutputStream(encryptPdfFile);) {
                    out.writeTo(os);
                } catch (Exception e) {
                    logger.debug(e.getMessage(), e);
                    result.set("isSuccess", false);
                }
            }
            result.set("isSuccess", true);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            result.set("isSuccess", false);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#downloadPdf(com.iisigroup.cap.component.Request, java.lang.String)
     */
    @Override
    public Result downloadPdf(Request request, String pdfPath) {
        try {
            File file = new File(pdfPath);
            String pdfName = "";
            if (file.exists()) {
                pdfName = file.getName();
                return new ByteArrayDownloadResult(request, FileUtils.readFileToByteArray(file), ContextTypeEnum.pdf.toString(), pdfName);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new CapException(e.getMessage(), getClass());
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#downloadPdf(com.iisigroup.cap.component.Request, byte[], java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Result downloadPdf(Request request, byte[] pdfContent, String pdfName, String encryptPassword, String fontName) {
        AjaxFormResult result = new AjaxFormResult();
        // PDF名稱
        String font = "";
        String outputFileName = "";
        if (!CapString.isEmpty(pdfName)) {
            outputFileName = pdfName + ".pdf";
        } else {
            outputFileName = defalutPDFName();
        }
        try {
            if (CapString.isEmpty(fontName)) {
                fontName = DEFAULT_FONT;
            }
            font = fontFactory.getFontPath(fontName, "");
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            // 加密密碼，建立PDF名稱並產出
            String encrypt = CapString.trimNull(encryptPassword, "");
            genByRender(out, pdfContent, encrypt, font);// gen encrypt Pdf
            // 直接下載PDF
            result.set("isSuccess", true);
            return new ByteArrayDownloadResult(request, out.toByteArray(), ContextTypeEnum.pdf.toString(), outputFileName);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            result.set("isSuccess", false);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#processPdfContent(java.util.Map, java.lang.String)
     */
    @Override
    public byte[] processPdfContent(Map<String, Object> dataMap, String ftlTemplateName) {
        byte[] pdfContent = null;
        if (!CapString.isEmpty(ftlTemplateName)) {
            // process ftl template
            Map<String, Object> contentMap = getPDFContent(dataMap);
            pdfContent = processPdfTemplate(ftlTemplateName, contentMap);
        }
        return pdfContent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#processPdfContent(java.util.Map, java.lang.String[])
     */
    @Override
    public ArrayList<byte[]> processPdfContent(Map<String, Object> dataMap, String[] ftlTemplateAry) {
        ArrayList<byte[]> pdfContent = null;
        if (ftlTemplateAry.length > 0) {
            // process ftl template
            Map<String, Object> contentMap = getPDFContent(dataMap);
            pdfContent = processPdfTemplate(ftlTemplateAry, contentMap);
        }
        return pdfContent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#mergePdfFiles(java.lang.String[], java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean mergePdfFiles(String[] filesPath, String mergerPDFPath, String mergerPDFName, String encryptPassword) {
        boolean isSuccess = false;
        Document document = new Document();
        // PDF名稱
        String outputMergeFileName = "";
        if (!CapString.isEmpty(mergerPDFName)) {
            outputMergeFileName = mergerPDFName + ".pdf";
        } else {
            outputMergeFileName = defalutPDFName();
        }
        try (OutputStream outputStream = new FileOutputStream(mergerPDFPath + File.separator + outputMergeFileName);) {
            document = new Document(new PdfReader(filesPath[0]).getPageSize(1));
            PdfCopy copy = new PdfCopy(document, outputStream);
            if (!CapString.isEmpty(encryptPassword))
                copy.setEncryption(encryptPassword.getBytes(), encryptPassword.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
            document.open();
            for (int i = 0; i < filesPath.length; i++) {
                PdfReader reader = new PdfReader(filesPath[i]);
                int n = reader.getNumberOfPages();
                for (int j = 1; j <= n; j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }
            }
            document.close();
            isSuccess = true;
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
        return isSuccess;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#partitionPdfFile(java.lang.String, java.lang.String, int, java.lang.String)
     */
    @Override
    public boolean partitionPdfFile(String inputFilePath, String outputFilePath, int partitionPageNum, String encryptPassword) {
        boolean isSuccess = false;
        Document document = null;
        try {
            PdfCopy copy = null;
            PdfReader reader = new PdfReader(inputFilePath);
            int n = reader.getNumberOfPages();

            if (n < partitionPageNum) {
                logger.debug("The document does not have  {}  pages to partition !", partitionPageNum);
                throw new CapException();
            }

            int size = n / partitionPageNum;
            String savePath = null;
            String savePathName = inputFilePath.substring(inputFilePath.lastIndexOf('/') + 1, inputFilePath.length() - 4);
            ArrayList<String> savePaths = new ArrayList<String>();
            for (int i = 1; i <= partitionPageNum; i++) {
                if (i < 10) {
                    savePath = outputFilePath + File.separator + savePathName + "_" + i + ".pdf";
                } else {
                    savePath = outputFilePath + File.separator + savePathName + i + ".pdf";
                }
                savePaths.add(savePath);
            }

            for (int i = 0; i < partitionPageNum - 1; i++) {
                document = new Document(reader.getPageSize(1));
                copy = new PdfCopy(document, new FileOutputStream(savePaths.get(i)));
                if (!CapString.isEmpty(encryptPassword))
                    copy.setEncryption(encryptPassword.getBytes(), encryptPassword.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
                document.open();
                for (int j = size * i + 1; j <= size * (i + 1); j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }
                document.close();
            }

            document = new Document(reader.getPageSize(1));
            copy = new PdfCopy(document, new FileOutputStream(savePaths.get(partitionPageNum - 1)));
            if (!CapString.isEmpty(encryptPassword))
                copy.setEncryption(encryptPassword.getBytes(), encryptPassword.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
            document.open();
            for (int j = size * (partitionPageNum - 1) + 1; j <= n; j++) {
                document.newPage();
                PdfImportedPage page = copy.getImportedPage(reader, j);
                copy.addPage(page);
            }
            document.close();
            isSuccess = true;
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
        return isSuccess;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#addTextWatermark(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void addTextWatermark(String inputFilePath, String outputFilePath, String textWatermark) throws DocumentException, IOException {
        Float opacity = 0.7f;// 透明度
        int rotationDegree = 0;// 旋轉角度
        int textWMRepeatNum = 4;// 文字浮水印重複次數
        BaseFont font = null;
        float fontSize = 32;
        try {
            font = this.getBaseMSJHFont();// 設定字型
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        this.addWatermark(inputFilePath, outputFilePath, textWatermark, "", font, fontSize, opacity, rotationDegree, textWMRepeatNum);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#addImgWatermark(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void addImgWatermark(String inputFilePath, String outputFilePath, String imgWatermarkPath) throws DocumentException, IOException {
        Float opacity = 0.4f;// 透明度
        int rotationDegree = 15;// 旋轉角度
        this.addWatermark(inputFilePath, outputFilePath, "", imgWatermarkPath, null, 0, opacity, rotationDegree, 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#addWatermark(java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.lowagie.text.pdf.BaseFont, float, java.lang.Float,
     * int, int)
     */
    @Override
    public boolean addWatermark(String inputFilePath, String outputFilePath, String textWatermark, String imgWatermarkPath, BaseFont font, float fontSize, Float opacity, int rotationDegree,
            int textWMRepeatNum) throws DocumentException, IOException {
        boolean isSuccess = false;
        FileInputStream inputStream = new FileInputStream(inputFilePath);
        FileOutputStream outputStream = new FileOutputStream(outputFilePath);
        Document document = new Document(PageSize.A4);
        PdfReader pdfReader = new PdfReader(inputStream);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        PdfContentByte pageContent = null;
        try {
            if (CapString.isEmpty(textWatermark) && font == null && fontSize == 0) {
                return isSuccess;
            }
            for (int i = 1, pdfPageSize = pdfReader.getNumberOfPages() + 1; i < pdfPageSize; i++) {
                pageContent = pdfStamper.getOverContent(i);// pdfContent所加入的浮水印會在PDF內容最上層
                pageContent.saveState();
                pageContent.setGState(this.getPdfGState(opacity));
                // 浮水印位置置中
                float documentWidth = document.getPageSize().getWidth() / 2;
                float documentHeight = document.getPageSize().getHeight() / 2;
                int alignment = Element.ALIGN_CENTER;
                if (!CapString.isEmpty(imgWatermarkPath)) {// 圖片
                    Image image = Image.getInstance(imgWatermarkPath);
                    image.setAbsolutePosition(documentWidth, documentHeight);
                    image.setAlignment(alignment);
                    image.setRotation(rotationDegree);
                    pageContent.addImage(image);
                } else if (!CapString.isEmpty(textWatermark)) {// 文字
                    pageContent.beginText();
                    pageContent.setColorFill(Color.LIGHT_GRAY);
                    pageContent.setFontAndSize(font, fontSize);
                    if (textWMRepeatNum > 0) {// text repeat
                        for (int j = 1; j <= textWMRepeatNum; j++) {
                            documentHeight = (document.getPageSize().getHeight() / textWMRepeatNum) * j - 100;
                            pageContent.showTextAligned(alignment, textWatermark, documentWidth, documentHeight, rotationDegree);
                        }
                    } else {
                        pageContent.showTextAligned(alignment, textWatermark, documentWidth, documentHeight, rotationDegree);
                    }
                    pageContent.endText();
                }
            }
            isSuccess = true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        pdfStamper.close();
        return isSuccess;
    }

    /**
     * 針對要帶入pdf中的值做處理
     * 
     * @param dataMap
     *            PDF欄位值資料Map
     * @return 放置PDF內容的Map
     */
    private Map<String, Object> getPDFContent(Map<String, Object> dataMap) {
        // put image path
        final String PDF_IMG_PATH_KEY = "imgPath";
        final String PDF_IMG_FILE_LOCATION = "pdfImageFileLocation";
        String imagesPath = getSysConfig().getProperty(PDF_IMG_FILE_LOCATION);
        if (!CapString.isEmpty(imagesPath)) {
            URL pdfImgPath = getClass().getResource(imagesPath);
            dataMap.put(PDF_IMG_PATH_KEY, pdfImgPath);
        }
        // put parameter.
        String logData = "";
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            dataMap.put(entry.getKey(), CapString.trimNull(entry.getValue()));
            logData = logData.concat(entry.getKey() + "=" + MapUtils.getString(dataMap, entry.getKey(), "") + " , ");
        }
        logger.debug("excute >>> all data : {}", logData);
        return dataMap;
    }

    /**
     * process Template
     * 
     * @param ftLTemplateName
     *            樣版名稱
     * @param contentMap
     *            PDF內容Map
     * @return FTL Template Byte陣列
     */
    private byte[] processPdfTemplate(String ftLTemplateName, Map<String, Object> contentMap) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                OutputStreamWriter wr = new OutputStreamWriter(out, getSysConfig().getProperty(PageReportParam.DEFAULT_ENCODING.toString(), DEFAULT_ENCORDING));
                Writer writer = new BufferedWriter(wr);) {
            if (ftLTemplateName != null) {
                Configuration config = getFmConfg().getConfiguration();
                Template t = config.getTemplate(ftLTemplateName);
                if (logger.isDebugEnabled()) {
                    logger.debug("[processPDFTemplate] freemarker template name: {} , content data: {}", ftLTemplateName, contentMap);
                }
                t.process(contentMap, writer);
            }
            return out.toByteArray();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Process Multiple FTL Template
     * 
     * @param ftLTemplatAry
     *            多個樣版陣列
     * @param contentMap
     *            PDF內容Map
     * @return Multiple FTL Template Byte陣列
     */
    private ArrayList<byte[]> processPdfTemplate(String[] ftLTemplatAry, Map<String, Object> contentMap) {
        ArrayList<byte[]> ftLTemplatArybyte = new ArrayList<byte[]>();
        for (String ftLTemplateName : ftLTemplatAry) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                    OutputStreamWriter wr = new OutputStreamWriter(out, getSysConfig().getProperty(PageReportParam.DEFAULT_ENCODING.toString(), DEFAULT_ENCORDING));
                    Writer writer = new BufferedWriter(wr);) {
                Configuration config = getFmConfg().getConfiguration();
                Template t = config.getTemplate(ftLTemplateName);
                if (logger.isDebugEnabled()) {
                    logger.debug("[processPDFTemplate] freemarker template name: {} , content data: {}", ftLTemplateName, contentMap);
                }
                t.process(contentMap, writer);
                ftLTemplatArybyte.add(out.toByteArray());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return ftLTemplatArybyte;
    }

    /**
     * 產出PDF
     * 
     * @param out
     *            OutputStream
     * @param pdfContent
     *            PDF內容
     * @param encrypt
     *            加密密碼
     * @param font
     *            字型
     * @throws DocumentException
     * @throws Exception
     */
    private void genByRender(OutputStream out, byte[] pdfContent, String encrypt, String font) throws Exception {
        String property = System.getProperty("javax.xml.transform.TransformerFactory");// org.apache.xalan.xsltc.trax.TransformerFactoryImpl
        logger.debug("javax.xml.transform.TransformerFactory :: {}", property);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        ITextRenderer iTextRenderer = new ITextRenderer();
        ITextFontResolver fontResolver = iTextRenderer.getFontResolver();
        fontResolver.addFont(font, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);// 字形

        if (!CapString.isEmpty(encrypt)) {
            // 只有列印權限 加密
            PDFEncryption pdfEncryption = new PDFEncryption();
            pdfEncryption.setEncryptionType(PdfWriter.ENCRYPTION_AES_128);// 花旗內部要求PDF加密格式為AES_128
            pdfEncryption.setOwnerPassword(encrypt.getBytes());
            pdfEncryption.setUserPassword(encrypt.getBytes());
            pdfEncryption.setAllowedPrivileges(PdfWriter.ALLOW_PRINTING);
            iTextRenderer.setPDFEncryption(pdfEncryption);
            logger.debug("[genByRender] create pdf with password: {}", encrypt);
        } else {
            logger.debug("[genByRender] create pdf with no password");
        }

        String html = new String(pdfContent, "UTF-8");
        html = html.replaceAll("&nbsp;", "&#160;");// avoid The entity "nbsp" was referenced, but not declared. exception
        org.w3c.dom.Document document = db.parse(new StringInputStream(html, "UTF-8"));
        // org.w3c.dom.Document document = XMLResource.load(new ByteArrayInputStream(pdfContent)).getDocument();
        iTextRenderer.setDocument(document, null);
        iTextRenderer.layout();
        iTextRenderer.createPDF(out);
    }

    /**
     * 產出多個FTL Template的PDF
     * 
     * @param out
     *            OutputStream
     * @param pdfContent
     *            PDF內容
     * @param encrypt
     *            加密密碼
     * @param font
     *            字型
     * @throws Exception
     */
    private void genByRender(OutputStream out, ArrayList<byte[]> pdfContent, String encrypt, String font) throws Exception {
        String property = System.getProperty("javax.xml.transform.TransformerFactory");// org.apache.xalan.xsltc.trax.TransformerFactoryImpl
        logger.debug("javax.xml.transform.TransformerFactory :: {}", property);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        ITextRenderer iTextRenderer = new ITextRenderer();
        ITextFontResolver fontResolver = iTextRenderer.getFontResolver();
        fontResolver.addFont(font, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);// 字形

        if (!CapString.isEmpty(encrypt)) {
            // 只有列印權限 加密
            PDFEncryption pdfEncryption = new PDFEncryption();
            pdfEncryption.setEncryptionType(PdfWriter.ENCRYPTION_AES_128);// 花旗內部要求PDF加密格式為AES_128
            pdfEncryption.setOwnerPassword(encrypt.getBytes());
            pdfEncryption.setUserPassword(encrypt.getBytes());
            pdfEncryption.setAllowedPrivileges(PdfWriter.ALLOW_PRINTING);
            iTextRenderer.setPDFEncryption(pdfEncryption);
            logger.debug("[genByRender] create pdf with password: {}", encrypt);
        } else {
            logger.debug("[genByRender] create pdf with no password");
        }

        org.w3c.dom.Document document;
        int i = 0;
        for (byte[] pdfbyte : pdfContent) {
            String html = new String(pdfbyte, "UTF-8");
            html = html.replaceAll("&nbsp;", "&#160;");// avoid The entity "nbsp" was referenced, but not declared. exception
            document = db.parse(new StringInputStream(html, "UTF-8"));
            iTextRenderer.setDocument(document, null);
            iTextRenderer.layout();
            // 第一次進來先create
            if (i == 0) {
                iTextRenderer.createPDF(out, false);
                // 增加第一頁長度
                i = 1;
                // 之後的直接寫下一頁
            } else if (i == 1) {
                iTextRenderer.writeNextDocument();
            }
        }
        iTextRenderer.finishPDF();
    }

    /**
     * 若無指定PDF檔案名稱，則預設給定檔案PDF_yyyyMMdd_hhmm_ss.pdf
     * 
     * @return Defalut PDF Name
     */
    private String defalutPDFName() {
        String convertTimestampToString = CapDate.convertTimestampToString(CapDate.getCurrentTimestamp(), "yyyyMMdd_hhmm_ss");
        return "PDF" + UNDER_LINE + convertTimestampToString + ".pdf";
    }

    /**
     * Get BaseFont 微軟正黑體
     *
     * @return 取得微軟正黑體字型
     * @throws IOException
     * @throws DocumentException
     */
    private BaseFont getBaseMSJHFont() throws DocumentException, IOException {
        return BaseFont.createFont(getMSJHFont(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
    }

    /**
     * Get String 微軟正黑體
     * 
     * @return 取得微軟正黑體字型
     * @throws IOException
     */
    private String getMSJHFont() throws IOException {
        return fontFactory.getFontPath(DEFAULT_FONT, "");
    }

    /**
     * Get PdfGState 取得透明度
     *
     * @return 取得透明度
     */
    private PdfGState getPdfGState(Float opacity) {
        PdfGState graphicState = new PdfGState();
        graphicState.setFillOpacity(opacity);
        graphicState.setStrokeOpacity(opacity);
        return graphicState;
    }
}
