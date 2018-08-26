/*
 * DepartmentService.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.auth.service;

import java.util.List;

import com.iisigroup.cap.auth.model.Department;

/**
 * <pre>
 * 分行代碼維護
 * </pre>
 * 
 * @since 2012/2/17
 * @author UFOJ
 * @version
 *          <ul>
 *          <li>2012/2/17,UFOJ,new
 *          </ul>
 */
public interface DepartmentService {

    /**
     * <pre>
     * 新增或修改
     * </pre>
     * 
     * @param entry
     *            Branch
     */
    void save(Department entry);

    /**
     * 查詢分行
     * 
     * @param brno
     *            分行代碼
     * @return {@link com.iisigroup.cap.auth.model.Department}
     */
    Department findByBrno(String brNo);

    /**
     * 取得所有的所分行(未cache)
     * 
     * @return 所有分行清單
     */
    List<Department> findByAllBranch();
}
