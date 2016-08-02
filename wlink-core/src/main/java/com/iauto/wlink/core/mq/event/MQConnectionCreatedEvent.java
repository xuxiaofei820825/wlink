package com.iauto.wlink.core.mq.event;

import javax.jms.Connection;

import com.iauto.wlink.core.session.SessionContext;

public class MQConnectionCreatedEvent {

	private final SessionContext sessionContext;
	private final Connection connection;

	public MQConnectionCreatedEvent( Connection connection, SessionContext sessionContext ) {
		this.connection = connection;
		this.sessionContext = sessionContext;
	}

	// =============================================================
	// setter/getter

	public SessionContext getSessionContext() {
		return sessionContext;
	}

	public Connection getConnection() {
		return connection;
	}
}
