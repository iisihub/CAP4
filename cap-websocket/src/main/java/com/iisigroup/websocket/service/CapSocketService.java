package com.iisigroup.websocket.service;

import com.corundumstudio.socketio.SocketIOClient;

public interface CapSocketService {

	public SocketIOClient onConnectHandler(String sessionId);

}
