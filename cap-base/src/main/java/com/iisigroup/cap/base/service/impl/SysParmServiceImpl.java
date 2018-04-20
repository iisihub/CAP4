package com.iisigroup.cap.base.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.iisigroup.cap.base.dao.SysParmDao;
import com.iisigroup.cap.base.model.SysParm;
import com.iisigroup.cap.base.service.SysParmService;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.model.Page;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * SysParmServiceImpl
 * </pre>
 *
 * @since 2018/04/23
 * @author bobpeng
 * @version
 *          <ul>
 *          <li>2018/04/23,bobpeng,new
 *          </ul>
 */
@Service
public class SysParmServiceImpl implements SysParmService {

    @Resource
    private SysParmDao sysParmDao;

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.SysParmService#findPage(com.iisigroup.cap.db.dao.SearchSetting, com.iisigroup.cap.component.Request)
     */
    @Override
    public Page<SysParm> findPage(SearchSetting search, Request params) {
        if (!CapString.isEmpty(params.get("parmId"))) {
            search.addSearchModeParameters(SearchMode.EQUALS, "parmId", params.get("parmId"));
        }
        if (!search.hasOrderBy()) {
            search.addOrderBy("parmId");
        }
        return sysParmDao.findPage(SysParm.class, search);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.SysParmService#addSysParm(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void addSysParm(String parmId, String parmValue, String parmDesc) {
        SysParm sysParm = sysParmDao.findById(SysParm.class, parmId);
        if (sysParm != null) {
            throw new CapMessageException(CapAppContext.getMessage("sysparm.error.01"), getClass()); // 參數代碼已存在
        }
        saveOrUpdateSysParm(new SysParm(), parmId, parmValue, parmDesc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.SysParmService#modifySysParm(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void modifySysParm(String parmId, String parmValue, String parmDesc) {
        SysParm sysParm = sysParmDao.findById(SysParm.class, parmId);
        if (sysParm == null) {
            throw new CapMessageException(CapAppContext.getMessage("sysparm.error.02"), getClass()); // 參數代碼不存在
        }
        saveOrUpdateSysParm(sysParm, parmId, parmValue, parmDesc);
    }

    private void saveOrUpdateSysParm(SysParm sysParm, String parmId, String parmValue, String parmDesc) {
        sysParm.setParmId(parmId);
        sysParm.setParmValue(parmValue);
        sysParm.setParmDesc(parmDesc);
        sysParm.setUpdateTime(CapDate.getCurrentTimestamp());
        sysParm.setUpdater(CapSecurityContext.getUserId());
        sysParmDao.save(sysParm);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.SysParmService#deleteSysParmByParmId(java.lang.String)
     */
    @Override
    public void deleteSysParmByParmId(String parmId) {
        SysParm sysParm = sysParmDao.findById(SysParm.class, parmId);
        if (sysParm != null) {
            sysParmDao.delete(sysParm);
        }
    }
}
