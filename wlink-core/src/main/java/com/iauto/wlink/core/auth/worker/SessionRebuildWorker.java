package com.iauto.wlink.core.auth.worker;

import io.netty.channel.ChannelHandlerContext;

import com.iauto.wlink.core.MessageWorker;
import com.iauto.wlink.core.auth.SessionContext;
import com.iauto.wlink.core.auth.event.SessionContextEvent;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage;

public class SessionRebuildWorker implements MessageWorker {

	/** 签名密匙 */
	private final String key;

	public SessionRebuildWorker( String key ) {
		this.key = key;
	}

	public void process( ChannelHandlerContext ctx, byte[] header, byte[] body ) throws Exception {

		// 解码消息
		SessionMessage session = SessionMessage.parseFrom( body );

		// 创建会话上下文
		SessionContext context = new SessionContext(
			session.getId(),
			session.getUserId(),
			ctx.channel() );
		context.setTimestamp( session.getTimestamp() );

		// 对Session的签名进行验证
		if ( SessionContext.validate( key, context, session.getSignature() ) ) {
			ctx.fireUserEventTriggered( new SessionContextEvent( context ) );
		} else {
			// 签名验证不通过，返回错误信息

			ErrorMessage error = ErrorMessage.newBuilder()
				.setError( "UnAuthenticated" )
				.build();
			ctx.channel().writeAndFlush( error );
		}
	}

	// =================================================================================
	// setter/getter

	public String getKey() {
		return key;
	}
}
