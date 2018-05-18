package com.iisigroup.colabase.pdf.service.impl;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import com.lowagie.text.Document;
import com.lowagie.text.Element;
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

@Service
public class PDFServiceImpl extends CCBasePageReport implements PDFService {

    @Autowired
    private ItextFontFactory fontFactory;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String DEFAULT_FONT = "MSJH.TTF";// 微軟正黑體
    private static final String UNDER_LINE = "_";
    private static final String DEFAULT_ENCORDING = "utf-8";

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#processPdf(com.iisigroup.cap.component.Request, java.util.Map, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean,
     * java.lang.String, java.lang.String)
     */
    @Override
    public Result processPdf(Request request, Map<String, Object> dataMap, String templateName, String pdfPath, String pdfName, Boolean isDownloadPDF, String encryptPassword, String font) {
        ByteArrayDownloadResult pdfContent = processPDFContent(request, dataMap, templateName, PDFType.FTL);
        return processPdf(request, pdfPath, pdfName, pdfContent, isDownloadPDF, encryptPassword, font);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#processPdf(com.iisigroup.cap.component.Request, java.lang.String, java.lang.String, com.iisigroup.cap.component.impl.ByteArrayDownloadResult,
     * java.lang.Boolean, java.lang.String, java.lang.String)
     */
    @Override
    public Result processPdf(Request request, String pdfPath, String pdfName, ByteArrayDownloadResult pdfContent, Boolean isDownloadPDF, String encryptPassword, String font) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream os = null;
        // PDF名稱
        String outputFileName = "";
        if (!CapString.isEmpty(pdfName)) {
            outputFileName = pdfName + ".pdf";
        } else {
            outputFileName = defalutPDFName();
        }
        if (!CapString.isEmpty(font)) {
            try {
                font = fontFactory.getFontPath(DEFAULT_FONT, "");
            } catch (IOException e) {
            }
        }
        try {
            // 加密密碼，建立PDF名稱並產出
            String encrypt = CapString.trimNull(encryptPassword, "");
            genByRender(out, pdfContent.getByteArray(), encrypt, font);// gen encrypt Pdf

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

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#processPDFContent(com.iisigroup.cap.component.Request, java.util.Map, java.lang.String, com.iisigroup.colabase.pdf.service.PDFService.PDFType)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#mergePDFFiles(java.lang.String[], java.lang.String, java.lang.String)
     */
    @Override
    public Result mergePDFFiles(String[] filesPath, String mergerPDFPath, String mergerPDFName) {
        Document document = new Document();
        OutputStream outputStream = null;
        try {
            // PDF名稱
            String outputMergeFileName = "";
            if (!CapString.isEmpty(mergerPDFName)) {
                outputMergeFileName = mergerPDFName + ".pdf";
            } else {
                outputMergeFileName = defalutPDFName();
            }
            document = new Document(new PdfReader(filesPath[0]).getPageSize(1));
            outputStream = new FileOutputStream(mergerPDFPath + File.separator + outputMergeFileName);
            PdfCopy copy = new PdfCopy(document, outputStream);
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
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            throw new CapException(e.getMessage(), e.getClass());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
            IOUtils.closeQuietly(outputStream);
        }
        return new AjaxFormResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#partitionPdfFile(java.lang.String, java.lang.String, int)
     */
    public Result partitionPdfFile(String inputFilePath, String outputFilePath, int partitionPageNum) {
        Document document = null;
        PdfCopy copy = null;
        try {
            PdfReader reader = new PdfReader(inputFilePath);
            int n = reader.getNumberOfPages();

            if (n < partitionPageNum) {
                logger.debug(String.valueOf(partitionPageNum), "The document does not have  {}  pages to partition !");
                return null;
            }

            int size = n / partitionPageNum;
            String savepath = null;
            ArrayList<String> savepaths = new ArrayList<String>();
            for (int i = 1; i <= partitionPageNum; i++) {
                savepath = inputFilePath.substring(inputFilePath.lastIndexOf("/") + 1, inputFilePath.length() - 4);
                if (i < 10) {
                    savepath = outputFilePath + File.separator + savepath + "_" + i + ".pdf";
                } else {
                    savepath = outputFilePath + File.separator + savepath + i + ".pdf";
                }
                savepaths.add(savepath);
            }

            for (int i = 0; i < partitionPageNum - 1; i++) {
                document = new Document(reader.getPageSize(1));
                copy = new PdfCopy(document, new FileOutputStream(savepaths.get(i)));
                document.open();
                for (int j = size * i + 1; j <= size * (i + 1); j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }
                document.close();
            }

            document = new Document(reader.getPageSize(1));
            copy = new PdfCopy(document, new FileOutputStream(savepaths.get(partitionPageNum - 1)));
            document.open();
            for (int j = size * (partitionPageNum - 1) + 1; j <= n; j++) {
                document.newPage();
                PdfImportedPage page = copy.getImportedPage(reader, j);
                copy.addPage(page);
            }
            document.close();
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            throw new CapException(e.getMessage(), e.getClass());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
        return new AjaxFormResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#addWatermark(java.lang.String, java.lang.String, java.lang.String)
     */
    public void addWatermark(String inputFilePath, String outputFilePath, String watermark) throws Exception {
        Float opacity = 0.7f;// 透明度0.7
        int degree = 15;// 15度角
        BaseFont font = null;
        float fontSize = 24;
        try {
            font = this.getBaseMSJHFont();// 設定字型
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        this.addWatermark(inputFilePath, outputFilePath, watermark, font, fontSize, opacity, degree);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.colabase.pdf.service.PDFService#addWatermark(java.lang.String, java.lang.String, java.lang.String, com.lowagie.text.pdf.BaseFont, float, java.lang.Float, int)
     */
    public void addWatermark(String inputFilePath, String outputFilePath, String watermark, BaseFont font, float fontSize, Float opacity, int degree) throws Exception {
        FileInputStream inputStream = new FileInputStream(inputFilePath);
        FileOutputStream outputStream = new FileOutputStream(outputFilePath);
        Document document = new Document(PageSize.A4);
        PdfReader pdfReader = new PdfReader(inputStream);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        PdfContentByte pageContent = null;
        try {
            if (font == null || pdfStamper == null) {
                return;
            }
            for (int i = 1, pdfPageSize = pdfReader.getNumberOfPages() + 1; i < pdfPageSize; i++) {
                pageContent = pdfStamper.getOverContent(i);// pdfContent所加入的浮水印會在PDF內容最上層
                pageContent.saveState();
                pageContent.setGState(this.getPdfGState(opacity));
                pageContent.beginText();
                pageContent.setColorFill(Color.LIGHT_GRAY);
                pageContent.setFontAndSize(font, fontSize);
                pageContent.showTextAligned(Element.ALIGN_CENTER, watermark, document.getPageSize().getWidth() / 2, document.getPageSize().getHeight() / 2, degree);
                pageContent.endText();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            pageContent = null;
            font = null;
        }
        pdfStamper.close();
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
        logger.debug(logData, "excute >>> all data : {}");
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
        OutputStreamWriter wr = null;
        FileInputStream is = null;
        Writer writer = null;
        try {
            if (templateName != null) {
                Configuration config = getFmConfg().getConfiguration();
                Template t = config.getTemplate(templateName);
                out = new ByteArrayOutputStream();
                wr = new OutputStreamWriter(out, getSysConfig().getProperty(PageReportParam.defaultEncoding.toString(), DEFAULT_ENCORDING));
                writer = new BufferedWriter(wr);
                if (logger.isDebugEnabled()) {
                    logger.debug(templateName, contentMap.toString(), "[processPDFTemplate] freemarker template name: {} , content data: {}");
                }
                t.process(contentMap, writer);
            }
            return new ByteArrayDownloadResult(request, out.toByteArray(), ContextTypeEnum.text.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new CapException(e.getMessage(), e.getClass());
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(wr);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }
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
     *            自行
     * @throws Exception
     */
    private void genByRender(OutputStream out, byte[] pdfContent, String encrypt, String font) throws Exception {
        /**
         * just start your local test use VM Arguments : -Djavax.xml.transform.TransformerFactory="org.apache.xalan.xsltc.trax.TransformerFactoryImpl" to SVN
         */
        // local test please unmark below code, but don't commit unmark code to remote server
        // System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.xsltc.trax.TransformerFactoryImpl");

        org.w3c.dom.Document document = XMLResource.load(new ByteArrayInputStream(pdfContent)).getDocument();
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
            logger.debug(encrypt, "[genByRender] create pdf with password: {}");
        } else {
            logger.debug("[genByRender] create pdf with no password");
        }

        iTextRenderer.setDocument(document, null);
        iTextRenderer.layout();
        iTextRenderer.createPDF(out);
    }

    /**
     * 若無指定PDF檔案名稱，則預設給定檔案PDF_yyyyMMdd_hhmm_ss.pdf
     * 
     * @return defalutPDFName
     */
    private String defalutPDFName() {
        String convertTimestampToString = CapDate.convertTimestampToString(CapDate.getCurrentTimestamp(), "yyyyMMdd_hhmm_ss");
        String defaultPDFName = "PDF" + UNDER_LINE + convertTimestampToString + ".pdf";
        return defaultPDFName;
    }

    /**
     * Get BaseFont 微軟正黑體
     *
     * @return
     * @throws Exception
     */
    private BaseFont getBaseMSJHFont() throws Exception {
        return BaseFont.createFont(getMSJHFont(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
    }

    /**
     * Get String 微軟正黑體
     * 
     * @return
     * @throws Exception
     */
    private String getMSJHFont() throws Exception {
        return fontFactory.getFontPath(DEFAULT_FONT, "");
    }

    /**
     * Get PdfGState 取得透明度
     *
     * @return
     */
    private PdfGState getPdfGState(Float opacity) {
        PdfGState graphicState = new PdfGState();
        graphicState.setFillOpacity(opacity);
        graphicState.setStrokeOpacity(opacity);
        return graphicState;
    }

}
