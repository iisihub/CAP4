package com.iisigroup.colabase.http.service.impl;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.impl.AjaxFormResult;

import static org.junit.Assert.*;

public class HTTPServiceImplTest {

    // 測試傳送URL
    private final String testSendUrl = "https://127.0.0.1:8443/http-test-server/v1/tw/sendTest"; // 自訂
    // 測試接收URL
    private final String testReceiveUrl = "http://127.0.0.1:8098/citi-web/demohttphandler/httpReceive"; // 自訂
    // 傳送資料
    private final String[] sendCols = new String[] { "name", "birthday", "mobile" }; // 自訂

    @Spy
    private HTTPServiceImpl httpServiceImpl;

    @Before
    public void setUp() throws Exception {
        httpServiceImpl = new HTTPServiceImpl();
    }

    @Test
    public void testSend() throws Exception {
        AjaxFormResult result = new AjaxFormResult();
        Map<String, String> request = new HashMap<String, String>();
        request.put("name", "name123");
        request.put("birthday", "birthday123");
        request.put("mobile", "mobile123");
        result = (AjaxFormResult) httpServiceImpl.sendUrlEncodedForm(request, testSendUrl, sendCols, false);
        assertNotNull(result.get("responseString"));
    }

    @Test
    public void testSend2() throws Exception {
        AjaxFormResult result = new AjaxFormResult();
        Map<String, String> request = new HashMap<String, String>();
        JSONObject json = new JSONObject();
        json.put("name", "name123");
        json.put("birthday", "birthday123");
        json.put("mobile", "mobile123");
        result = (AjaxFormResult) httpServiceImpl.sendJSON(request, testSendUrl, json, false);
        assertNotNull(result.get("responseString"));
    }

    @Test
    public void testReceive() throws Exception {
        AjaxFormResult result = new AjaxFormResult();
        Map<String, String> request = new HashMap<String, String>();
        request.put("name", "name123");
        request.put("birthday", "birthday123");
        request.put("mobile", "mobile123");
        result = (AjaxFormResult) httpServiceImpl.sendUrlEncodedForm(request, testReceiveUrl, sendCols, true);
        assertNotNull(result.get("responseString"));
    }
    
    @Test
    public void testReceive2() throws Exception {
        AjaxFormResult result = new AjaxFormResult();
        Map<String, String> request = new HashMap<String, String>();
        JSONObject json = new JSONObject();
        json.put("name", "name123");
        json.put("birthday", "birthday123");
        json.put("mobile", "mobile123");
        result = (AjaxFormResult) httpServiceImpl.sendJSON(request, testReceiveUrl, json, true);
        assertNotNull(result.get("responseString"));
    }

}
