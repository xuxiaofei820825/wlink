package com.iauto.wlink.server.auth.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.Message;
import com.iauto.wlink.core.MessageCodec;
import com.iauto.wlink.core.auth.Authentication;
import com.iauto.wlink.core.auth.AuthenticationProvider;
import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionContext;
import com.iauto.wlink.core.session.SessionIdGenerator;
import com.iauto.wlink.core.session.SessionSignatureHandler;
import com.iauto.wlink.core.session.proto.SessionMessageProto.SessionMessage;
import com.iauto.wlink.server.session.event.SessionContextEvent;

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

	/** 认证信息编解码器 */
	private MessageCodec<? extends Authentication> messageCodec;

	/** 认证提供者 */
	private final AuthenticationProvider provider;

	/** 会话编号生成器 */
	private final SessionIdGenerator idGenerator;

	/** 会话签名处理器 */
	private SessionSignatureHandler signHandler;

	public AuthenticationHandler( final AuthenticationProvider provider, SessionIdGenerator idGenerator ) {
		this.provider = provider;
		this.idGenerator = idGenerator;
	}

	@Override
	protected void channelRead0( final ChannelHandlerContext ctx, CommunicationPackage msg ) throws Exception {

		// 获取用户会话是否已经建立
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
		if ( MessageType.Auth.equals( msg.getType() ) ) {
			// 如果是，则进行认证处理

			// 解码
			Message<? extends Authentication> authMsg = messageCodec.decode( msg.getBody() );

			// 执行异步任务
			ListenableFuture<Authentication> future = null;
			future = MoreExecutors.listeningDecorator( Executors.newFixedThreadPool( 10 ) )
				.submit( new AuthenticationTask( authMsg.payload() ) );

			Futures.addCallback( future, new FutureCallback<Authentication>() {
				public void onSuccess( Authentication result ) {
					// 成功

					long userId = result.principal();

					// 创建会话
					Session session = new Session( idGenerator.generate(),
						userId, System.currentTimeMillis() );

					// 保存到Channel的附件中
					ctx.channel()
						.attr( SessionKey ).set( session );

					// 创建会话上下文对象，并返回给终端
					// 终端可使用会话上下文重新建立与服务器的会话
					long timestamp = System.currentTimeMillis();
					String signature = signHandler.sign( session );
					SessionMessage sessionMsg = SessionMessage.newBuilder()
						.setId( session.getId() )
						.setUserId( String.valueOf( userId ) )
						.setTimestamp( timestamp )
						.setSignature( signature )
						.build();

					// 把签名后的会话上下文返回给终端
					ctx.channel().writeAndFlush( sessionMsg );

					// 创建会话上下文
					SessionContext sessionCtx = new SessionContext( session, ctx.channel() );

					// 触发用户会话创建成功的事件
					ctx.fireUserEventTriggered( new SessionContextEvent( sessionCtx ) );
				}

				public void onFailure( Throwable t ) {
					// 失败

					// info
					logger.info( "Error occoured when processing the authentication.", t );

					// 返回认证错误
					ErrorMessage error = ErrorMessage.newBuilder()
						.setError( "UnAuthenticated" )
						.build();
					ctx.channel().writeAndFlush( error );
				}
			} );

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
	 */
	private class AuthenticationTask implements Callable<Authentication> {

		/** 需要认证的内容 */
		private final Authentication authentication;

		public AuthenticationTask( Authentication authentication ) {
			this.authentication = authentication;
		}

		public Authentication call() throws Exception {
			return provider.authenticate( authentication );
		}
	}

	// ===========================================================================
	// setter/getter

	public MessageCodec<? extends Authentication> getMessageCodec() {
		return messageCodec;
	}

	public void setMessageCodec( MessageCodec<? extends Authentication> messageCodec ) {
		this.messageCodec = messageCodec;
	}

	public SessionSignatureHandler getSignHandler() {
		return signHandler;
	}

	public void setSignHandler( SessionSignatureHandler signHandler ) {
		this.signHandler = signHandler;
	}
}
