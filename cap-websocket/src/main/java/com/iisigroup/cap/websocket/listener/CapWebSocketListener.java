/* 
 * CapWebSocketListener.java
 * 
 * Copyright (c) 2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.websocket.listener;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.websocket.server.CapNettyWebSocketServer;

/**
 * <pre>
 * TODO Write a short description on the purpose of the program
 * </pre>
 * 
 * @since 2018年3月21日
 * @author shawnyhw6n9
 * @version
 *          <ul>
 *          <li>2018年3月21日,shawnyhw6n9,new
 *          </ul>
 */
@Component
public class CapWebSocketListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CapWebSocketListener.class);

    @Resource
    private CapNettyWebSocketServer server;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Get CapNettyWebSocketServer bean." + server.getServer().hashCode());
        }
        LOGGER.info("SocketIOServer stop....");
        server.getServer().stop();
    }

}
