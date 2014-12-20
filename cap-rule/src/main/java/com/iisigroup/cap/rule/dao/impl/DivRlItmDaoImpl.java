/*
 * Copyright (c) 2009-2012 International Integrated System, Inc. 
 * 11F, No.133, Sec.4, Minsheng E. Rd., Taipei, 10574, Taiwan, R.O.C.
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */

package com.iisigroup.cap.rule.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.iisigroup.cap.dao.impl.GenericDao;
import com.iisigroup.cap.dao.utils.ISearch;
import com.iisigroup.cap.dao.utils.SearchMode;
import com.iisigroup.cap.rule.dao.DivRlItmDao;
import com.iisigroup.cap.rule.model.DivRlItm;

/**
 * <pre>
 * Division Rule Item DAO Impl
 * </pre>
 * 
 * @since 2013/12/13
 * @author TimChiang
 * @version <ul>
 *          <li>2013/12/13,TimChiang,new
 *          </ul>
 */
@Repository
public class DivRlItmDaoImpl extends GenericDao<DivRlItm> implements DivRlItmDao {

	@Override
	public DivRlItm findByDivRlNo(String divRlNo) {
		ISearch search = createSearchTemplete();
		search.addSearchModeParameters(SearchMode.EQUALS, "divRlNo", divRlNo);
		return findUniqueOrNone(search);
	}

	@Override
	public DivRlItm findByDivRlNoAndInputFlg(String divRlNo, String inputFlag) {
		ISearch search = createSearchTemplete();
		search.addSearchModeParameters(SearchMode.EQUALS, "divRlNo", divRlNo);
		search.addSearchModeParameters(SearchMode.EQUALS, "inputFlag", inputFlag);
		search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
		return findUniqueOrNone(search);
	}

	@Override
	public List<DivRlItm> findByDivRlNo(String[] divRlNos) {
		ISearch search = createSearchTemplete();
		search.addSearchModeParameters(SearchMode.EQUALS, "divRlNo", divRlNos);
		search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
		search.addOrderBy("codeOrder");
		return find(search);
	}

	@Override
	public List<DivRlItm> findByDivRlNoAndInputFlg(String[] divRlNos,
			String inputFlag) {
		ISearch search = createSearchTemplete();
		search.addSearchModeParameters(SearchMode.EQUALS, "divRlNo", divRlNos);
		search.addSearchModeParameters(SearchMode.EQUALS, "inputFlag", inputFlag);
		return find(search);
	}

	@Override
	public DivRlItm findByOid(String oid) {
		return find(oid);
	}

}