package com.iisigroup.colabase.demo.sslclient.model;

import com.iisigroup.colabase.model.RequestContent;
import com.iisigroup.colabase.model.ResponseContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/4/10 AndyChen,new
 *          </ul>
 * @since 2018/4/10
 */
public class DemoRequestContent extends RequestContent {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void afterSendRequest(ResponseContent responseContent) {
        logger.debug("Do something with DB...");
    }
}
