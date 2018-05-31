package com.iisigroup.colabase.import_.dao.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;
import com.iisigroup.colabase.import_.dao.ImportCustomerDao;
import com.iisigroup.colabase.import_.model.ImportCustomer;

@Repository("importCustomerDao")
public class ImportCustomerDaoImpl extends GenericDaoImpl<ImportCustomer> implements ImportCustomerDao {

    public ImportCustomerDaoImpl(){
        
    }

    public List spImport(String[] importFiles, String[] storedProcedureNames) {
        
        List list = new ArrayList();
        try {
            for (int i = 0; i < importFiles.length; i++) {
                String importFile = importFiles[i];
                File f = new File(importFile);
                if (!f.exists()) {
                    System.out.println("File Not Found");
                    logger.info("SP Import DEMO_CUSTOMER Result: File Not Found");
                }
                String sp = storedProcedureNames[i];
                Query storedProcedure = getEntityManager().createNativeQuery("exec " + "?1" + " ?2");
                storedProcedure.setParameter(1, sp);
                storedProcedure.setParameter(2, importFile);
                list = storedProcedure.getResultList();
                logger.info("SP Import DEMO_CUSTOMER Result:");
                for (Object s : list) {
                    logger.info(sp + " result: " + s.toString());
                }
            }
        } catch (Exception e) {
            logger.error("*****無法匯入檔案 Exception:", e);
            list.add(e.getMessage());
            list.add(Arrays.toString(e.getStackTrace()));
        }
        return list;
    }
    
    public List spImport(String importFile, String storedProcedureName) {
        
        List list = new ArrayList();
        try {
                File f = new File(importFile);
                if (!f.exists()) {
                    System.out.println("File Not Found");
                    logger.info("SP Import DEMO_CUSTOMER Result: File Not Found");
                }
                Query storedProcedure = getEntityManager().createNativeQuery("exec " + "?1" + " ?2");
                storedProcedure.setParameter(1, storedProcedureName);
                storedProcedure.setParameter(2, importFile);
                list = storedProcedure.getResultList();
                logger.info(storedProcedureName + " result: " + list.toString());
        } catch (Exception e) {
            logger.error("*****無法匯入檔案 Exception:", e);
            list.add(e.getMessage());
            list.add(Arrays.toString(e.getStackTrace()));
        }
        return list;
    }

}
