package com.iauto.wlink.core.message;

import com.iauto.wlink.core.exception.MessageCodecException;

public interface MessageCodec<T> {
	byte[] encode( T payload );

	T decode( byte[] bytes ) throws MessageCodecException;
}
