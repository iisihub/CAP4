package com.iisigroup.cap.auth.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.iisigroup.cap.auth.dao.UserRoleDao;
import com.iisigroup.cap.auth.model.UserRole;
import com.iisigroup.cap.dao.impl.GenericDao;
import com.iisigroup.cap.dao.utils.ISearch;
import com.iisigroup.cap.dao.utils.SearchMode;

@Repository
public class UserRoleDaoImpl extends GenericDao<UserRole> implements
        UserRoleDao {

    @Override
    public int deleteByRoleCodeAndUserCodes(String roleCode, List<String> delUsr) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("roleCode", roleCode);
        param.put("delUsrs", delUsr);
        return getNamedJdbcTemplate().update("userRole_deleteUserRole", param);
    }

    @Override
    public UserRole findByUserCodeAndRoleCode(String userCode, String roleCode) {
        ISearch search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "userCode", userCode);
        search.addSearchModeParameters(SearchMode.EQUALS, "roleCode", roleCode);
        return findUniqueOrNone(search);
    }

    @Override
    public void deleteByUserCode(String userCode) {
        ISearch search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "userCode", userCode);
        delete(find(search));
    }
}
