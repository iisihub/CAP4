/* 
 * DepartmentDao.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.auth.dao;

import java.util.List;

import com.iisigroup.cap.auth.model.Department;
import com.iisigroup.cap.db.dao.GenericDao;

/**
 * <pre>
 * 分行資訊DAO
 * </pre>
 * 
 * @since 2011/8/30
 * @author Fantasy
 * @version
 *          <ul>
 *          <li>2011/8/30,Fantasy,new
 *          </ul>
 */
public interface DepartmentDao extends GenericDao<Department> {

    /**
     * 取得所有單位資訊(含已停業)
     * 
     * @return List<Department>
     */
    List<Department> findByAllDepartment();

    /**
     * 取得所有單位資訊(不含已停業)
     * 
     * @return List<Department>
     */
    List<Department> findByAllActDepartment();

    /**
     * 查詢單位
     * 
     * @param code
     *            單位代碼
     * @return Department
     */
    Department findByCode(String code);

}
