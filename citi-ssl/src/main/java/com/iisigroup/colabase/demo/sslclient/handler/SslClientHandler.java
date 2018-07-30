package com.iisigroup.colabase.demo.sslclient.handler;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.mvc.handler.MFormHandler;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.colabase.demo.sslclient.model.DemoRequestContent;
import com.iisigroup.colabase.model.RequestContent;
import com.iisigroup.colabase.model.ResponseContent;
import com.iisigroup.colabase.service.SslClient;


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
    private SslClient<ResponseContent> sslClient;

    public Result testSslClient(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        StringBuilder resultStr = new StringBuilder();
        String targetUrl = request.get("targetUrl");
        final JsonObject obj;
        final Map<String, List<String>> header;
        RequestContent requestContent;
        ResponseContent responseContent;
        try {
            Gson gson = new Gson();
            obj = gson.fromJson(request.get("jsonData"), JsonObject.class);
            header = getHeader(request.get("headerData"));
            requestContent = getDummyContent(targetUrl, header, obj);
            if(header == null) {
                responseContent = sslClient.sendRequestWithDefaultHeader(requestContent);
            } else {
                responseContent = sslClient.sendRequest(requestContent);
            }
            resultStr.append(responseContent);
        } catch (JsonSyntaxException e) {
            resultStr.append("please check your JSON format");
        }
        result.set("result", resultStr.toString());
        return result;
    }

    private Map<String, List<String>> getHeader(String jsonStr){
        if(CapString.isEmpty(jsonStr))
            return null;
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, List<String>>>() {}.getType();
        return gson.fromJson(jsonStr, type);
    }

    private RequestContent getDummyContent(String targetUrl, Map<String, List<String>> headers, JsonObject jsonData){
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
