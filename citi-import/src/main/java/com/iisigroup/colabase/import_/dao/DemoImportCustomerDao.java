package com.iisigroup.colabase.import_.dao;

import java.util.List;

import com.iisigroup.cap.db.dao.GenericDao;
import com.iisigroup.colabase.import_.model.DemoImportCustomer;

/**
 * <pre>
 * Demo Import Customer Dao
 * </pre>
 * 
 * @since 2018年5月14日
 * @author LilyPeng
 * @version <ul>
 *          <li>2018年5月14日,Lily,new
 *          </ul>
 */
public interface DemoImportCustomerDao extends GenericDao<DemoImportCustomer> {

    /**
     * @param importFiles
     *            要匯入的檔案
     * @param storedProcedureNames
     *            預存程序名稱
     * @return resultList
     */
    List spImport(String[] importFiles, String[] storedProcedureNames);

    /**
     * @param importFile
     *            要匯入的檔案
     * @param storedProcedureName
     *            預存程序名稱
     * @return resultList
     */
    List spImport(String importFile, String storedProcedureName);

}
