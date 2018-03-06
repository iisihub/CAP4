import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.Asserts;
import org.junit.Test;

import com.iisigroup.cap.constants.Constants;
import com.iisigroup.cap.hg.service.impl.CapHttpService;

/* 
 * CapHttpServiceTest.java
 * 
 * Copyright (c) 2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */

/**
 * <pre>
 * TODO Write a short description on the purpose of the program
 * </pre>
 * 
 * @since 2018年3月5日
 * @author Lancelot
 * @version
 *          <ul>
 *          <li>2018年3月5日,Lancelot,new
 *          </ul>
 */
public class CapHttpServiceTest {
    @Test
    public void post() throws Exception {
        CapHttpService s = new CapHttpService();
        s.setProperty(Constants.HTTP_METHOD, HttpPost.METHOD_NAME);
        s.setProperty(Constants.HOST_URL, "https://iv5eqtz9rl.execute-api.ap-northeast-1.amazonaws.com/prod/normalize");
        s.setProperty(Constants.HTTP_RETRY_COUNT, "3");
        s.setProperty(Constants.CONNECTION_TIMEOUT, "20000");
        s.setProperty(Constants.ASYNC, "false");
        s.initConnection();
        s.setRequestBody("{\"address\":\" 10547 台北市 民生東路四段133號11樓\"}");
        s.execute();
        Asserts.check(s.getHttpStatus() == 200, "http status error " + s.getHttpStatus());
        System.out.println(s.getReceiveStringData());
    }

    @Test
    public void get() throws Exception {
        CapHttpService s = new CapHttpService();
        s.setProperty(Constants.HTTP_METHOD, HttpGet.METHOD_NAME);
        s.setProperty(Constants.HOST_URL, "https://www.google.com/");
        s.setProperty(Constants.HTTP_RETRY_COUNT, "3");
        s.setProperty(Constants.CONNECTION_TIMEOUT, "5000");
        s.setProperty(Constants.ASYNC, "false");
        s.initConnection();
        Map<String, String> map = new HashMap<String, String>();
        s.setRequestParams(map);
        s.execute();
        Asserts.check(s.getHttpStatus() == 200, "http status error " + s.getHttpStatus());
        System.out.println(s.getReceiveStringData());
    }
}
