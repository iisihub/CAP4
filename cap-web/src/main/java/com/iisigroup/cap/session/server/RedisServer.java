/* 
 * RedisServer.java
 * 
 * Copyright (c) 2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.session.server;

import java.io.IOException;

/**
 * <pre>
 * Start a embedded redis server
 * </pre>
 * 
 * @since 2018年8月2日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2018年8月2日,Sunkist,new
 *          </ul>
 */
public class RedisServer {
    private final redis.embedded.RedisServer redisServer;

    public RedisServer() throws IOException {
        redisServer = new redis.embedded.RedisServer(6379);
        // redis-server
        redisServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                // redis-cli shutdown
                redisServer.stop();
            }
        });
    }

    public redis.embedded.RedisServer getServer() {
        return redisServer;
    }
}
