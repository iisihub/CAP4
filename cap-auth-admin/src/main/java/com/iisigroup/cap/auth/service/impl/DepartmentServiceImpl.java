/*
 * DepartmentServiceImpl.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.auth.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.iisigroup.cap.auth.dao.DepartmentDao;
import com.iisigroup.cap.auth.model.Department;
import com.iisigroup.cap.auth.service.DepartmentService;

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
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentDao departmentDao;

    @Override
    public void save(Department model) {
        departmentDao.save(model);
    }

    @Override
    public Department findByBrno(String brNo) {
        return departmentDao.findByCode(brNo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mega.eloan.adm.service.ADM2050Service#findByAllBranch()
     */
    @Override
    public List<Department> findByAllBranch() {
        return departmentDao.findByAllActDepartment();
    }
}
