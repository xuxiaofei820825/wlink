package com.iauto.wlink.core.session;

import com.iauto.wlink.core.Message;
import com.iauto.wlink.core.MessageCodec;
import com.iauto.wlink.core.session.proto.SessionMessageProto.SessionMessage;

/**
 * 实现会话的编解码器
 * 
 * @author xiaofei.xu
 * 
 */
public class DefaultSessionCodec implements MessageCodec<Session> {

	public byte[] encode( Session payload ) {
		String id = payload.getId();
		long userId = payload.getUserId();
		long timestamp = payload.getTimestamp();

		// 编码
		SessionMessage sessionMsg = SessionMessage.newBuilder()
			.setId( id )
			.setUserId( String.valueOf( userId ) )
			.setTimestamp( timestamp )
			.build();
		return sessionMsg.toByteArray();
	}

	public Message<Session> decode( byte[] bytes ) throws Exception {
		// 解码消息
		SessionMessage sessionMsg = SessionMessage.parseFrom( bytes );

		Session session = new Session( sessionMsg.getId(),
			Long.valueOf( sessionMsg.getUserId() ),
			sessionMsg.getTimestamp() );

		return new com.iauto.wlink.core.session.SessionMessage( session );
	}
}
