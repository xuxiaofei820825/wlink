package com.iauto.wlink.core.message.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import com.iauto.wlink.core.message.ErrorMessage;
import com.iauto.wlink.core.message.MessageCodec;

public class ProtoErrorMessageCodec implements MessageCodec<ErrorMessage> {

	public byte[] encode( ErrorMessage payload ) {
		// 编码
		return com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage.newBuilder()
				.setCode( payload.getCode() )
				.build().toByteArray();
	}

	public ErrorMessage decode( byte[] bytes ) {

		ErrorMessage errorMsg = null;
		try {
			// 解码消息
			com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage msg =
					com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage.parseFrom( bytes );

			errorMsg = new ErrorMessage( msg.getCode() );
		}
		catch ( InvalidProtocolBufferException e ) {
			throw new RuntimeException( e );
		}

		return errorMsg;
	}

}
