package com.iisigroup.colabase.dao;

import java.util.List;

import com.iisigroup.cap.db.dao.GenericDao;
import com.iisigroup.colabase.model.ZipCode;

public interface ZipCodeDao extends GenericDao<ZipCode> {

	List<ZipCode> findByZipCode(String zipCode);
	
	List<ZipCode> findByCounty(String county);
	
	List<ZipCode> findByDistrict(String district);
	
	List<ZipCode> findByCountyAndDist(String county, String district);
	
}