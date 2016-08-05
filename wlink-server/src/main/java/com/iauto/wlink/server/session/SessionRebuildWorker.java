package com.iauto.wlink.server.session;

import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.MessageWorker;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionContext;
import com.iauto.wlink.core.session.SessionSignatureHandler;
import com.iauto.wlink.core.session.proto.SessionMessageProto.SessionMessage;
import com.iauto.wlink.server.session.event.SessionContextEvent;

public class SessionRebuildWorker implements MessageWorker {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 签名处理器 */
	private final SessionSignatureHandler signHandler;

	public SessionRebuildWorker( SessionSignatureHandler signHandler ) {
		this.signHandler = signHandler;
	}

	public void process( ChannelHandlerContext ctx, byte[] header, byte[] body ) throws Exception {

		// 解码消息
		SessionMessage sessionMsg = SessionMessage.parseFrom( body );

		// 创建会话上下文
		Session session = new Session( sessionMsg.getId(),
			Long.valueOf( sessionMsg.getUserId() ),
			sessionMsg.getTimestamp() );
		SessionContext context = new SessionContext( session, ctx.channel() );

		// 对Session的签名进行验证
		if ( signHandler.validate( session, sessionMsg.getSignature() ) ) {
			// info
			logger.info( "Signature of session is valid, try to rebuild session context of user[ID:{}].",
				session.getUserId() );

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
}
