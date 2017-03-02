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
		long expireTime = message.getExpiredTime();

		// 编码
		return com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage.newBuilder()
			.setId( id )
			.setTuid( message.getUid() )
			.setExpireTime( expireTime )
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
			sessionMsg.setUid( msg.getTuid() );
			sessionMsg.setExpiredTime( msg.getExpireTime() );
			sessionMsg.setSignature( msg.getSignature() );
		} catch ( InvalidProtocolBufferException e ) {
			throw new RuntimeException( e );
		}

		return sessionMsg;
	}
}
