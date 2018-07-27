package com.iisigroup.colabase.http.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.colabase.http.service.HttpService;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapString;

@Controller("demohttphandler")
public class DemoHttpHandler extends MFormHandler {
    
    @Autowired
    private HttpService httpSvc;
    
    private static final String ERROR_MSG = "error";
    private static final String[] SEND_COLS = new String[] { "name", "birthday", "mobile" };
    private static final String TEST_SEND_URL = "https://127.0.0.1:8443/http-test-server/v1/tw/sendTest";
    private static final String TEST_RECEIVE_URL = "http://127.0.0.1:8098/citi-web/demohttphandler/httpReceiveTest";
    
    public Result httpSend(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        try {

            String way = request.get("way");
            String url = request.get("url");
            String jsonStr = request.get("jsonStr");
            
            if (CapString.isEmpty(url)) {
                url = TEST_SEND_URL;
            }
            
            Map<String, String> contents = new HashMap<String, String>();
            for(String param: SEND_COLS){
                contents.put(param, request.get(param));
            }
            
            if("basic".equalsIgnoreCase(way)){
                result.add(httpSvc.sendUrlEncodedForm(url, SEND_COLS, contents, false));    //call test server，isTestMode = false
            }else if("json".equalsIgnoreCase(way)){
                result.add(httpSvc.sendJson(url, jsonStr, false));    //call test server，isTestMode = false
            }

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
            String jsonStr = request.get("jsonStr");
            
            if (CapString.isEmpty(url)) {
                url = TEST_RECEIVE_URL;
            }
            
            Map<String, String> contents = new HashMap<String, String>();
            for(String param: SEND_COLS){
                contents.put(param, request.get(param));
            }
            
            if("basic".equalsIgnoreCase(way)){
                result.add(httpSvc.sendUrlEncodedForm(url, SEND_COLS, contents, true));   //local測試自己的receive，isTestMode = true
            }else if("json".equalsIgnoreCase(way)){
                result.add(httpSvc.sendJson(url, jsonStr, true));             //local測試自己的receive，isTestMode = true
            }

        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("Http Receive Error", e);
        }
        return result;
    }
    
    public Result httpReceiveTest(Request request) throws CapException {
        Result result = httpSvc.receiveData(request);
        return result;
    }
    
}