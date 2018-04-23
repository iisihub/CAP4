package com.iisigroup.cap.base.support;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iisigroup.cap.base.model.Remind;

/**
 * <pre>
 * RemindRowMapper
 * </pre>
 * @since  2018年4月23日
 * @author bob peng
 * @version <ul>
 *           <li>2018年4月23日,bob peng,new
 *          </ul>
 */
public class RemindRowMapper implements RowMapper<Remind> {
    @Override
    public Remind mapRow(ResultSet rs, int rowNum) throws SQLException {
        Remind item = new Remind();
        item.setOid(rs.getString("OID"));
        item.setContent(rs.getString("CONTENT"));
        item.setScopeTyp(rs.getString("SCOPETYP"));
        item.setScopePid(rs.getString("SCOPEPID"));
        item.setStartDate(rs.getTimestamp("STARTDATE"));
        item.setEndDate(rs.getTimestamp("ENDDATE"));
        item.setCrTime(rs.getTimestamp("CRTIME"));
        item.setLocale(rs.getString("LOCALE"));
        item.setUpdater(rs.getString("UPDATER"));
        item.setUpdTime(rs.getTimestamp("UPDTIME"));
        return item;
    }
}
