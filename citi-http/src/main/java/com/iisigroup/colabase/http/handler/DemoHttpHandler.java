package com.iisigroup.colabase.http.handler;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.colabase.http.service.HTTPService;
import com.iisigroup.cap.mvc.handler.MFormHandler;

@Controller("demohttphandler")
public class DemoHttpHandler extends MFormHandler {
    
    @Autowired
    private HTTPService httpSvc;
    
    private static final String FILE_DATE_FORMAT = "yyyyMMdd";
    
    private static final String RESULT_MSG = "result";
    private static final String ERROR_MSG = "error";
    
    private String[] sendCols = new String[] { "name", "birthday", "mobile" };
    
    public Result httpSend(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        try {

            String way = request.get("way");
            String url = request.get("url");
            
            Map contents = new HashMap<String, String>();
            for(String param: sendCols){
                contents.put(param, request.get(param));
            }
            
            if("basic".equalsIgnoreCase(way)){
//                result.add(httpSvc.sendUrlEncodedForm(request, url, sendCols, true));
                result.add(httpSvc.sendUrlEncodedForm(contents, url, sendCols, false));    //call server
            }else if("json".equalsIgnoreCase(way)){
                JSONObject json = new JSONObject();
                for (String colName : sendCols) {
                    json.put(colName, request.get(colName));
                }
//                result.add(httpSvc.sendJSON(request, url, json, true));
                result.add(httpSvc.sendJSON(contents, url, json, false));    //call server
            }
            
//            result.add(httpSvc.sendEligibleDatatoMFS(request));
//            result.add(httpSvc.sendUrlEncodedForm(request, sendCols, true));
//            result.add(httpSvc.sendJSON(request, sendCols, true));


            String test = "";


//            if(!list.isEmpty()){
//                result.set(RESULT_MSG, "檔案內容有" + map.get("countRows") + "筆資料，實際匯入" + list.get(0) + "筆資料");
//            }else{
//                result.set(RESULT_MSG, "檔案時間過久，沒有讀取。");
//            }
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("Http Send Error", e);
        }
        return result;
    }
    
    public Result httpReceive(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        try {
            
            String way = request.get("way");
            String url = request.get("url");
            JSONObject json = new JSONObject();
            
            Map contents = new HashMap<String, String>();
            for(String param: sendCols){
                contents.put(param, request.get(param));
            }
            
            if("basic".equalsIgnoreCase(way)){
                result.add(httpSvc.sendUrlEncodedForm(contents, url, sendCols, true));   //url設成自己的receive
            }else if("json".equalsIgnoreCase(way)){
                result.add(httpSvc.sendJSON(contents, url, json, true));             //url設成自己的receive
            }
            
            
//            result.add(httpSvc.receiveMFSUpdateMessage(request));
            String test = "";
//            if(!list.isEmpty()){
//                result.set(RESULT_MSG, "檔案內容有" + map.get("countRows") + "筆資料，實際匯入" + list.get(0) + "筆資料");
//            }else{
//                result.set(RESULT_MSG, "檔案時間過久，沒有讀取。");
//            }
        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("Http Receive Error", e);
        }
        return result;
    }
    
}