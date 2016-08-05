package com.iauto.wlink.core.message.event;

import javax.jms.Connection;

import com.iauto.wlink.core.session.SessionContext;

public class QpidConnectionCreatedEvent {

	private final SessionContext sessionContext;
	private final Connection connection;

	public QpidConnectionCreatedEvent( Connection connection, SessionContext sessionContext ) {
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
