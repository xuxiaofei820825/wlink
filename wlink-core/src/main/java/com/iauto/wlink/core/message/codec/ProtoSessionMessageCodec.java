package com.iauto.wlink.core.message.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.SessionMessage;

public class ProtoSessionMessageCodec implements MessageCodec<SessionMessage> {

	/**
	 * 构造函数
	 * 
	 * @param signHandler
	 *          会话签名处理器
	 */
	public ProtoSessionMessageCodec() {
	}

	public byte[] encode( SessionMessage message ) {
		String id = message.getId();
		long timestamp = message.getTimestamp();

		// 编码
		return com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage.newBuilder()
			.setId( id )
			.setUuid( message.getUuid() )
			.setTimestamp( timestamp )
			.setSignature( message.getSignature() )
			.build().toByteArray();
	}

	public SessionMessage decode( byte[] bytes ) {
		SessionMessage sessionMsg = null;
		try {
			// 解码消息
			com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage msg =
					com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage.parseFrom( bytes );

			sessionMsg = new SessionMessage();
			sessionMsg.setId( msg.getId() );
			sessionMsg.setUuid( msg.getUuid() );
			sessionMsg.setTimestamp( msg.getTimestamp() );
			sessionMsg.setSignature( msg.getSignature() );
		} catch ( InvalidProtocolBufferException e ) {
			throw new RuntimeException( e );
		}

		return sessionMsg;
	}
}
