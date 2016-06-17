package com.iauto.wlink.core.message.event;

import javax.jms.Session;

public class MQSessionCreatedEvent {

	private final String userId;
	private final Session session;

	public MQSessionCreatedEvent( Session session, String userId ) {
		this.userId = userId;
		this.session = session;
	}

	public String getUserId() {
		return userId;
	}

	public Session getSession() {
		return session;
	}
}
