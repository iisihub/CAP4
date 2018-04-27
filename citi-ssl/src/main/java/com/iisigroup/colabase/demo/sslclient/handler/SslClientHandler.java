package com.iisigroup.colabase.demo.sslclient.handler;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.demo.sslclient.model.DemoRequestContent;
import com.iisigroup.colabase.model.RequestContent;
import com.iisigroup.colabase.model.ResponseContent;
import com.iisigroup.colabase.service.SslClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * <pre>
 * demo use handler
 * </pre>
 *
 * @author AndyChen
 * @version
 *          <ul>
 *          <li>2018/04/03,AndyChen,new
 *          </ul>
 * @since 2018/03/12
 */
@Controller("demosslclienthandler")
public class SslClientHandler extends MFormHandler {

    @Autowired
    private SslClient sslClient;

    public SslClientHandler() {
    }

    public Result testSslClient(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        StringBuilder resultStr = new StringBuilder();
        String targetUrl = request.get("targetUrl");
        final JSONObject obj;
        final HashMap<String, List<String>> header;
        RequestContent requestContent;
        ResponseContent responseContent;
        try {
            obj = JSONObject.fromObject(request.get("jsonData"));
            header = getHeader(request.get("headerData"));
            requestContent = getDummyContent(targetUrl, header, obj);
            if(header == null) {
                responseContent = sslClient.sendRequestWithDefaultHeader(requestContent);
            } else {
                responseContent = sslClient.sendRequest(requestContent);
            }
            resultStr.append(responseContent);
        } catch (JSONException e) {
            resultStr.append("please check your JSON format");
        } catch (IOException e) {
            resultStr.append( e.getCause());
        }
        result.set("result", resultStr.toString());
        return result;
    }

    private HashMap<String, List<String>> getHeader(String jsonStr){
        if(CapString.isEmpty(jsonStr))
            return null;
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        HashMap<String, List<String>> result = new HashMap<>();
        for (Object key  : jsonObject.keySet()) {
            Object o = jsonObject.get(key);
            if(o instanceof JSONArray) {
                List<String> values = new ArrayList<>();
                for (int i = 0 ; i < ((JSONArray) o).size() ; i++) {
                    values.add(String.valueOf(((JSONArray) o).get(i)));
                }
                result.put(key.toString(), values);
            } else {
                throw new JSONException("wrong json format");
            }
        }
        return result;
    }

    private RequestContent getDummyContent(String targetUrl, HashMap<String, List<String>> headers, JSONObject jsonData){
        RequestContent requestContent = new DemoRequestContent();
        requestContent.setRequestHeaders(headers);
        requestContent.setHttpMethod(RequestContent.HTTPMethod.POST);
        requestContent.setRetryTimes(3);
        requestContent.setTimeout(30000);
        requestContent.setTargetUrl(targetUrl);
        requestContent.setRetryHttpStatus(new int[]{404});
        requestContent.setRequestContent(jsonData);

        return requestContent;
    }
}
