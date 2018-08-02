package com.iisigroup.colabase.service.impl;

import com.iisigroup.colabase.dao.ZipCodeDao;
import com.iisigroup.colabase.model.Address;
import com.iisigroup.colabase.model.ZipCode;
import com.iisigroup.colabase.service.AddressService;
import com.iisigroup.colabase.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2018/6/4 AndyChen,new
 * </ul>
 * @since 2018/6/4
 */
@Service
public class AddressServiceImpl extends AddressOriginalServiceImpl implements AddressService {

    @Autowired
    private ZipCodeDao zipCodeDao;


    @Override
    protected List<Map<String, Object>> query(int columnCount, String sqlStr, Object... parameters) throws Exception {
        if(sqlStr.contains("ZIP3")) {
            String parameter = parameters[0].toString();
            List<ZipCode> zipCodeList = zipCodeDao.findByZipCode(parameter);
            String s = "";
        }
        return super.query(columnCount, sqlStr, parameters);
    }
}
