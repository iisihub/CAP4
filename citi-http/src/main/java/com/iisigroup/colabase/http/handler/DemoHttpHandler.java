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

@Controller("demohttphandler")
public class DemoHttpHandler extends MFormHandler {
    
    @Autowired
    private HttpService httpSvc;
    
    private static final String ERROR_MSG = "error";
    
    private String[] sendCols = new String[] { "name", "birthday", "mobile" };
    
    public Result httpSend(Request request) throws CapException {
        AjaxFormResult result = new AjaxFormResult();
        try {

            String way = request.get("way");
            String url = request.get("url");
            
            String jsonStr = request.get("jsonStr");
            
            Map<String, String> contents = new HashMap<String, String>();
            for(String param: sendCols){
                contents.put(param, request.get(param));
            }
            
            if("basic".equalsIgnoreCase(way)){
                result.add(httpSvc.sendUrlEncodedForm(contents, url, sendCols, false));    //call test server
            }else if("json".equalsIgnoreCase(way)){
                result.add(httpSvc.sendJson(contents, url, jsonStr, false));    //call test server
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
            
            Map<String, String> contents = new HashMap<String, String>();
            for(String param: sendCols){
                contents.put(param, request.get(param));
            }
            
            if("basic".equalsIgnoreCase(way)){
                result.add(httpSvc.sendUrlEncodedForm(contents, url, sendCols, true));   //url設成自己的receive
            }else if("json".equalsIgnoreCase(way)){
                result.add(httpSvc.sendJson(contents, url, jsonStr, true));             //url設成自己的receive
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