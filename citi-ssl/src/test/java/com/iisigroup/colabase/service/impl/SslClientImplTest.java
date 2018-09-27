package com.iisigroup.colabase.service.impl;

import com.google.gson.JsonObject;
import com.iisigroup.colabase.demo.sslclient.model.DemoJsonRequestContent;
import com.iisigroup.colabase.demo.sslclient.model.DemoPostDataRequestContent;
import com.iisigroup.colabase.demo.sslclient.service.impl.DemoSslClientService;
import com.iisigroup.colabase.model.ApiRequest;
import com.iisigroup.colabase.model.RequestContent;
import com.iisigroup.colabase.model.ResponseContent;
import com.iisigroup.colabase.service.SslClient;
import org.apache.log4j.varia.NullAppender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by VALLA on 2018/4/10.
 */
public class SslClientImplTest {

    SslClient sslClient;

    final String keyStorePath = "/Users/maiev/Documents/iisi/CITI_CLM/codeSpace/impl/workspace/CLM/clm-app/src/main/resources/keystore/keystore_client_1602006";
    final String keyStorePWD = "p@ssw0rd";
    final String trustStorePath = "/Users/maiev/Documents/iisi/CITI_CLM/codeSpace/impl/workspace/CLM/clm-app/src/main/resources/keystore/truststore_client_1602006_own.jks";
    final String targetUrl = "https://127.0.0.1:8443/mutual-authentication-server/v1/tw/onboarding/customers/deduplicationFlag";


    @BeforeClass
    public static void setSys(){
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());
    }

    @Before
    public void setUp() throws Exception {
        sslClient = new DemoSslClientService(keyStorePath, keyStorePWD, trustStorePath);
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());
    }


    @Test
    public void sendRequest() throws Exception {
        RequestContent requestContent = this.getDummyContent();
        ResponseContent responseContent = sslClient.sendRequest(requestContent);
        Assert.assertNotEquals("http status should not equals 0", responseContent.getStatusCode(), 0);
    }

    @Test
    public void sendRequestWithDefaultHeader() throws Exception {
        RequestContent requestContent = this.getDummyContent();
        ResponseContent responseContent = sslClient.sendRequestWithDefaultHeader(requestContent);
        Assert.assertNotEquals("http status should not equals 0", responseContent.getStatusCode(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_init_request_by_constructure() throws Exception {
        RequestContent demoRequestContent = new DemoJsonRequestContent();
        sslClient.sendRequestWithDefaultHeader(demoRequestContent);
    }

    @Test
    public void test_send_postForm() throws Exception {
        DemoPostDataRequestContent demoRequestContent = (DemoPostDataRequestContent)this.getDummyContent(DemoPostDataRequestContent.class);
        demoRequestContent.putData("testKey1", "value1");
        demoRequestContent.putData("testKey2", "value2");
        demoRequestContent.putData("參數1", "國字1");
        demoRequestContent.setSendType(ApiRequest.SendType.POST_FORM);
        ResponseContent responseContent = sslClient.sendRequest(demoRequestContent);
        Assert.assertNotEquals("http status should not equals 0", responseContent.getStatusCode(), 0);
    }

    @Test
    public void test_use_all_trust_ssl_connection() throws Exception {
        RequestContent requestContent = this.getDummyContent();
        requestContent.setUseOwnKeyAndTrustStore(false);
        requestContent.setIgnoreSSLcert(true);
        //預設是走雙向SSL驗證 server，要測該case，要自定義單向的server
//        requestContent.setTargetUrl("use your url");
        ResponseContent responseContent = sslClient.sendRequestWithDefaultHeader(requestContent);
        Assert.assertEquals( "javax.net.ssl.SSLHandshakeException: Received fatal alert: bad_certificate", responseContent.getException().toString());
    }

    private RequestContent getDummyContent() throws InvocationTargetException, NoSuchMethodException,
        InstantiationException, IllegalAccessException {
        return this.getDummyContent(DemoJsonRequestContent.class);
    }
    private RequestContent getDummyContent(Class requestContentClass) throws NoSuchMethodException,
        IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor constructor = requestContentClass.getConstructor();
        RequestContent requestContent = (RequestContent)constructor.newInstance();
//        RequestContent requestContent = new DemoJsonRequestContent();
        HashMap<String, List<String>> headers = new HashMap<>();
        headers.put("Accept", Arrays.asList(new String[]{"application/json"}));
        requestContent.setRequestHeaders(headers);
        requestContent.setHttpMethod(RequestContent.HTTPMethod.POST);
        requestContent.setRetryTimes(3);
        requestContent.setTimeout(30000);
        requestContent.setTargetUrl(targetUrl);
        requestContent.setRetryHttpStatus(new int[]{404});
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("testData1", "testValue");
        requestContent.setRequestContent(jsonObject);

        return requestContent;
    }

}