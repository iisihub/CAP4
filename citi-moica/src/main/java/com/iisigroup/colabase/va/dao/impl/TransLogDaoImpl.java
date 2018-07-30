package com.iisigroup.colabase.va.dao.impl;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.stereotype.Repository;

import com.iisigroup.colabase.va.dao.ITransLogDao;
import com.iisigroup.colabase.va.model.TransLog;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.colabase.common.dao.MOMJpaDao;

@Repository("transLogDao")
public class TransLogDaoImpl extends MOMJpaDao<TransLog> implements ITransLogDao {

    private static final String TRANSDATE = "transDate";
    
    public TransLog findByPrintSeq(String seq) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "printSeq", seq);
        search.addOrderBy(TRANSDATE, true);
        return findUniqueOrNone(search);
    }

    public int countInTransLogToday(String idHash) {
        Timestamp todayTs = CapDate.getCurrentTimestamp();
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "idHash", idHash);
        search.addSearchModeParameters(SearchMode.EQUALS, "status", "0000");
        search.addSearchModeParameters(SearchMode.BETWEEN, TRANSDATE, new Date[] { CapDate.shiftDays(todayTs, -1), todayTs });
        search.addOrderBy(TRANSDATE, true);
        return count(search);
    }

}