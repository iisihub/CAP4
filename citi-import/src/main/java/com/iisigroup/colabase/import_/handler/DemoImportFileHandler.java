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
    
    private static final String FILE_DATE_FORMAT = "yyyyMMdd";
    private static final String LOCAL_FILE_PATH_1 = "localFilePath1";
    private static final String LOCAL_FILE_NAME_1 = "localFileName1";
    private static final String DAYS_1 = "days1";
    private static final String DATE_1 = "date1";
    private static final String REMOTE_FILE_PATH_NAME_1 = "remoteFilePathName1";
    private static final String STORED_PROCEDURE_NAME_1 = "storedProcedureName1";
    
    private static final String RESULT_MSG = "result";
    private static final String ERROR_MSG = "error";
    
    public Result importFileProcess(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String localFilePath1 = request.get(LOCAL_FILE_PATH_1);
        String localFileName1 = request.get(LOCAL_FILE_NAME_1);
        int days1 = Integer.valueOf(request.get(DAYS_1));
        String remoteFilePathName1 = request.get(REMOTE_FILE_PATH_NAME_1);
        String storedProcedureName1 = request.get(STORED_PROCEDURE_NAME_1);
        try {
            Map map = importFileSrv.importFileProcess(localFilePath1, localFileName1, days1, remoteFilePathName1, storedProcedureName1);
            List list = (List) map.get("spResult");
            if(!list.isEmpty()){
                result.set(RESULT_MSG, "檔案內容有" + map.get("countRows") + "筆資料，實際匯入" + list.get(0) + "筆資料");
            }else{
                result.set(RESULT_MSG, "檔案時間過久，沒有讀取。");
            }
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("Import File Process Error", e);
        }
        return result;
    }
    
    public Result checkTime(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String localFilePath1 = request.get(LOCAL_FILE_PATH_1);
        String localFileName1 = request.get(LOCAL_FILE_NAME_1);
        int days1 = Integer.valueOf(request.get(DAYS_1));
        try {
            boolean checkTime = importFileSrv.checkTime(localFilePath1, localFileName1, days1);
            if(checkTime){
                result.set(RESULT_MSG, "檔案在" + days1 + "天以內，可以執行Stored Procedure。");
            }else{
                result.set(RESULT_MSG, "檔案不在" + days1 + "天以內，不要執行Stored Procedure。");
            }
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("Check File Time Error", e);
        }
        return result;
    }
    
    public Result checkDate(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String localFilePath1 = request.get(LOCAL_FILE_PATH_1);
        String localFileName1 = request.get(LOCAL_FILE_NAME_1);
        String date1 = request.get(DATE_1);
        try {
            boolean checkDate = importFileSrv.checkDate(localFilePath1, localFileName1, date1, FILE_DATE_FORMAT);
            if(checkDate){
                result.set(RESULT_MSG, "有" + date1 + "的檔案，可以執行Stored Procedure。");
            }else{
                result.set(RESULT_MSG, "沒有" + date1 + "的檔案，不要執行Stored Procedure。");
            }
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("Check File Time Error", e);
        }
        return result;
    }
    
    public Result countRows(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String localFilePath1 = request.get(LOCAL_FILE_PATH_1);
        String localFileName1 = request.get(LOCAL_FILE_NAME_1);
        try {
            int rows = importFileSrv.countRows(localFilePath1, localFileName1);
            result.set(RESULT_MSG, "檔案筆數:" + rows);
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("Count File Rows Error", e);
        }
        return result;
    }
    
    public Result runSP(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        String remoteFilePathName1 = request.get(REMOTE_FILE_PATH_NAME_1);
        String storedProcedureName1 = request.get(STORED_PROCEDURE_NAME_1);
        try {
            List list = importFileSrv.runSP(remoteFilePathName1, storedProcedureName1);
            result.set(RESULT_MSG, "已匯入筆數:" + list.get(0));
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("Run StoredProcedure Error", e);
        }
        return result;
    }
    
}