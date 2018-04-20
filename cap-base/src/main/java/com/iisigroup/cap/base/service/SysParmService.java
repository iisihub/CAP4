package com.iisigroup.cap.base.service;

import com.iisigroup.cap.base.model.SysParm;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.model.Page;

/**
 * <pre>
 * SysParmService
 * </pre>
 *
 * @since 2018/04/23
 * @author bobpeng
 * @version
 *          <ul>
 *          <li>2018/04/23,bobpeng,new
 *          </ul>
 */
public interface SysParmService {
    /**
     * query data for grid
     * 
     * @param search
     * @param params
     * @return
     */
    Page<SysParm> findPage(SearchSetting search, Request params);

    /**
     * add SysParm
     * 
     * @param parmId
     * @param parmValue
     * @param parmDesc
     */
    void addSysParm(String parmId, String parmValue, String parmDesc);

    /**
     * modify SysParm
     * 
     * @param parmId
     * @param parmValue
     * @param parmDesc
     */
    void modifySysParm(String parmId, String parmValue, String parmDesc);

    /**
     * delete SysParm By ParmId
     * 
     * @param parmId
     */
    void deleteSysParmByParmId(String parmId);

}
