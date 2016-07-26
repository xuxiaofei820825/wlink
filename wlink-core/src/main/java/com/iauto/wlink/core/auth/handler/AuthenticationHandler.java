package com.iauto.wlink.core.auth.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant;
import com.iauto.wlink.core.auth.Session;
import com.iauto.wlink.core.auth.SessionContext;
import com.iauto.wlink.core.auth.SessionIdGenerator;
import com.iauto.wlink.core.auth.event.SessionContextEvent;
import com.iauto.wlink.core.auth.service.AuthenticationProvider;
import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.exception.AuthenticationException;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.tools.Executor;

/**
 * 进行用户身份认证<br/>
 * 判断当前通道是否已建立用户会话。
 * <ul>
 * <li>若未建立，则进行用户身份的认证。
 * <li>若已建立，则流转接收到的消息
 * <ul>
 * 
 * @author xiaofei.xu
 * 
 */
public class AuthenticationHandler extends SimpleChannelInboundHandler<CommunicationPackage> {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 会话键值 */
	public static final AttributeKey<Session> SessionKey =
			AttributeKey.newInstance( "session" );

	/** 认证提供者 */
	private final AuthenticationProvider provider;

	private final SessionIdGenerator idGenerator;

	public AuthenticationHandler( final AuthenticationProvider provider, SessionIdGenerator idGenerator ) {
		this.provider = provider;
		this.idGenerator = idGenerator;
	}

	@Override
	protected void channelRead0( ChannelHandlerContext ctx, CommunicationPackage msg ) throws Exception {

		// 获取用户会话
		Session session = ctx.channel().attr( SessionKey ).get();

		// 检查用户会话是否已建立
		// 如果已经建立，直接流转获取到的消息
		if ( session != null ) {
			ctx.fireChannelRead( msg );
			return;
		}

		// debug
		logger.debug( "Type of communication package: {}", msg.getType() );

		// 如果会话还未建立，则进行用户认证处理
		// 判断消息类型是否为认证类型
		if ( StringUtils.equals( msg.getType(), Constant.MessageType.Auth ) ) {
			// 如果是，则进行认证处理

			// 解码
			AuthMessage authMsg = AuthMessage.parseFrom( msg.getBody() );

			// 进行用户身份认证
			Executor.execute( new AuthenticationTask( ctx, authMsg.getTicket(), provider ) );
		} else {
			// 如果不是，则直接返回错误
			// 其余类型的消息将被忽略(不会继续流转到下一个处理器)，且返回未认证的错误消息

			ErrorMessage error = ErrorMessage.newBuilder()
				.setError( "UnAuthenticated" )
				.build();
			ctx.channel().writeAndFlush( error );
		}
	}

	/**
	 * 根据提供的认证票据，对用户进行身份认证
	 * 
	 * @author xiaofei.xu
	 * 
	 */
	private class AuthenticationTask implements Runnable {

		/** 通道处理上下文 */
		private final ChannelHandlerContext ctx;

		/** 认证者 */
		private final AuthenticationProvider provider;

		/** 票据 */
		private final String ticket;

		public AuthenticationTask( ChannelHandlerContext ctx, String ticket, AuthenticationProvider provider ) {
			this.ctx = ctx;
			this.ticket = ticket;
			this.provider = provider;
		}

		public void run() {
			// info
			logger.info( "Processing the authentication. ticket:{}", this.ticket );

			// 默认值
			long userId = 0;

			try {
				// 进行认证
				userId = provider.authenticate( ticket );

				// success log
				logger.info( "Succeed to authenticate the ticket. userId:{}", userId );

				// 创建会话
				Session session = new Session( idGenerator.generate(), userId, System.currentTimeMillis() );

				// 保存到Channel的附件中
				this.ctx.channel().attr( SessionKey ).set( session );

				// 创建会话上下文
				SessionContext sessionCtx = new SessionContext( session, ctx.channel() );
				// 触发用户会话创建成功的事件
				this.ctx.fireUserEventTriggered( new SessionContextEvent( sessionCtx ) );
			} catch ( AuthenticationException e ) {
				// ignore
				logger.info( "Error occoured when processing the authentication.", e );

				// 返回认证错误
				ErrorMessage error = ErrorMessage.newBuilder()
					.setError( "UnAuthenticated" )
					.build();
				ctx.channel().writeAndFlush( error );
			}
		}
	}
}
