/* 
 * SequenceDao.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.dao;

import java.util.Map;

import com.iisigroup.cap.base.model.Sequence;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.db.dao.GenericDao;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.model.Page;

public interface SequenceDao extends GenericDao<Sequence> {
    Page<Map<String, Object>> findForSequencePage(SearchSetting search, Request params);

    void createFromMap(Map<String, Object> map);

    int updateByNodeAndNextSeqFromMap(Map<String, Object> map);

    Sequence findBySeqNode(String seqNode);

}
