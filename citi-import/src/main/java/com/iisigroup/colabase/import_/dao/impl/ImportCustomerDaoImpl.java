package com.iisigroup.colabase.import_.dao.impl;

import java.io.File;
import java.util.ArrayList;
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
                Query storedProcedure = getEntityManager().createNativeQuery("exec " + sp + " ?1");
                storedProcedure.setParameter(1, importFile);
                list = storedProcedure.getResultList();
                logger.info("SP Import DEMO_CUSTOMER Result:");
                for (Object s : list) {
                    logger.info(sp + " result: " + s.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            list.add(e.getMessage());
            list.add(e.getStackTrace().toString());
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
                Query storedProcedure = getEntityManager().createNativeQuery("exec " + storedProcedureName + " ?1");
                storedProcedure.setParameter(1, importFile);
                list = storedProcedure.getResultList();
                logger.info(storedProcedureName + " result: " + list.toString());
        } catch (Exception e) {
            e.printStackTrace();
            list.add(e.getMessage());
            list.add(e.getStackTrace().toString());
        }
        return list;
    }

}
