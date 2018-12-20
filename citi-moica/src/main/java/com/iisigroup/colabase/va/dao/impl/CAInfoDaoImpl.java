package com.iisigroup.colabase.va.dao.impl;

import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;
import org.springframework.stereotype.Repository;

import com.iisigroup.colabase.va.dao.ICAInfoDao;
import com.iisigroup.colabase.va.model.CAInfo;

@Repository("caInfoDao")
public class CAInfoDaoImpl extends GenericDaoImpl<CAInfo> implements ICAInfoDao {

}
