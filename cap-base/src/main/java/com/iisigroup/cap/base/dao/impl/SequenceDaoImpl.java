/* 
 * SequenceDaoImpl.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.iisigroup.cap.base.dao.SequenceDao;
import com.iisigroup.cap.base.model.Sequence;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;
import com.iisigroup.cap.db.model.Page;

@Repository
public class SequenceDaoImpl extends GenericDaoImpl<Sequence> implements SequenceDao {

    @Override
    public Page<Map<String, Object>> findForSequencePage(SearchSetting search, Request params) {
        return getNamedJdbcTemplate().queryForPage("sequence_findForSequencePage", search);
    }

    @Override
    public void createFromMap(Map<String, Object> map) {
        getNamedJdbcTemplate().update("sequence_createFromMap", map);
    }

    @Override
    public int updateByNodeAndNextSeqFromMap(Map<String, Object> map) {
        return getNamedJdbcTemplate().update("sequence_updateByNodeAndNextSeqFromMap", map);
    }

    @Override
    public Sequence findBySeqNode(String seqNode) {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("seqNode", seqNode);
        return getNamedJdbcTemplate().queryForObject("sequence_findBySeqNode", args, new RowMapper<Sequence>() {
            @Override
            public Sequence mapRow(ResultSet rs, int rowNum) throws SQLException {
                Sequence seq = new Sequence();
                seq.setSeqNode(rs.getString("SEQNODE"));
                seq.setNextSeq(rs.getInt("NEXTSEQ"));
                seq.setRounds(rs.getInt("ROUNDS"));
                seq.setUpdateTime(rs.getTimestamp("UPDATETIME"));
                return seq;
            }
        });
    }

}
