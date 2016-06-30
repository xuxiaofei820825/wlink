package com.iauto.wlink.core.message.event;

import org.apache.qpid.client.AMQConnection;

import com.iauto.wlink.core.session.SessionContext;

public class MQConnectionCreatedEvent {

	private final SessionContext session;
	private final AMQConnection connection;

	public MQConnectionCreatedEvent( AMQConnection connection, SessionContext session ) {
		this.connection = connection;
		this.session = session;
	}

	// =============================================================
	// setter/getter

	public SessionContext getSession() {
		return session;
	}

	public AMQConnection getConnection() {
		return connection;
	}
}
