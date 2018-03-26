package com.iisigroup.colabase.va.dao.impl;

import org.springframework.stereotype.Repository;

import com.iisigroup.colabase.common.dao.MOMJpaDao;
import com.iisigroup.colabase.va.dao.ICAInfoDao;
import com.iisigroup.colabase.va.model.CAInfo;

@Repository("caInfoDao")
public class CAInfoDaoImpl extends MOMJpaDao<CAInfo> implements ICAInfoDao {

}
