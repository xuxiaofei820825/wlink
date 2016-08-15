package com.iauto.wlink.core.message;

public class DefaultBroadcastMessage<T> extends AbstractBroadcastMessage<T> {
	public DefaultBroadcastMessage( String type, T layload, long from ) {
		super( type, layload, from );
	}
}
