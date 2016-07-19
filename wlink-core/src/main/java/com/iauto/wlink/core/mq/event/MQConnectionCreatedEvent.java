package com.iauto.wlink.core.mq.event;

import javax.jms.Connection;

import com.iauto.wlink.core.auth.SessionContext;

public class MQConnectionCreatedEvent {

	private final SessionContext session;
	private final Connection connection;

	public MQConnectionCreatedEvent( Connection connection, SessionContext session ) {
		this.connection = connection;
		this.session = session;
	}

	// =============================================================
	// setter/getter

	public SessionContext getSession() {
		return session;
	}

	public Connection getConnection() {
		return connection;
	}
}
