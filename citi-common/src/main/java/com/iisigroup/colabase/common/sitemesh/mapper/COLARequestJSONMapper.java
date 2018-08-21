/*
 * CCRequestJSONMapper.java
 *
 * Copyright (c) 2009-2014 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.common.sitemesh.mapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.common.constants.COLAConstants;
import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;

import net.sf.json.JSONSerializer;

/**
 * <pre>
 * Get req in json format from session object.
 * </pre>
 *
 * @since 2014/5/26
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2014/5/26,Sunkist Wang,new
 *          <li>2014/6/3,Sunkist Wang,fix ConcurrentModificationException problem
 *          <li>2014/7/10,Sunkist Wang,fix cause lsa redirect 302 , resolved the lost params. problem
 *          </ul>
 */
public class COLARequestJSONMapper extends AbstractDecoratorMapper {

    private final static String PROP_KEY = "reqJSON";
    private String ignorePathReg;
    private Set<String> ignoreParams;
    private Set<String> keepInSessions;
    private Set<String> decoratorFile;

    @Override
    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        ignorePathReg = properties.getProperty("ignorePathReg");
        String decorator = properties.getProperty("decoratorFile");
        if (!CapString.isEmpty(decorator)) {
            decoratorFile = new HashSet<String>();
            decoratorFile.addAll(Arrays.asList(decorator.split(",")));
        }
        String params = properties.getProperty("ignoreParams");
        if (!CapString.isEmpty(params)) {
            ignoreParams = new HashSet<String>();
            ignoreParams.addAll(Arrays.asList(params.split(",")));
        }
        String keeps = properties.getProperty("keepInSessions");
        if (!CapString.isEmpty(keeps)) {
            keepInSessions = new HashSet<String>();
            keepInSessions.addAll(Arrays.asList(keeps.split(",")));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Decorator getDecorator(HttpServletRequest request, Page page) {
        if ((decoratorFile == null || decoratorFile.contains(page.getProperties().get("meta.decorator")))
                && (ignorePathReg == null || !CapString.checkRegularMatch(CapString.trimNull(request.getPathInfo()), ignorePathReg))) {

            HttpSession session = request.getSession(false);
            Object data = session.getAttribute(COLAConstants.ATTR_REDIRECT);

            HashMap<String, Object> hm = new HashMap<String, Object>();
            if (data != null && data instanceof HashMap) {
                HashMap<String, Object> sessionMap = (HashMap<String, Object>) data;
                for (Entry<String, Object> field : sessionMap.entrySet()) {
                    if (!ignoreParams.contains(field.getKey())) {
                        hm.put(field.getKey(), field.getValue());
                    }
                }
            }
            StringBuffer str = new StringBuffer("<script type=\"text/javascript\">var reqJSON=");
            str.append(JSONSerializer.toJSON(hm).toString()).append(";</script>");
            page.addProperty(PROP_KEY, str.toString());

            if (CapString.checkRegularMatch(CapString.trimNull(request.getQueryString()), COLAConstants.ATTR_REDIRECT)) {
                HashMap<String, Object> hm1 = new HashMap<String, Object>();
                for (Entry<String, Object> field : hm.entrySet()) {
                    if (keepInSessions.contains(field.getKey())) {
                        hm1.put(field.getKey(), field.getValue());
                    }
                }

                if (session != null) {
                    session.setAttribute(COLAConstants.ATTR_REDIRECT, hm1);
                }
            }
        }
        return super.getDecorator(request, page);
    }
}
