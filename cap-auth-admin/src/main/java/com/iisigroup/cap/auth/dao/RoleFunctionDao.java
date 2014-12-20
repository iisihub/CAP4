package com.iisigroup.cap.auth.dao;

import java.util.List;

import com.iisigroup.cap.auth.model.RoleFunction;
import com.iisigroup.cap.dao.IGenericDao;

public interface RoleFunctionDao extends IGenericDao<RoleFunction> {
    int deleteByRoleCodeAndFuncCodes(String roleCode, List<String> delFunc);

    int deleteByFuncCodeAndRoleCodes(String funcCode, List<String> delRole);
}
