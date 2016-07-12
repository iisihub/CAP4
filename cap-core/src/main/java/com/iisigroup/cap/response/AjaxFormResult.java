/*
 * CapAjaxFormResult.java
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * Form Result
 * </pre>
 * 
 * @since 2010/7/21
 * @author iristu
 * @version <ul>
 *          <li>2010/7/21,iristu,new
 *          <li>2011/2/14,RodesChen,增加add判斷
 *          <li>2011/8/25,RodesChen,change CapAjaxFormResult
 *          putAll(Map<String,Object> map) to CapAjaxFormResult
 *          putAll(Map<String, ? extends Object> map)
 *          <li>2011/11/1,rodeschen,from cap
 *          <li>2013/03/29,rodeschen,field change to protected
 *          </ul>
 */
@SuppressWarnings("serial")
public class AjaxFormResult implements IResult {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected JSONObject resultMap;

	protected String contentType;
	protected String encoding;

	/**
	 * 建構子
	 */
	public AjaxFormResult() {
		resultMap = new JSONObject();
	}

	/**
	 * 建構子
	 * 
	 * @param obj
	 *            Object
	 */
	public AjaxFormResult(Object obj) {
		resultMap = JSONObject.fromObject(obj);
	}

	/**
	 * 放到畫面
	 * 
	 * @param key
	 *            String
	 * @param val
	 *            String
	 * @return this FormResult
	 */
	public AjaxFormResult set(String key, String val) {
		resultMap.put(key, val);
		return this;
	}

	/**
	 * 放到畫面
	 * 
	 * @param key
	 *            String
	 * @param val
	 *            List<String>
	 * @return this FormResult
	 */
	public AjaxFormResult set(String key, List<String> val) {
		resultMap.put(key, val);
		return this;
	}

	/**
	 * 放到畫面
	 * 
	 * @param key
	 *            String
	 * @param val
	 *            Date
	 * @return this FormResult
	 */
	public AjaxFormResult set(String key, Date val) {
		resultMap.put(key, val);
		return this;
	}

	/**
	 * 放到畫面
	 * 
	 * @param key
	 *            String
	 * @param val
	 *            Integer
	 * @return this FormResult
	 */
	public AjaxFormResult set(String key, Integer val) {
		resultMap.put(key, val);
		return this;
	}

	/**
	 * 放到畫面
	 * 
	 * @param key
	 *            String
	 * @param val
	 *            Integer
	 * @return this FormResult
	 */
	public AjaxFormResult set(String key, BigDecimal val) {
		resultMap.put(key, val.toString());
		return this;
	}

	/**
	 * 放到畫面
	 * 
	 * @param key
	 *            String
	 * @param val
	 *            Boolean
	 * @return this FormResult
	 */
	public AjaxFormResult set(String key, Boolean val) {
		resultMap.put(key, val);
		return this;
	}

	/**
	 * 放到畫面
	 * 
	 * @param key
	 *            String
	 * @param val
	 *            Timestamp
	 * @return this FormResult
	 */
	public AjaxFormResult set(String key, Timestamp val) {
		resultMap.put(key, val);
		return this;
	}

	/**
	 * 放到畫面
	 * 
	 * @param key
	 *            String
	 * @param val
	 *            FormResult
	 * @return this FormResult
	 */
	public AjaxFormResult set(String key, AjaxFormResult val) {
		resultMap.put(key, val.toString());
		return this;
	}

	public AjaxFormResult putAll(AjaxFormResult val) {
		resultMap.putAll(val.resultMap);
		return this;
	}

	public AjaxFormResult putAll(Map<String, ? extends Object> map) {
		resultMap.putAll(map);
		return this;
	}

	/**
	 * 比對是否存在此key
	 * 
	 * @param key
	 *            key
	 * @return boolean
	 */
	public boolean containsKey(String key) {
		return resultMap.containsKey(key);
	}

	/**
	 * to String
	 * 
	 * @return Json String
	 */
	public String toString() {
		return resultMap.toString();
	}

	public boolean isEmpty() {
		return resultMap.isEmpty();
	}

	/**
	 * 移除欄位
	 * 
	 * @param key
	 *            String
	 * @return this FormResult
	 */
	public AjaxFormResult removeField(String key) {
		if (resultMap.containsKey(key))
			resultMap.remove(key);
		return this;
	}

	/**
	 * 清空Field
	 * 
	 * @return this FormResult
	 */
	public AjaxFormResult clearResult() {
		resultMap.clear();
		return this;
	}

	/**
	 * put FormResult Map to FormResult
	 * 
	 * @param m
	 *            Map<String, FormResult>
	 */
	public void setResultMap(Map<String, AjaxFormResult> m) {
		for (String key : m.keySet()) {
			AjaxFormResult form = m.get(key);
			resultMap.put(key, form.toString());
		}
	}// ;

	/**
	 * 取值
	 * 
	 * @param key
	 *            the key
	 * @return Object
	 */
	public Object get(String key) {
		return resultMap.get(key);
	}

	@Override
	public String getResult() {
		return resultMap.toString();
	}

	@Override
	public String getLogMessage() {
		return getResult();
	}

	@Override
	public void add(IResult result) {
		if (result != null) {
			JSONObject json = JSONObject.fromObject(result.getResult());
			resultMap.putAll(json);
		}
	}

	@Override
	public String getContextType() {
		if (contentType != null) {
			return this.contentType;
		} else {
			return "text/plain";
		}
	}

	@Override
	public String getEncoding() {
		if (encoding != null) {
			return this.encoding;
		} else {
			return CharEncoding.UTF_8;
		}
	}

	@Override
	public void setContextType(String cxtType) {
		this.contentType = cxtType;
	}

	@Override
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public void respondResult(ServletResponse response) {
		new StringResponse(getContextType(), getEncoding(), getResult())
				.respond(response);
	}// ;

}
