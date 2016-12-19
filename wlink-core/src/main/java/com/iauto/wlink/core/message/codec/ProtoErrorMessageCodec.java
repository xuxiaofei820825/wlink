package com.iauto.wlink.core.message.codec;

import com.iauto.wlink.core.message.ErrorMessage;
import com.iauto.wlink.core.message.MessageCodec;

public class ProtoErrorMessageCodec implements MessageCodec<ErrorMessage> {

	public byte[] encode( ErrorMessage payload ) {
		return null;
	}

	public ErrorMessage decode( byte[] bytes ) {
		return null;
	}

}
