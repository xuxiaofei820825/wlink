package com.iauto.wlink.core.mq.event;

import javax.jms.MessageConsumer;

import com.iauto.wlink.core.auth.SessionContext;

public class MQMessageConsumerCreatedEvent {

	private final SessionContext session;
	private final MessageConsumer consumer;

	public MQMessageConsumerCreatedEvent( SessionContext session, MessageConsumer consumer ) {
		this.session = session;
		this.consumer = consumer;
	}

	// =============================================================
	// setter/getter

	public SessionContext getSession() {
		return session;
	}

	public MessageConsumer getConsumer() {
		return consumer;
	}
}
