package com.iauto.wlink.core.message.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.event.AuthenticationEvent;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;

public class SessionContextCheckHandler extends SimpleChannelInboundHandler<CommunicationPackage> {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	@Override
	protected void channelRead0( ChannelHandlerContext ctx, CommunicationPackage msg ) throws Exception {

		// 检查用户会话上下文是否已经建立
		SessionContextHandler sessionHandler = (SessionContextHandler) ctx.pipeline().get( "session_handler" );

		if ( sessionHandler.getSessionContext() != null ) {
			ctx.fireChannelRead( msg );
			return;
		}

		// debug
		// logger.debug( "Session has not been created, add the authentication handler to pipline." );

		// 添加认证处理器
		// ctx.pipeline().addAfter( SessionContextCodec.HANDLER_NAME, "auth",
		// new AuthenticationMessageCodec( new AuthWorker(
		// "9aROHg2eQXQ6X3XKrXGKWjXrLiRIO25CKTyz212ujvc" ) ) );

		// debug
		logger.debug( "Type of communication package: {}", msg.getType() );

		// 以下判断消息类型是否为认证类型
		// 如果是，则流转给认证处理器处理认证
		// 如果不是，则直接返回错误
		if ( StringUtils.equals( msg.getType(), "auth" ) ) {
			// 解码
			AuthMessage authMsg = AuthMessage.parseFrom( msg.getBody() );

			// 触发用户认证
			ctx.fireUserEventTriggered( new AuthenticationEvent( authMsg.getTicket() ) );
		} else {
			// 其余类型的消息将被忽略(不会继续流转到下一个处理器)，且返回未认证的错误消息

			ErrorMessage error = ErrorMessage.newBuilder()
				.setError( "UnAuthenticated" )
				.build();
			ctx.channel().writeAndFlush( error );
		}
	}
}
