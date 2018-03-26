package com.iisigroup.colabase.va.dao;

import com.iisigroup.colabase.va.model.TransLog;
import com.iisigroup.cap.db.dao.GenericDao;

public interface ITransLogDao extends GenericDao<TransLog> {

    TransLog findByPrintSeq(String seq);

    int countInTransLogToday(String idHash);
}
