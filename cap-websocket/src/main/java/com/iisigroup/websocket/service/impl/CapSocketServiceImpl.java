package com.iisigroup.websocket.service.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.iisigroup.cap.service.AbstractService;
import com.iisigroup.websocket.server.CapNettyWebSocketServer;
import com.iisigroup.websocket.service.CapSocketService;

@Service
public class CapSocketServiceImpl extends AbstractService implements
		CapSocketService {

	@Resource
	private CapNettyWebSocketServer server;

	// @OnConnect
	// public void onConnectHandler(SocketIOClient client) {
	//
	// }
	//
	// @OnEvent("xxx")
	// public void onAddEventHandler(SocketIOClient client, NotifyObject data,
	// AckRequest ackRequest) {
	//
	// }

	@Override
	public SocketIOClient onConnectHandler(String sessionId) {
		SocketIOServer socket = server.getServer();
		if (socket != null) {
			Collection<SocketIOClient> clients = socket.getAllClients();
			for (SocketIOClient c : clients) {
				if (c.getSessionId().toString().equals(sessionId)) {
					return c;
				}
			}
		}
		return null;
	}

}
