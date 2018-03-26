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

    public TransLog findByPrintSeq(String seq) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "printSeq", seq);
        search.addOrderBy("transDate", true);
        return findUniqueOrNone(search);
    }

    public int countInTransLogToday(String idHash) {
        // String today = CapDate.getCurrentDate("yyyy-MM-dd");
        Timestamp todayTs = CapDate.getCurrentTimestamp();
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "idHash", idHash);
        search.addSearchModeParameters(SearchMode.EQUALS, "status", "0000");
        // search.addSearchModeParameters(SearchMode.BETWEEN, "transDate",
        // new Date[] { CapDate.getFirstMinuteDate(today, "yyyy-MM-dd"),
        // CapDate.getLastMinuteDate(today, "yyyy-MM-dd") });
        search.addSearchModeParameters(SearchMode.BETWEEN, "transDate", new Date[] { CapDate.shiftDays(todayTs, -1), todayTs });
        search.addOrderBy("transDate", true);
        return count(search);
    }

}