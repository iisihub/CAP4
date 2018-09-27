package com.iisigroup.colabase.demo.sslclient.model;

import com.iisigroup.colabase.model.PostFormData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iisigroup.colabase.model.RequestContent;
import com.iisigroup.colabase.model.ResponseContent;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/4/10 AndyChen,new
 *          </ul>
 * @since 2018/4/10
 */
public class DemoPostDataRequestContent extends PostFormData {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void afterSendRequest(ResponseContent responseContent) {
        logger.debug("Do something with DB...");
    }

    @Override
    public String getJsonString() {
        return super.getRequestContent().toString();
    }
}
