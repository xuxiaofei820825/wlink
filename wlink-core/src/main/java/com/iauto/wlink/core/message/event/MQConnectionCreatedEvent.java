package com.iauto.wlink.core.message.event;

import org.apache.qpid.client.AMQConnection;

public class MQConnectionCreatedEvent {

	private final String userId;
	private final AMQConnection connection;
//	private final Session session;

	public MQConnectionCreatedEvent( AMQConnection connection,  String userId ) {
		//public MQSessionCreatedEvent( AMQConnection connection, Session session, String userId ) {
		this.connection = connection;
		this.userId = userId;
//		this.session = session;
	}

	// =============================================================
	// setter/getter

	public String getUserId() {
		return userId;
	}

//	public Session getSession() {
//		return session;
//	}

	public AMQConnection getConnection() {
		return connection;
	}
}
