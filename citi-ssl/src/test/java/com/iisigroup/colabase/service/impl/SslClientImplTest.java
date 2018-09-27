package com.iisigroup.colabase.service.impl;

import com.google.gson.JsonObject;
import com.iisigroup.colabase.demo.sslclient.model.DemoRequestContent;
import com.iisigroup.colabase.demo.sslclient.service.impl.DemoSslClientService;
import com.iisigroup.colabase.model.RequestContent;
import com.iisigroup.colabase.model.ResponseContent;
import com.iisigroup.colabase.service.SslClient;
import org.apache.log4j.varia.NullAppender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by VALLA on 2018/4/10.
 */
public class SslClientImplTest {

    SslClient sslClient;

    final String keyStorePath = "/Users/VALLA/Documents/iisi/CITI_CLM/codeSpace/impl/workspace/CLM/clm-app/src/main/resources/keystore/keystore_client_1602006";
    final String keyStorePWD = "p@ssw0rd";
    final String trustStorePath = "/Users/VALLA/Documents/iisi/CITI_CLM/codeSpace/impl/workspace/CLM/clm-app/src/main/resources/keystore/truststore_client_1602006_own.jks";
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
        RequestContent demoRequestContent = new DemoRequestContent();
        sslClient.sendRequestWithDefaultHeader(demoRequestContent);
    }

    private RequestContent getDummyContent(){
        RequestContent requestContent = new DemoRequestContent();
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