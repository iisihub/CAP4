/* 
 * LocaleChangeInterceptor.java
 * 
 * Copyright (c) 2009-2011 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.mvc.Interceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.support.RequestContextUtils;

import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.security.model.CapUserDetails;
import com.iisigroup.cap.utils.CapWebUtil;

/**
 * <pre>
 * 切換語系動作
 * </pre>
 * 
 * @since 2011/11/30
 * @author rodeschen
 * @version <ul>
 *          <li>2011/11/30,rodeschen,new
 *          <li>2013/12/30,tammy,以RequestContextUtils.getLocale(request)取得
 *          </ul>
 */
public class LocaleChangeInterceptor extends
		org.springframework.web.servlet.i18n.LocaleChangeInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler)
			throws ServletException {
		String newLocale = request.getParameter(getParamName());
		if (newLocale != null) {
			super.preHandle(request, response, handler);
			CapUserDetails user = CapSecurityContext.getUser();
			user.setLocale(RequestContextUtils.getLocale(request));
			request.getSession(false).setAttribute(CapWebUtil.localeKey,
					RequestContextUtils.getLocale(request));
		}
		return true;
	}

}
