package com.iisigroup.colabase.http.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.colabase.http.model.HttpReceiveRequest;
import com.iisigroup.colabase.http.model.HttpSendResponse;
import com.iisigroup.colabase.http.response.CrossDomainAjaxFormResult;
import com.iisigroup.colabase.http.service.HttpService;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapString;

/**<pre>
 * Demo HTTP Handler
 * </pre>
 * @since  2018年7月
 * @author LilyPeng
 * @version <ul>
 *           <li>2018年7月,LilyPeng,new
 *          </ul>
 */
@Controller("demohttphandler")
public class DemoHttpHandler extends MFormHandler {
    
    @Autowired
    private HttpService httpSvc;
    
    private static final String ERROR_MSG = "error";
    private static final String[] SEND_COLS = new String[] { "name", "birthday", "mobile" };
    private static final String TEST_SEND_URL = "https://127.0.0.1:8443/http-test-server/v1/tw/sendTest";
    private static final String TEST_RECEIVE_URL = "http://127.0.0.1:8098/citi-web/demohttphandler/httpReceiveTest";
    private static final String STATUS_MSG = "status_msg";
    private static final String STATUS_CODE = "status_code";
    
    /**
     * @param request request
     * @return result
     * @throws CapException CapException
     */
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
                result.add(httpSvc.sendUrlEncodedForm(url, SEND_COLS, contents));
            }else if("json".equalsIgnoreCase(way)){
                result.add(httpSvc.sendJson(url, jsonStr));
            }

        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("Http Send Error", e);
        }
        return result;
    }
    
    /**
     * @param request request
     * @return result
     * @throws CapException CapException
     */
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
                result.add(httpSvc.sendUrlEncodedForm(url, SEND_COLS, contents));
            }else if("json".equalsIgnoreCase(way)){
                result.add(httpSvc.sendJson(url, jsonStr));
            }

        } catch (Exception e) {
            result.set(ERROR_MSG, e.getMessage());
            logger.error("Http Receive Error", e);
        }
        return result;
    }
    
    /**
     * @param request request
     * @return result
     * @throws CapException CapException
     */
    public Result httpReceiveTest(Request request) throws CapException {
        CrossDomainAjaxFormResult result = new CrossDomainAjaxFormResult();
        result.setCallback(request.get("callback"));
        result.setCorsDomain("*");
        HttpReceiveRequest httpResult = httpSvc.receiveData(request);
        String requestString = httpResult.getRequestString();

        result.set(STATUS_MSG, requestString);
        result.set(STATUS_CODE, httpResult.getStatusCode());
        return result;
    }
    
}