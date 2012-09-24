/*
 * FileDownloadResult.java
 * 
 * Copyright (c) 2009-2012 International Integrated System, Inc.
 * 11F, No.133, Sec.4, Minsheng E. Rd., Taipei, 10574, Taiwan, R.O.C.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System,Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. ("Confidential Information").
 */
package com.iisigroup.cap.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iisigroup.cap.component.IRequest;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * File Download
 * </pre>
 * 
 * @since 2010/11/24
 * @author iristu
 * @version <ul>
 *          <li>iristu,2010/11/24,new
 *          <li>2011/11/1,rodeschen,from cap
 *          </ul>
 */
@SuppressWarnings("serial")
public class FileDownloadResult implements IResult {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected String _file;
	protected String _outputName;
	protected String _contentType;
	protected IRequest _request;
	private String encoding;

	public FileDownloadResult() {
		super();
	}

	public FileDownloadResult(IRequest request, String file, String contentType) {
		this(request, file, null, contentType);
	}

	public FileDownloadResult(IRequest request, String file, String outputName,
			String contentType) {
		this._request = request;
		this._file = file;
		this._outputName = CapString.isEmpty(outputName) ? FilenameUtils
				.getName(_file) : outputName;
		this._contentType = contentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tw.com.iisi.cap.response.IResult#getResult()
	 */
	@Override
	public String getResult() {
		return "";
	}// ;

	@Override
	public String getLogMessage() {
		return new StringBuffer("Download file:").append(_file).toString();
	}

	@Override
	public void add(IResult result) {
		if (result instanceof FileDownloadResult) {
			FileDownloadResult r = (FileDownloadResult) result;
			this._request = r._request;
			this._contentType = r._contentType;
			this._file = r._file;
			this._outputName = r._outputName;
		}
	}

	@Override
	public String getContextType() {
		return this._contentType;
	}

	@Override
	public String getEncoding() {
		return this.encoding;
	}

	@Override
	public void setContextType(String cxtType) {
		this._contentType = cxtType;
	}

	@Override
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public void respondResult(ServletResponse response) {
		InputStream in = null;
		try {
			if (_outputName != null && response instanceof HttpServletResponse) {
				((HttpServletResponse) response).setHeader(
						"Content-Disposition", "attachment;filename=\""
								+ _outputName + "\"");
			}
			OutputStream output = response.getOutputStream();
			File file = new File(_file);
			int length = -1;
			// Stream to the requester.
			byte[] bbuf = new byte[1024 * 1024];

			in = new FileInputStream(file);
			while ((in != null) && ((length = in.read(bbuf)) != -1)) {
				output.write(bbuf, 0, length);
			}
			output.flush();

		} catch (Exception e) {
			throw new CapException(e, getClass());
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.getMessage();
			}
		}

	}// ;

}
