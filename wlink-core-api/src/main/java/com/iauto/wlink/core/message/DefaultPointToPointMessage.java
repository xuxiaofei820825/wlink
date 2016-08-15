package com.iauto.wlink.core.message;

public class DefaultPointToPointMessage<T> extends AbstractPointToPointMessage<T> {

	public DefaultPointToPointMessage( String type, T payload, long from, long to ) {
		super( type, payload, from, to );
	}
}
