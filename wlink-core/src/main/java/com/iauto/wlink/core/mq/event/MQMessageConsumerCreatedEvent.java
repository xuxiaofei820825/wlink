package com.iauto.wlink.core.mq.event;

import javax.jms.MessageConsumer;

public class MQMessageConsumerCreatedEvent {

	private final String sessionId;
	private final MessageConsumer consumer;

	public MQMessageConsumerCreatedEvent( String sessionId, MessageConsumer consumer ) {
		this.sessionId = sessionId;
		this.consumer = consumer;
	}

	// =============================================================
	// setter/getter

	public String getSessionId() {
		return sessionId;
	}

	public MessageConsumer getConsumer() {
		return consumer;
	}
}
