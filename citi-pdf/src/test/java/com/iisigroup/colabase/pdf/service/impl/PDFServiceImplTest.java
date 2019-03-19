package com.iisigroup.colabase.pdf.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.cap.component.impl.CapSpringMVCRequest;
import com.iisigroup.cap.report.constants.ContextTypeEnum;
import com.iisigroup.cap.report.factory.ItextFontFactory;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;
import com.lowagie.text.pdf.BaseFont;

import freemarker.template.Template;

@RunWith(MockitoJUnitRunner.class)
public class PDFServiceImplTest {
    private PDFServiceImpl pdfService;
    private static final String TEST_CUST_NAME = "王測試";
    private static final String TEST_ID_NO = "M123456789";
    private static final String TEST_MPHONE = "0911222333";
    private static final String TEST_SIGN_WORDING = "線上簽名驗證完成";
    private static final String TEST_DATE_FORMAT = "yyyy/MM/dd";
    private static final String TEST_FONT_PATH = "report/font/MSJH.TTF";
    private static final String TEST_PDF_IMG_FILE_LOCATION = "ftl/colabaseDemo/pdfImages/";
    private static final String TEST_FREEMARK_DIR = "classpath:/ftl/";
    private static final String TEST_TEXT_WM = "浮水印公司翻印必究";
    private static final String PDF_PATH1 = "/Users/cathy/Downloads/testPDF/test1.pdf";
    private static final String PDF_PATH2 = "/Users/cathy/Downloads/testPDF/test2.pdf";
    private static final String PDF_OUT_PATH = "/Users/cathy/Downloads/testPDF/Output";
    private static final String PDF_NAME = "testPDF";
    private static final String PDF_PASS_WORD = "1111";
    private static final String DEFAULT_FONT = "MSJH.TTF";// 微軟正黑體
    private static final String FTL_TEMPLETE_NAME = "PDF_TEMPLETE.ftl";
    private static final String DEFAULT_ENCORDING = "utf-8";
    private static final String MERGE_PDF_NAME = "testMergePDF";
    private static final String PART_PDF_FILE_NAME = "testPartPDF.pdf";

    @Mock
    private ItextFontFactory fontFactory;

    @Before
    public void setUp() throws Exception {
        pdfService = new PDFServiceImpl();
        fontFactory = Mockito.mock(ItextFontFactory.class);
        Mockito.when(fontFactory.getFontPath(DEFAULT_FONT, "")).thenReturn(TEST_FONT_PATH);
    }

    @Test
    public void testProcessPdf() {
        Request request = new CapSpringMVCRequest();
        String colabaseDemoPath = "colabaseDemo" + File.separator;
        String templateName = colabaseDemoPath + FTL_TEMPLETE_NAME;
        ByteArrayDownloadResult pdfContent = null;
        // PDF產生結果
        try {
            // Process PDF Content
            pdfContent = testProcessPDFContent(request, templateName);
            // Process PDF
            AjaxFormResult result = (AjaxFormResult) pdfService.processPdf(pdfContent.getByteArray(), PDF_OUT_PATH, PDF_NAME, PDF_PASS_WORD, DEFAULT_FONT);
            assertEquals(true, result.get("isSuccess"));
            result = (AjaxFormResult) pdfService.processPdf(null, "", "", "", DEFAULT_FONT);
            assertEquals(false, result.get("isSuccess"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMergePDFFiles() {
        String[] filesPath = { PDF_PATH1, PDF_PATH2 };
        String[] failFilesPath = { PDF_PATH1, "" };
        try {
            boolean isSuccess = pdfService.mergePdfFiles(filesPath, PDF_OUT_PATH, MERGE_PDF_NAME);
            assertTrue(isSuccess);
            isSuccess = pdfService.mergePdfFiles(failFilesPath, PDF_OUT_PATH, MERGE_PDF_NAME);
            assertFalse(isSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPartitionPdfFile() {
        int partPDFStartPage = 2;
        try {
            boolean isSuccess = pdfService.partitionPdfFile(PDF_PATH2, PDF_OUT_PATH + File.separator + PDF_OUT_PATH, partPDFStartPage);
            assertTrue(isSuccess);
            isSuccess = pdfService.partitionPdfFile("", PDF_OUT_PATH, partPDFStartPage);
            assertFalse(isSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddWatermark() {
        Float opacity = 0.7f;// 透明度0.7
        int rotationDegree = 15;// 15度角
        int textWMRepeatNum = 4;// 文字浮水印重複次數
        URL font = null;
        float fontSize = 24;
        String outFilePath = PDF_OUT_PATH + File.separator + PART_PDF_FILE_NAME;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            font = classLoader.getResource(TEST_FONT_PATH);
            boolean isSuccess = pdfService.addWatermark(PDF_PATH1, outFilePath, TEST_TEXT_WM, "", BaseFont.createFont(font.getPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), fontSize, opacity,
                    rotationDegree, textWMRepeatNum);
            assertTrue(isSuccess);
            isSuccess = pdfService.addWatermark(PDF_PATH1, outFilePath, "", "", null, 0, opacity, rotationDegree, 0);
            assertFalse(isSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ByteArrayDownloadResult testProcessPDFContent(Request request, String templateName) {
        ClassLoader classLoader = getClass().getClassLoader();
        // 給PDF值
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("custName", TEST_CUST_NAME);
        dataMap.put("idNo", TEST_ID_NO);
        dataMap.put("mPhone", TEST_MPHONE);
        dataMap.put("applyDate", CapDate.getCurrentDate(TEST_DATE_FORMAT));
        dataMap.put("onlineSign", TEST_SIGN_WORDING);
        // Process PDF Content
        final String PDF_IMG_PATH_KEY = "imgPath";
        URL pdfImgPath = classLoader.getResource(TEST_PDF_IMG_FILE_LOCATION);
        dataMap.put(PDF_IMG_PATH_KEY, pdfImgPath);
        String logData = "";
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            dataMap.put(entry.getKey(), CapString.trimNull(entry.getValue()));
            logData = logData.concat(entry.getKey() + "=" + MapUtils.getString(dataMap, entry.getKey(), "") + " , ");
        }
        // Process Templete
        ByteArrayDownloadResult pdfContent = null;
        ByteArrayOutputStream out = null;
        OutputStreamWriter wr = null;
        FileInputStream is = null;
        Writer writer = null;
        try {
            if (templateName != null) {
                FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
                factory.setTemplateLoaderPath(TEST_FREEMARK_DIR);
                factory.setDefaultEncoding(DEFAULT_ENCORDING);
                FreeMarkerConfigurer config = new FreeMarkerConfigurer();
                config.setConfiguration(factory.createConfiguration());
                Template t = config.getConfiguration().getTemplate(templateName);
                out = new ByteArrayOutputStream();
                wr = new OutputStreamWriter(out, DEFAULT_ENCORDING);
                writer = new BufferedWriter(wr);
                t.process(dataMap, writer);
            }
            pdfContent = new ByteArrayDownloadResult(request, out.toByteArray(), ContextTypeEnum.text.toString());
        } catch (Exception e) {
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(wr);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }
        return pdfContent;
    }

}
