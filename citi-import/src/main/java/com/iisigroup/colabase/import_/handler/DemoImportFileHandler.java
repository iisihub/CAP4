package com.iisigroup.colabase.import_.handler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.colabase.import_.service.ImportFileService;

@Controller("demoimportfilehandler")
public class DemoImportFileHandler extends MFormHandler {
    
    @Autowired
    private ImportFileService importFileSrv;
    
    private static final String fileDateFormat = "yyyyMMdd";
    
    public Result importFileProcess(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String localFilePath1 = request.get("localFilePath1");
        String localFileName1 = request.get("localFileName1");
//        String days1 = request.get("days1");
        int days1 = Integer.valueOf(request.get("days1"));
        String remoteFilePathName1 = request.get("remoteFilePathName1");
        String storedProcedureName1 = request.get("storedProcedureName1");
        
//        remoteFilePathName1 = "D:/data/Demo_Import_AP/demo_customer_gen_data.txt";
//        storedProcedureName1 = "DEMO_IMPORT_CUSTOMER_DATA";
        
        try {
            Map map = importFileSrv.importFileProcess(localFilePath1, localFileName1, days1, remoteFilePathName1, storedProcedureName1);
            List list = (List) map.get("spResult");
            if(!list.isEmpty()){
                result.set("result", "檔案內容有" + map.get("countRows") + "筆資料，實際匯入" + list.get(0) + "筆資料");
            }else{
                result.set("result", "檔案時間過久，沒有讀取。");
            }
        } catch (Exception e) {
            result.set("error", e.getMessage());
            logger.error("Import File Process Error", e);
        }
        return result;
    }
    
    public Result importFileProcessTest(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        try {
            importFileSrv.test("D:/TEST/TEST_EXPORT/", "data.txt.bak");
        } catch (Exception e) {
            result.set("error", e.getMessage());
            logger.error("Import File Process Error", e);
        }
        return result;
    }
    
    public Result checkTime(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String localFilePath1 = request.get("localFilePath1");
        String localFileName1 = request.get("localFileName1");
//        String days1 = request.get("days1");
        int days1 = Integer.valueOf(request.get("days1"));
        try {
//            localFileName1 += ".bak";
            boolean checkTime = importFileSrv.checkTime(localFilePath1, localFileName1, days1);
            if(checkTime){
                result.set("result", "檔案在" + days1 + "天以內，可以執行Stored Procedure。");
            }else{
                result.set("result", "檔案不在" + days1 + "天以內，不要執行Stored Procedure。");
            }
        } catch (Exception e) {
            result.set("error", e.getMessage());
            logger.error("Check File Time Error", e);
        }
        return result;
    }
    
    public Result checkDate(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String localFilePath1 = request.get("localFilePath1");
        String localFileName1 = request.get("localFileName1");
        String date1 = request.get("date1");
        try {
            boolean checkDate = importFileSrv.checkDate(localFilePath1, localFileName1, date1, fileDateFormat);
            if(checkDate){
                result.set("result", "有" + date1 + "的檔案，可以執行Stored Procedure。");
            }else{
                result.set("result", "沒有" + date1 + "的檔案，不要執行Stored Procedure。");
            }
        } catch (Exception e) {
            result.set("error", e.getMessage());
            logger.error("Check File Time Error", e);
        }
        return result;
    }
    
    public Result countRows(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String localFilePath1 = request.get("localFilePath1");
        String localFileName1 = request.get("localFileName1");
        try {
//            localFileName1 += ".bak";
            int rows = importFileSrv.countRows(localFilePath1, localFileName1);
            result.set("result", "檔案筆數:" + rows);
        } catch (Exception e) {
            result.set("error", e.getMessage());
            logger.error("Count File Rows Error", e);
        }
        return result;
    }
    
    public Result runSP(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String remoteFilePathName1 = request.get("remoteFilePathName1");
        String storedProcedureName1 = request.get("storedProcedureName1");
        
//        remoteFilePathName1 = "D:/data/Demo_Import_AP/demo_customer_gen_data.txt";
//        storedProcedureName1 = "DEMO_IMPORT_CUSTOMER_DATA";
        try {
            List list = importFileSrv.runSP(remoteFilePathName1, storedProcedureName1);
            result.set("result", "已匯入筆數:" + list.get(0));
        } catch (Exception e) {
            result.set("error", e.getMessage());
            logger.error("Run StoredProcedure Error", e);
        }
        return result;
    }
    
}