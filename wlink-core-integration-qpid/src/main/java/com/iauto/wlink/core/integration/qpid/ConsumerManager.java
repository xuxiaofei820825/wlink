package com.iauto.wlink.core.integration.qpid;

import java.util.HashMap;
import java.util.Map;

import javax.jms.MessageConsumer;

public class ConsumerManager {

	/** 会话编号与消息监听器的对应 */
	private final static Map<String, MessageConsumer> consumers = new HashMap<String, MessageConsumer>();

	public static void add( String id, MessageConsumer consumer ) {
		consumers.put( id, consumer );
	}

	public static MessageConsumer get( String id ) {
		return consumers.get( id );
	}

	public static void remove( String id ) {
		consumers.remove( id );
	}
}
