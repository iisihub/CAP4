/* 
 * CapSocketService.java
 * 
 * Copyright (c) 2011 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.websocket.service;

import com.corundumstudio.socketio.SocketIOClient;

public interface CapSocketService {

    public SocketIOClient onConnectHandler(String sessionId);

}
