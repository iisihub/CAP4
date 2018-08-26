/*
 * RemindsRowMapper.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.support;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.iisigroup.cap.base.model.Reminds;

/**
 * <pre>
 * RemindsRowMapper
 * </pre>
 * @since  2018年4月23日
 * @author bob peng
 * @version <ul>
 *           <li>2018年4月23日,bob peng,new
 *          </ul>
 */
public class RemindsRowMapper implements RowMapper<Reminds> {
    @Override
    public Reminds mapRow(ResultSet rs, int rowNum) throws SQLException {
        Reminds item = new Reminds();
        item.setOid(rs.getString("OID"));
        item.setPid(rs.getString("PID"));
        item.setScopePid(rs.getString("SCOPEPID"));
        item.setStyleTyp(rs.getString("STYLETYP"));
        item.setStyleClr(rs.getString("STYLECLR"));
        item.setStyle(rs.getBigDecimal("STYLE"));
        item.setUnit(rs.getBigDecimal("UNIT"));
        item.setYnFlag(rs.getString("YNFLAG"));
        return item;
    }
}
