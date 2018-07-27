package com.iisigroup.colabase.http.service.impl;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;

import com.iisigroup.cap.component.impl.AjaxFormResult;

import static org.junit.Assert.*;

public class HttpServiceImplTest {

    // 測試傳送URL
    private final String TEST_SEND_UTL = "https://127.0.0.1:8443/http-test-server/v1/tw/sendTest"; // 自訂
    // 測試接收URL
    private final String TEST_RECEIVE_URL = "http://127.0.0.1:8098/citi-web/demohttphandler/httpReceiveTest"; // 自訂
    // 傳送資料(key)
    private final String[] SEND_COLUMNS = new String[] { "name", "birthday", "mobile" }; // 自訂
    // 傳送資料(value)
    private final String[] SEND_VALUES = new String[] { "測試", "19800101", "0912XXX678" }; // 自訂
    // 傳送資料(JSON)
    private final String JSON_STRING = "{\"name\": \"測試\",\"birthday\": \"19800101\",\"mobile\": \"0912XXX678\"}";
    // Http狀態碼
    private final String STATUS_CODE = "statusCode";
    // 返回值
    private final String RETURN_CODE = "status_code";
    
    @Spy
    private HttpServiceImpl httpServiceImpl;

    @Before
    public void setUp() throws Exception {
        httpServiceImpl = new HttpServiceImpl();
    }

    @Test
    public void testSend() throws Exception {
        AjaxFormResult result = new AjaxFormResult();
        Map<String, String> contents = new HashMap<String, String>();
        for(int i=0 ; i<SEND_COLUMNS.length; i++){
            contents.put(SEND_COLUMNS[i], SEND_VALUES[i]);
        }
        result = (AjaxFormResult) httpServiceImpl.sendUrlEncodedForm(TEST_SEND_UTL, SEND_COLUMNS, contents, false);
//        assertNotNull(result.get("responseString"));
        assertEquals(200, result.get(STATUS_CODE));
    }

    @Test
    public void testSend2() throws Exception {
        AjaxFormResult result = new AjaxFormResult();
        result = (AjaxFormResult) httpServiceImpl.sendJson(TEST_SEND_UTL, JSON_STRING, false);
//        assertNotNull(result.get("responseString"));
        assertEquals(200, result.get(STATUS_CODE));
    }

    @Test
    public void testReceive() throws Exception {
        AjaxFormResult result = new AjaxFormResult();
        Map<String, String> contents = new HashMap<String, String>();
        for(int i=0 ; i<SEND_COLUMNS.length; i++){
            contents.put(SEND_COLUMNS[i], SEND_VALUES[i]);
        }
        result = (AjaxFormResult) httpServiceImpl.sendUrlEncodedForm(TEST_RECEIVE_URL, SEND_COLUMNS, contents, true);
        String responseString = (String) result.get("responseString");
        JSONObject resultJSON = JSONObject.fromObject(responseString);
        assertEquals(200, resultJSON.opt(RETURN_CODE));
//        assertEquals(200, result.get(STATUS_CODE));
    }
    
    @Test
    public void testReceive2() throws Exception {
        AjaxFormResult result = new AjaxFormResult();
        result = (AjaxFormResult) httpServiceImpl.sendJson(TEST_RECEIVE_URL, JSON_STRING, true);
        String responseString = (String) result.get("responseString");
        JSONObject resultJSON = JSONObject.fromObject(responseString);
        assertEquals(200, resultJSON.opt(RETURN_CODE));
//        assertEquals(200, result.get(STATUS_CODE));
    }

}
