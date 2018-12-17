package com.iisigroup.colabase.import_.dao;

import java.util.List;

import com.iisigroup.cap.db.dao.GenericDao;
import com.iisigroup.colabase.import_.model.DemoImportCustomer;

public interface DemoImportCustomerDao extends GenericDao<DemoImportCustomer> {

    List spImport(String[] importFiles, String[] storedProcedureNames);
    
    List spImport(String importFile, String storedProcedureName);

}
