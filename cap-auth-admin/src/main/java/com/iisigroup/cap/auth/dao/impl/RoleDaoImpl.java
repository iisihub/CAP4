/*
 * RoleDaoImpl.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.auth.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.iisigroup.cap.auth.dao.RoleDao;
import com.iisigroup.cap.auth.model.DefaultRole;
import com.iisigroup.cap.auth.support.RoleRowMapper;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;
import com.iisigroup.cap.db.model.Page;
import com.iisigroup.cap.security.model.Role;

/**
 * <pre>
 * 角色者資訊Dao
 * </pre>
 * 
 * @since 2013/12/20
 * @author tammy
 * @version
 *          <ul>
 *          <li>2013/12/20,tammy,new
 *          </ul>
 */
@Repository
public class RoleDaoImpl extends GenericDaoImpl<DefaultRole> implements RoleDao {

    @Override
    public List<Role> findBySysTypeAndPath(String sysType, String path) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("sysType", sysType);
        param.put("path", path);
        return getNamedJdbcTemplate().query("role_findBySysTypeAndPath", "", param, new RoleRowMapper());
    }

    @Override
    public List<DefaultRole> findAll() {
        SearchSetting search = createSearchTemplete();
        search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
        search.addOrderBy("code");
        List<DefaultRole> list = find(search);
        return list;
    }

    @Override
    public DefaultRole findByCode(String code) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "code", code);
        return findUniqueOrNone(search);
    }

    @Override
    public List<Map<String, Object>> findAllWithSelectedByUserCode(String userCode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userCode", userCode);
        return getNamedJdbcTemplate().query("role_findAllWithSelectedByUserCode", params);
    }

    @Override
    public Page<Map<String, Object>> findPageUnselectedBySysTypeAndFuncCode(String sysType, String funcCode, int firstResult, int maxResults) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("sysType", sysType);
        param.put("funcCode", funcCode);
        return getNamedJdbcTemplate().queryForPage("role_findUnSelectedRoleByFuncCode", param, firstResult, maxResults);
    }

    @Override
    public Page<Map<String, Object>> findPageBySysTypeAndFuncCode(String sysType, String funcCode, int firstResult, int maxResults) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("sysType", sysType);
        param.put("funcCode", funcCode);
        return getNamedJdbcTemplate().queryForPage("role_findRoleByFuncCode", param, firstResult, maxResults);
    }
}
