package com.iisigroup.cap.auth.dao;

import java.util.Map;

import com.iisigroup.cap.auth.model.User;
import com.iisigroup.cap.dao.IGenericDao;
import com.iisigroup.cap.model.Page;

public interface UserDao extends IGenericDao<User> {
    User findByCode(String userId);

    Page<Map<String, Object>> findPage(String userId, String userName,
            String[] roleOids, String[] status, int maxResults, int firstResult);

    Page<Map<String, Object>> findPageByRoleCode(String roleCode,
            int firstResult, int maxResults);

    Page<Map<String, Object>> findPageUnselectedByRoleCodeAndDepCode(
            String roleCode, String depCode, int firstResult, int maxResults);

    void processUserStatus(int pwdExpiredDay, int pwdAccountDisable,
            int pwdAccountDelete);

}
