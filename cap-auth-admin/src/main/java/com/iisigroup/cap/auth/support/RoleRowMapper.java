package com.iisigroup.cap.auth.support;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iisigroup.cap.auth.model.DefaultRole;
import com.iisigroup.cap.security.model.Role;

/**
 * <pre>
 * Role RowMapper
 * </pre>
 * @since  2018年4月23日
 * @author bob peng
 * @version <ul>
 *           <li>2018年4月23日,bob peng,new
 *          </ul>
 */
public class RoleRowMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        DefaultRole role = new DefaultRole();
        role.setOid(rs.getString("OID"));
        role.setCode(rs.getString("CODE"));
        role.setSysType(rs.getString("SYSTYPE"));
        role.setStatus(rs.getString("STATUS"));
        role.setName(rs.getString("NAME"));
        role.setDescription(rs.getString("DESCRIPTION"));
        role.setUpdater(rs.getString("UPDATER"));
        role.setUpdateTime(rs.getTimestamp("UPDATETIME"));
        return role;
    }

}
