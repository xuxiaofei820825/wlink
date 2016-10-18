package com.iauto.wlink.core.message;

public interface MessageCodec<T> {
	byte[] encode( T payload );

	T decode( byte[] bytes ) throws Exception;
}
