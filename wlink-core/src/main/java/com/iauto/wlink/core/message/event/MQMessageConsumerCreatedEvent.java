package com.iauto.wlink.core.message.event;

import javax.jms.MessageConsumer;

public class MQMessageConsumerCreatedEvent {

	private final String userId;
	private final MessageConsumer consumer;

	public MQMessageConsumerCreatedEvent( String userId, MessageConsumer consumer ) {
		this.userId = userId;
		this.consumer = consumer;
	}

	// =============================================================
	// setter/getter

	public String getUserId() {
		return userId;
	}

	public MessageConsumer getConsumer() {
		return consumer;
	}
}
