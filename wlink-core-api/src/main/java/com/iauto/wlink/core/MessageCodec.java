package com.iauto.wlink.core;

public interface MessageCodec<T> {
	byte[] encode( T payload );

	Message<T> decode( byte[] bytes ) throws Exception;
}
