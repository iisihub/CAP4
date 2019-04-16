package com.iisigroup.colabase.demo.sslclient.model;

import com.iisigroup.colabase.json.annotation.ApiRequest;
import com.iisigroup.colabase.json.service.SslClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iisigroup.colabase.json.model.PostFormData;
import com.iisigroup.colabase.json.model.ResponseContent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/4/10 AndyChen,new
 *          </ul>
 * @since 2018/4/10
 */
public class DemoPostDataRequestContent extends PostFormData {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SslClient sslClient;

    @ApiRequest
    private String test1;
    @ApiRequest
    private String test2;
    @ApiRequest
    private String test3;

    @Override
    public void afterSendRequest(ResponseContent responseContent) {
        logger.debug("Do something with DB...");
    }

    @Override
    public String getJsonString() {
        return super.getRequestContent().toString();
    }

    public String getTest1() {
        return test1;
    }

    public void setTest1(String test1) {
        this.test1 = test1;
    }

    public String getTest2() {
        return test2;
    }

    public void setTest2(String test2) {
        this.test2 = test2;
    }

    public String getTest3() {
        return test3;
    }

    public void setTest3(String test3) {
        this.test3 = test3;
    }
}
