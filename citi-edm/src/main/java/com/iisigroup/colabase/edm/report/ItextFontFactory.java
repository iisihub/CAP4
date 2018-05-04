/*
 * ItextFontFactory.java
 *
 * Copyright (c) 2009-2013 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.edm.report;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapString;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;

/**
 * <pre>
 * FreeMarker report of page report
 * </pre>
 *
 * @since 2014/2/13
 * @author tammy
 * @version <ul>
 *          <li>2014/2/13,tammy,new
 *          <li>2015/9/10,Tim,因was會咬住字形檔,所以basePath搬到D槽的目錄下,getFontPath改為直接回傳路徑
 *          </ul>
 */
public class ItextFontFactory {

	protected static Log logger = LogFactory.getLog(ItextFontFactory.class);

	private String basePath;

	/**
	 * @param basePath
	 *            the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getBasePath() {
		return this.basePath;
	}

	public Font getFont(String fontname, String fontType, String encoding,
			boolean embedded, float size, int style, BaseColor color) {
		try {
			return FontFactory.getFont(getFontPath(fontname, fontType),
					encoding, embedded, size, style, color);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return new Font();
	}

	public Font getFont(String fontname, String fontType, String encoding,
			boolean embedded, float size) {
		return getFont(fontname, fontType, encoding, embedded, size,
				Font.UNDEFINED, null);
	}

	public String getFontPath(String fontname, String fontType)
			throws IOException {
		File font = new File(basePath + fontname);
		if(font.exists() && font.canRead()){
			return basePath + fontname + (CapString.isEmpty(fontType) ? "" : "," + fontType);
		}else{
		return CapAppContext.getResource(basePath + fontname).getURI()
				.getPath()
				+ (CapString.isEmpty(fontType) ? "" : "," + fontType);
		}
	}

}
