package com.iauto.wlink.core.message;

public class DefaultCommMessage<T> extends AbstractCommMessage<T> {

	public DefaultCommMessage( String type, T payload, long from, long to ) {
		super( type, payload, from, to );
	}
}
