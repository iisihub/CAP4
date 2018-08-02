package com.iisigroup.colabase.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;
import com.iisigroup.colabase.dao.ZipCodeDao;
import com.iisigroup.colabase.model.ZipCode;

@Repository("zipCodeDao")
public class ZipCodeDaoImpl extends GenericDaoImpl<ZipCode> implements ZipCodeDao {


    @Override
    public List<ZipCode> findAll() {
        Query query = getEntityManager().createNativeQuery("select r.* from CO_XSL_ZIPCODE r WITH(NOLOCK)", ZipCode.class);
        return query.getResultList();
    }

}
