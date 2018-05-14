package com.iisigroup.colabase.import_.dao.impl;

import java.io.File;
import java.sql.CallableStatement;
//import java.sql.Connection;
//import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

//import javax.persistence.Query;
import javax.sql.DataSource;

//import org.springframework.stereotype.Repository;

import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;
import com.iisigroup.colabase.import_.dao.ImportCustomerDao;
import com.iisigroup.colabase.import_.model.ImportCustomer;
//import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

//@Repository("customerDao")
public class ImportCustomerDaoImpl extends GenericDaoImpl<ImportCustomer> implements ImportCustomerDao {

    
    public ImportCustomerDaoImpl(){
    }
    
    
    public ImportCustomerDaoImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }
    
    
    
    private DataSource dataSource;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ImportCustomer findCustomerByIdNoAndBd(String idNo, String brithday) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "id", idNo);
        search.addSearchModeParameters(SearchMode.EQUALS, "birthday", brithday);
        search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
        List<ImportCustomer> list = find(search);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public int countCustomer() {
        SearchSetting search = createSearchTemplete();
        return this.count(search);
    }

    public List spImport(String[] importFiles, String[] storedProcedureNames) {

        List<String> list = new ArrayList();
        /*
        try {
            for (int i = 0; i < importFiles.length; i++) {
                String importFile = importFiles[i];
                File f = new File(importFile);
                if (!f.exists()) {
                    System.out.println("File Not Found");
                    logger.info("SP Import DEMO_CUSTOMER Result: File Not Found");
                }
                String sp = storedProcedureNames[i];
                // Map<String, Object> params = new HashMap<String, Object>();
                // params.put("ImportFile", importFile);
                Query storedProcedure = getEntityManager().createNativeQuery("exec " + sp + " ?1");
                storedProcedure.setParameter(1, importFile);
                list = storedProcedure.getResultList();
                // Map<String, Object> result = callStoredProcedure(sp, params);
                
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
        */
        
        
        CallableStatement cs = null;
        try {
            
            // TODO 改寫JAVA與SP
            for (int i = 0; i < importFiles.length; i++) {
                String importFile = importFiles[i];
                File f = new File(importFile);
                if (!f.exists()) {
                    System.out.println("File Not Found");
                    logger.info("SP Import DEMO_CUSTOMER Result: File Not Found");
                }
                String sp = storedProcedureNames[i];
//                String spCmmdStr = "exec " + sp + " ?1";
                String spCmmdStr = "{call " + sp + " (?,?)}";
//                String spCmmdStr = "{?=call " + sp + " (?)}";
                
                logger.info(spCmmdStr);
                cs = dataSource.getConnection().prepareCall(spCmmdStr);

                // 設定 IN/OUT 參數的 Index 及值
                cs.setString(1, importFile);
                cs.registerOutParameter(2, Types.INTEGER);
//                cs.registerOutParameter(1, Types.INTEGER);
//                cs.setString(2, importFile);
                // 執行並取回 OUT 參數值
//                cs.executeUpdate();
                cs.execute();
                int OutResult = cs.getInt(2);
//                ResultSet rs = cs.getResultSet();
                list.add(String.valueOf(OutResult));
                logger.info("SP Import DEMO_CUSTOMER Result:" + OutResult);
                for (Object s : list) {
                    logger.info(sp + " result: " + s.toString());
                }
            }
        } catch (Exception e) {
            logger.error("spImport>>spImportError::", e);
            list.add(e.getMessage());
        } finally {
            try {
                if (cs != null) {
                    cs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    
    public List spImport(String importFile, String storedProcedureName) {
        List<String> list = new ArrayList();
        
        /*
        try {
                File f = new File(importFile);
                if (!f.exists()) {
                    System.out.println("File Not Found");
                    logger.info("SP Import DEMO_CUSTOMER Result: File Not Found");
                }
                // Map<String, Object> params = new HashMap<String, Object>();
                // params.put("ImportFile", importFile);
                Query storedProcedure = getEntityManager().createNativeQuery("exec " + storedProcedureName + " ?1");
                storedProcedure.setParameter(1, importFile);
                list = storedProcedure.getResultList();
                // Map<String, Object> result = callStoredProcedure(sp, params);
                logger.info(storedProcedureName + " result: " + list.toString());
        } catch (Exception e) {
            e.printStackTrace();
            list.add(e.getMessage());
            list.add(e.getStackTrace().toString());
        }
        */
        
        
        CallableStatement cs = null;
        try {
                File f = new File(importFile);
                if (!f.exists()) {
                    System.out.println("File Not Found");
                    logger.info("SP Import DEMO_CUSTOMER Result: File Not Found");
                }
//                String spCmmdStr = "exec " + storedProcedureName + " ?1";
                String spCmmdStr = "{call " + storedProcedureName + " (?,?)}";
//                String spCmmdStr = "{?=call " + storedProcedureName + " (?)}";
                
                logger.info(spCmmdStr);
                
                
                
                cs = dataSource.getConnection().prepareCall(spCmmdStr);
                
                
//                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); 
//                Connection con = java.sql.DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=XCOLA;SelectMethod=direct","sa","P@ssw0rd");
//                cs = con.prepareCall(spCmmdStr);
                
                
//                SQLServerDataSource ds = new SQLServerDataSource();  
//                ds.setUser("sa");  
//                ds.setPassword("P@ssw0rd");
//                ds.setServerName("localhost");
//                ds.setPortNumber(1433);
//                ds.setDatabaseName("XCOLA");
//                ds.setSelectMethod("direct");
//                Connection con = ds.getConnection();
//                cs = con.prepareCall(spCmmdStr);
                

                // 設定 IN/OUT 參數的 Index 及值
                cs.setString(1, importFile);
                cs.registerOutParameter(2, Types.INTEGER);
//                cs.registerOutParameter(1, Types.INTEGER);
//                cs.setString(2, importFile);
                // 執行並取回 OUT 參數值
//                cs.executeUpdate();
                cs.execute();
                int OutResult = cs.getInt(2);
//                ResultSet rs = cs.getResultSet();
                list.add(String.valueOf(OutResult));
                logger.info(storedProcedureName + " result: " + list.toString());
        } catch (Exception e) {
            logger.error("spImport>>spImportError::", e);
            list.add(e.getMessage());
        } finally {
            try {
                if (cs != null) {
                    cs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

}
