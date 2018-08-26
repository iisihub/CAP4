/*
 * CodeTypeServiceImpl.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.iisigroup.cap.base.dao.CodeTypeDao;
import com.iisigroup.cap.base.model.CodeType;
import com.iisigroup.cap.base.service.CodeTypeService;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.model.Page;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.operation.simple.SimpleContextHolder;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.cap.utils.CapWebUtil;

/**
 * <pre>
 * CodeType Service
 * </pre>
 * 
 * @since 2011/11/28
 * @author rodeschen
 * @version
 *          <ul>
 *          <li>2011/11/28,rodeschen,new
 *          <li>2013/4/10,rodeschen,增加預設語系
 *          </ul>
 */
@Service
public class CodeTypeServiceImpl implements CodeTypeService {

    @Resource
    private CodeTypeDao dao;

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#addCodeType(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void addCodeType(String codeType, String codeValue, String codeDesc, Integer codeOrder, String locale) {
        CodeType codeTypeModel = dao.findByCodeTypeAndCodeValue(codeType, codeValue, locale);
        if (codeTypeModel != null) {
            throw new CapMessageException(CapAppContext.getMessage("js.codetype.0001"), getClass()); // 字典種類及值重復！
        }
        saveOrUpdateCodeType(new CodeType(), codeType, codeValue, codeDesc, codeOrder, locale);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#modifyCodeType(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void modifyCodeType(String oid, String codeType, String codeValue, String codeDesc, Integer codeOrder, String locale) {
        CodeType codeTypeModel = dao.findByCodeTypeAndCodeValue(codeType, codeValue, locale);
        if (codeTypeModel != null && !codeTypeModel.getOid().equals(oid)) {
            throw new CapMessageException(CapAppContext.getMessage("js.codetype.0001"), getClass()); // 字典種類及值重復！
        } else if (codeTypeModel == null) {
            codeTypeModel = dao.find(oid);
        }
        saveOrUpdateCodeType(codeTypeModel, codeType, codeValue, codeDesc, codeOrder, locale);
    }

    private void saveOrUpdateCodeType(CodeType codeTypeModel, String codeType, String codeValue, String codeDesc, Integer codeOrder, String locale) {
        codeTypeModel.setCodeType(codeType);
        codeTypeModel.setCodeValue(codeValue);
        codeTypeModel.setCodeDesc(codeDesc);
        codeTypeModel.setCodeOrder(codeOrder);
        codeTypeModel.setLocale(locale);
        codeTypeModel.setUpdater(CapSecurityContext.getUserId());
        codeTypeModel.setUpdateTime(CapDate.getCurrentTimestamp());
        dao.save(codeTypeModel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#findByCodeType(java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, String> findByCodeType(String codeType, String locale) {
        List<CodeType> codeList = dao.findByCodeType(codeType, locale);
        Map<String, String> m = new LinkedHashMap<String, String>();
        if (!codeList.isEmpty()) {
            for (CodeType c : codeList) {
                m.put(c.getCodeValue(), c.getCodeDesc());
            }
        }
        return m;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#findByCodeTypes(java.lang.String[], java.lang.String)
     */
    @Override
    public Map<String, Map<String, String>> findByCodeTypes(String[] types, String locale) {
        List<CodeType> codes = dao.findByCodeType(types, locale);
        Map<String, Map<String, String>> m = new LinkedHashMap<String, Map<String, String>>();
        if (!codes.isEmpty()) {
            for (int i = 0; i < types.length; i++) {
                Map<String, String> map = new LinkedHashMap<String, String>();
                for (CodeType c : codes) {
                    if (types[i].equals(c.getCodeType())) {
                        map.put(c.getCodeValue(), c.getCodeDesc());
                        m.put(types[i], map);
                    }
                }
            }
        }
        return m;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#getCodeTypeByTypes(java.lang.String[], java.lang.String)
     */
    @Override
    public Map<String, AjaxFormResult> getCodeTypeByTypes(String[] types, String locale) {
        List<CodeType> codes = dao.findByCodeType(types, locale);
        Map<String, AjaxFormResult> m = new LinkedHashMap<String, AjaxFormResult>();
        for (CodeType c : codes) {
            String type = c.getCodeType();
            AjaxFormResult sm = m.get(type);
            if (sm == null) {
                sm = new AjaxFormResult(true);
            }
            sm.set(c.getCodeValue(), c.getCodeDesc());
            m.put(type, sm);
        }
        return m;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#getByCodeTypeAndValue(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public CodeType getByCodeTypeAndValue(String type, String value, String locale) {
        return dao.findByCodeTypeAndCodeValue(type, value, locale);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#getById(java.lang.String)
     */
    @Override
    public CodeType getById(String oid) {
        return dao.find(oid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#deleteById(java.lang.String)
     */
    @Override
    public void deleteById(String oid) {
        CodeType codeType = dao.find(oid);
        if (codeType != null) {
            dao.delete(codeType);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#findByCodeType(java.lang .String)
     */
    @Override
    public Map<String, String> findByCodeType(String codeType) {
        return findByCodeType(codeType, SimpleContextHolder.get(CapWebUtil.localeKey).toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#findByCodeTypes(java.lang .String[])
     */
    @Override
    public Map<String, Map<String, String>> findByCodeTypes(String[] types) {
        return findByCodeTypes(types, SimpleContextHolder.get(CapWebUtil.localeKey).toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#getCodeTypeByTypes(java .lang.String[])
     */
    @Override
    public Map<String, AjaxFormResult> getCodeTypeByTypes(String[] types) {
        return getCodeTypeByTypes(types, SimpleContextHolder.get(CapWebUtil.localeKey).toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#getByCodeTypeAndValue( java.lang.String, java.lang.String)
     */
    @Override
    public CodeType getByCodeTypeAndValue(String type, String value) {
        return getByCodeTypeAndValue(type, value, SimpleContextHolder.get(CapWebUtil.localeKey).toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.CodeTypeService#findPage(com.iisigroup.cap.db.dao.SearchSetting, com.iisigroup.cap.component.Request)
     */
    @Override
    public Page<CodeType> findPage(SearchSetting search, Request params) {
        if (!CapString.isEmpty(params.get("locale"))) {
            search.addSearchModeParameters(SearchMode.EQUALS, "locale", params.get("locale"));
        }
        if (!CapString.isEmpty(params.get("codeType"))) {
            search.addSearchModeParameters(SearchMode.EQUALS, "codeType", params.get("codeType"));
        }
        if (!search.hasOrderBy()) {
            search.addOrderBy("codeType");
            search.addOrderBy("codeOrder");
        } else {
            Map<String, Boolean> m = search.getOrderBy();
            if (!m.containsKey("codeType")) {
                search.addOrderBy("codeType");
            }
            if (!m.containsKey("codeOrder")) {
                search.addOrderBy("codeOrder");
            }
        }
        return dao.findPage(CodeType.class, search);
    }

}
