package com.iisigroup.colabase.ssl.service.impl;

import com.google.gson.JsonObject;
import com.iisigroup.cap.utils.CapSystemConfig;
import com.iisigroup.colabase.json.model.ApiRequest;
import com.iisigroup.colabase.json.model.RequestContent;
import com.iisigroup.colabase.json.model.ResponseContent;
import com.iisigroup.colabase.ssl.sslclient.model.DemoJsonRequestContent;
import com.iisigroup.colabase.ssl.sslclient.model.DemoPostDataRequestContent;
import com.iisigroup.colabase.ssl.sslclient.service.impl.DemoSslClientService;
import com.iisigroup.colabase.ssl.util.PostFormDataFactory;
import org.apache.log4j.varia.NullAppender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by VALLA on 2018/4/10.
 */
@RunWith(MockitoJUnitRunner.class)
public class SslClientImplTest {



    final String keyStorePath = "/Users/maiev/Documents/iisi/CITI_CLM/codeSpace/impl/workspace/CLM/clm-app/src/main/resources/keystore/keystore_client_1602006";
    final String keyStorePWD = "p@ssw0rd";
    final String trustStorePath = "/Users/maiev/Documents/iisi/CITI_CLM/codeSpace/impl/workspace/CLM/clm-app/src/main/resources/keystore/truststore_client_1602006_own.jks";
    final String targetUrl = "https://127.0.0.1:8443/emgmDummyServer/api/clmAllApi";

    @Spy
    private CapSystemConfig systemConfig = new CapSystemConfig();

    @InjectMocks
    DemoSslClientService sslClient = new DemoSslClientService();


    @BeforeClass
    public static void setSys(){
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());
    }

    @Before
    public void setUp() throws Exception {

//        sslClient = new DemoSslClientService(keyStorePath, keyStorePWD, trustStorePath);
        Properties properties = systemConfig.getProperties();
        properties.put("keyStorePath", keyStorePath);
        properties.put("trustStorePath", trustStorePath);
        properties.put("keyStorePWD", keyStorePWD);
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
        DemoPostDataRequestContent demoRequestContent =
            PostFormDataFactory.getInstance(DemoPostDataRequestContent.class, sslClient);
//            PostFormDataFactory.getInstance(DemoPostDataRequestContent.class);

        demoRequestContent.setHttpMethod(RequestContent.HTTPMethod.POST);
        demoRequestContent.setUseOwnKeyAndTrustStore(true);
        demoRequestContent.setRetryTimes(3);
        demoRequestContent.setTimeout(30000);
        demoRequestContent.setTargetUrl(targetUrl);
        demoRequestContent.setRetryHttpStatus(new int[]{404});
        demoRequestContent.setTest1("value1");
        demoRequestContent.setTest2("value2");
        demoRequestContent.setTest3("value3");
//        demoRequestContent.putData("testKey1", "value1");
//        demoRequestContent.putData("testKey2", "value2");
//        demoRequestContent.putData("參數1", "國字1");
        demoRequestContent.setSendType(ApiRequest.SendType.POST_FORM);
//        demoRequestContent.setIgnoreSSLcert(true);
//        demoRequestContent.setProtocol("TLSv1.1");
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

    @Test //用於查看console ssl hand shake有沒有改變
    public void test_only_set_protocol_ssl_connection() throws Exception {
        RequestContent requestContent = this.getDummyContent();
        requestContent.setUseOwnKeyAndTrustStore(false);
        requestContent.setProtocol("TLSv1.2");
//        requestContent.setTargetUrl("your url");
        ResponseContent responseContent = sslClient.sendRequestWithDefaultHeader(requestContent);
        Assert.assertEquals( "javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target", responseContent.getException().toString());
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
        requestContent.setUseOwnKeyAndTrustStore(true);

        return requestContent;
    }

}