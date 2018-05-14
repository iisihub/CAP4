package com.iisigroup.colabase.import_.dao;

import java.util.List;

import javax.sql.DataSource;

import com.iisigroup.cap.db.dao.GenericDao;
import com.iisigroup.colabase.import_.model.ImportCustomer;

public interface ImportCustomerDao extends GenericDao<ImportCustomer> {

    /**
     * 用身分證字號及生日取得客戶資訊
     * 
     * @param idNo
     * @param brithday
     * @return
     */
    ImportCustomer findCustomerByIdNoAndBd(String idNo, String brithday);

    int countCustomer();

    List spImport(String[] importFiles, String[] storedProcedureNames);
    
    List spImport(String importFile, String storedProcedureName);

    void setDataSource(DataSource ds);

}
