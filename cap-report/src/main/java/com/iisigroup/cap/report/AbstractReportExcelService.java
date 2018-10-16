/* 
 * AbstractReportExcelService.java
 * 
 * Copyright (c) 2011 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.google.gson.JsonObject;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapSystemConfig;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * <pre>
 * The base excel report service.
 * </pre>
 * 
 * @since 2015/6/2
 * @author Lancelot
 * @version
 *          <ul>
 *          <li>2015/6/2,Lancelot,new
 *          </ul>
 */
public abstract class AbstractReportExcelService implements ReportService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final static String REPORT_SUFFIX = ".xls";
    private final static String POSITION_SUFFIX = "XY";
    private final static String OFFSET_SUFFIX = "Offset";
    @Resource
    private FreeMarkerConfigurer fmConfg;
    @Resource
    private CapSystemConfig sysConfig;
    protected WritableFont defaultFont = new WritableFont(WritableFont.createFont("標楷體"), 12);

    private Workbook readTemplate() {
        Workbook w = null;
        try (InputStream inputWorkbook = getClass().getClassLoader().getResourceAsStream("/ftl/" + getReportDefinition() + REPORT_SUFFIX);) {
            w = Workbook.getWorkbook(inputWorkbook);
        } catch (BiffException e) {
            logger.trace(e.getMessage(), e);
        } catch (IOException e) {
            logger.trace(e.getMessage(), e);
        }
        return w;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.report.ReportService#generateReport(com.iisigroup.cap.component.Request)
     */
    @SuppressWarnings("unchecked")
    @Override
    public ByteArrayOutputStream generateReport(Request request) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook t = null;
        try {
            t = readTemplate();
            // Define the cell format
            // 設定千分位
            NumberFormat nf = new NumberFormat("#,##0_");
            // 設定小數格式
            NumberFormat nf2 = new NumberFormat("#,##0.00");
            // 設定字形和字體大小
            WritableFont titleWf = new WritableFont(WritableFont.createFont("標楷體"), 12);
            WritableCellFormat timesString = new WritableCellFormat(defaultFont);
            WritableCellFormat timesNumber = new WritableCellFormat(nf);
            WritableCellFormat timesDouble = new WritableCellFormat(nf2);
            timesNumber.setFont(titleWf);
            timesDouble.setFont(titleWf);
            // 水平置中
            timesNumber.setAlignment(Alignment.RIGHT);
            timesDouble.setAlignment(Alignment.CENTRE);
            timesString.setAlignment(Alignment.CENTRE);
            // 垂直置中
            timesNumber.setVerticalAlignment(VerticalAlignment.CENTRE);
            timesDouble.setVerticalAlignment(VerticalAlignment.CENTRE);
            timesString.setVerticalAlignment(VerticalAlignment.CENTRE);
            // 表格框線
            timesNumber.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            timesDouble.setBorder(Border.BOTTOM, BorderLineStyle.THIN, Colour.BLACK);
            // Lets automatically wrap the cells
            timesNumber.setWrap(true);
            timesDouble.setWrap(true);
            timesString.setWrap(true);
            Map<String, Object> reportData = execute(request);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setRationalization(false);
            wbSettings.setLocale(new Locale("zh", "TW"));
            WritableWorkbook workbook = Workbook.createWorkbook(out, t, wbSettings);
            WritableSheet sheet = workbook.getSheet(0);
            // 判斷是否為純數字
            for (Entry<String, Object> entry : reportData.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (!key.endsWith(OFFSET_SUFFIX) && !key.endsWith(POSITION_SUFFIX)) {
                    // it's data
                    int[] pos = (int[]) reportData.get(key + POSITION_SUFFIX);
                    if (pos != null) {
                        int x = pos[0];
                        int y = pos[1];
                        if (value instanceof List) {
                            // grid data
                            JsonObject offset = (JsonObject) reportData.get(key + OFFSET_SUFFIX);
                            List<Map<String, Object>> l = (List<Map<String, Object>>) value;
                            for (Map<String, Object> m : l) {
                                for (Entry<String, Object> e : m.entrySet()) {
                                    if (offset.get(e.getKey()) != null) {
                                        String cellVal = "";
                                        if (e.getValue() != null) {
                                            if (e.getValue() instanceof Timestamp) {
                                                cellVal = CapDate.convertDateTimeFromF1ToF2(CapDate.getDateTimeFormat((Timestamp) e.getValue()), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
                                            } else {
                                                cellVal = e.getValue().toString();
                                            }
                                        }
                                        Label label = new Label(x + offset.get(e.getKey()).getAsInt(), y, cellVal, timesString);
                                        sheet.addCell(label);
                                    }
                                }
                                y++;
                            }
                        } else {
                            if (value instanceof BigDecimal) {

                                jxl.write.Number number = new jxl.write.Number(x, y, ((BigDecimal) value).doubleValue(), timesNumber);

                                // 判斷是否有小數點
                                if (!((BigDecimal) value).divideAndRemainder(new BigDecimal(2))[1].equals(BigDecimal.ZERO)
                                        && !((BigDecimal) value).divideAndRemainder(new BigDecimal(2))[1].equals(BigDecimal.ONE)) {
                                    number = new jxl.write.Number(x, y, ((BigDecimal) value).doubleValue(), timesDouble);
                                }

                                sheet.addCell(number);
                            } else if (value instanceof Integer) {
                                jxl.write.Number number = new jxl.write.Number(x, y, (Integer) value, timesNumber);
                                sheet.addCell(number);
                            } else {
                                Label label = new Label(x, y, (String) value, timesString);
                                sheet.addCell(label);
                            }
                        }
                    }
                }
            }
            workbook.write();
            workbook.close();
            IOUtils.closeQuietly(out);
        } catch (Exception e) {
            logger.trace(e.getMessage(), e);
            if (e.getCause() != null) {
                throw new CapException(e.getCause(), e.getClass());
            } else {
                throw new CapException(e, e.getClass());
            }
        } finally {
            if (t != null) {
                t.close();
            }
        }
        return out;
    }

    public FreeMarkerConfigurer getFmConfg() {
        return fmConfg;
    }

    public CapSystemConfig getSysConfig() {
        return sysConfig;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.report.ReportService#isWriteToFile()
     */
    @Override
    public boolean isWriteToFile() {
        return false;
    }
}
