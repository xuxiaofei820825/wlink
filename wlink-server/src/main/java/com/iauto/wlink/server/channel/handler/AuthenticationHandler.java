package com.iauto.wlink.server.channel.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.auth.Authentication;
import com.iauto.wlink.core.auth.AuthenticationProvider;
import com.iauto.wlink.core.auth.TicketAuthentication;
import com.iauto.wlink.core.comm.CommunicationPayload;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.SessionMessage;
import com.iauto.wlink.core.message.TerminalMessageRouter;
import com.iauto.wlink.core.message.TicketAuthMessage;
import com.iauto.wlink.core.message.codec.ProtoSessionMessageCodec;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionIdGenerator;
import com.iauto.wlink.core.session.SessionSignatureHandler;
import com.iauto.wlink.server.Constant;
import com.iauto.wlink.server.channel.SessionManager;

/**
 * <p>
 * 进行终端认证处理。通过检查通道的会话值是否存在，判断通道是否已完成认证处理。如果当前通道会话不存在，则进行认证处理。<br/>
 * </p>
 * 认证可以是以下3种情况:
 * <ul>
 * <li>终端传递的是Session消息，需要使用该消息进行会话重建</li>
 * <li>终端传递的是认证票据，需要对票据的有效性进行验证。如果有效才建立会话。</li>
 * <li>以上两种情况都不是，则返回错误消息</li>
 * </ul>
 * 
 * @author xiaofei.xu
 */
@Sharable
public class AuthenticationHandler extends SimpleChannelInboundHandler<CommunicationPayload> implements
		InitializingBean {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 认证消息编解码器 */
	private MessageCodec<? extends TicketAuthMessage> authMessageCodec;

	/** 会话消息编解码器 */
	private ProtoSessionMessageCodec sessionMessageCodec;

	/** 认证提供者 */
	private final AuthenticationProvider provider;

	/** 会话编号生成器 */
	private final SessionIdGenerator idGenerator;

	/** 会话签名处理器 */
	private SessionSignatureHandler signHandler;

	private TerminalMessageRouter messageRouter;

	/** 执行线程池 */
	private ExecutorService executors;

	public void afterPropertiesSet() throws Exception {
		Assert.notNull( authMessageCodec, "Authentication codec is required." );
		Assert.notNull( sessionMessageCodec, "Session message codec is required." );
		Assert.notNull( provider, "Authentication provider is required." );
		Assert.notNull( idGenerator, "Session id generator is required." );
		Assert.notNull( signHandler, "Session signature handler is required." );
		Assert.notNull( messageRouter, "Session signature handler is required." );
	}

	/**
	 * 构造函数
	 * 
	 * @param provider
	 * @param idGenerator
	 */
	public AuthenticationHandler( final AuthenticationProvider provider, SessionIdGenerator idGenerator, int nthread ) {
		this.provider = provider;
		this.idGenerator = idGenerator;

		executors = Executors.newFixedThreadPool( nthread );
	}

	@Override
	protected void channelRead0( final ChannelHandlerContext ctx, CommunicationPayload msg ) throws Exception {

		// 判断终端会话是否已经建立
		Session session = ctx.channel().attr( Constant.SessionKey ).get();

		// 如果已经建立，直接流转获取到的消息
		if ( session != null ) {
			// debug log
			logger.debug( "Session has been created. ID: {}, UUID: {}", session.getId(), session.getUuId() );

			ctx.fireChannelRead( msg );
			return;
		}

		// info log
		logger.info( "Type of communication package: {}", msg.getType() );

		// 重建会话
		boolean isSessionMsg = MessageType.Session.equals( msg.getType() );
		if ( isSessionMsg ) {
			rebuildSession( ctx, msg );
			return;
		}

		// 如果会话还未建立，则进行终端认证处理
		// 判断消息类型是否为认证类型
		boolean isAuthMessage = MessageType.Auth.equals( msg.getType() );
		if ( isAuthMessage ) {
			// 如果是，则进行认证处理
			authenticate( ctx, msg );
			return;
		}

		// 如果不是，则直接返回错误
		// 其余类型的消息将被忽略(不会继续流转到下一个处理器)，且返回未认证的错误消息
		ErrorMessage error = ErrorMessage.newBuilder()
			.setError( "UnAuthenticated" )
			.build();
		ctx.channel().writeAndFlush( error );
	}

	/*
	 * 重建终端会话
	 */
	private void rebuildSession( final ChannelHandlerContext ctx, CommunicationPayload msg ) {
		// 解码会话消息
		SessionMessage sessionMsg = sessionMessageCodec.decode( msg.getPayload() );

		// 创建会话实例
		String sessionId = idGenerator.generate();
		Session session = new Session( sessionId,
			sessionMsg.getUuid(), System.currentTimeMillis() );

		// info log
		logger.info( "Rebuild session context. ID: {}, UUID: {}", sessionId, sessionMsg.getUuid() );

		// 保存到Channel的附件中
		ctx.channel()
			.attr( Constant.SessionKey ).set( session );

		SessionManager.add( sessionMsg.getUuid(), sessionId, ctx.channel() );
	}

	/*
	 * 对票据进行认证
	 */
	private void authenticate( final ChannelHandlerContext ctx, CommunicationPayload msg ) throws Exception {
		// 解码
		TicketAuthMessage ticketMsg = authMessageCodec.decode( msg.getPayload() );
		TicketAuthentication authentication = new TicketAuthentication( ticketMsg.getTicket() );

		// info log
		logger.info( "Authenticating the ticket......" );

		// 执行异步任务
		ListenableFuture<Authentication> future = null;
		future = MoreExecutors.listeningDecorator( executors )
			.submit( new AuthenticationTask( authentication ) );

		Futures.addCallback( future, new FutureCallback<Authentication>() {
			public void onSuccess( Authentication result ) {
				// 失败
				logger.info( "Succeed to process authentication." );

				// 成功
				long userId = result.principal();

				// 创建会话
				String sessionId = idGenerator.generate();
				Session session = new Session( sessionId,
					String.valueOf( userId ), System.currentTimeMillis() );

				// 保存到Channel的附件中
				ctx.channel()
					.attr( Constant.SessionKey ).set( session );

				// info log
				logger.info( "Starting to subscribe message of terminal(UID:{})", session.getUuId() );

				// 订阅终端消息
				ListenableFuture<?> future = messageRouter.subscribe( String.valueOf( userId ) );
				Futures.addCallback( future, new FutureCallback<Object>() {
					public void onSuccess( Object result ) {
						// info log
						logger.info( "Succeed to subscribe message." );
					}

					public void onFailure( Throwable t ) {
						// info log
						logger.info( "Failed to subscribe message.", t );
					}
				} );

				SessionManager.add( String.valueOf( userId ), sessionId, ctx.channel() );

				// 创建会话上下文对象，并返回给终端
				// 终端可使用会话上下文重新建立与服务器的会话
				long timestamp = System.currentTimeMillis();
				String signature = signHandler.sign( session );

				SessionMessage sessionMsg = new SessionMessage();
				sessionMsg.setId( session.getId() );
				sessionMsg.setSignature( signature );
				sessionMsg.setTimestamp( timestamp );
				sessionMsg.setUuid( String.valueOf( userId ) );

				byte[] byts_session = sessionMessageCodec.encode( sessionMsg );

				// 封装成通讯消息
				CommunicationPayload comm = new CommunicationPayload();
				comm.setType( MessageType.Session );
				comm.setPayload( byts_session );

				// 把签名后的会话上下文返回给终端
				ctx.channel().writeAndFlush( comm );
			}

			public void onFailure( Throwable t ) {
				// 失败
				logger.info( "Error occourred when processing the authentication.", t );

				// 返回认证错误
				ErrorMessage error = ErrorMessage.newBuilder()
					.setError( "UnAuthenticated" )
					.build();
				ctx.channel().writeAndFlush( error );
			}
		} );
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

	public SessionSignatureHandler getSignHandler() {
		return signHandler;
	}

	public void setSignHandler( SessionSignatureHandler signHandler ) {
		this.signHandler = signHandler;
	}

	public void setSessionMessageCodec( ProtoSessionMessageCodec sessionMessageCodec ) {
		this.sessionMessageCodec = sessionMessageCodec;
	}

	public void setAuthMessageCodec( MessageCodec<? extends TicketAuthMessage> authMessageCodec ) {
		this.authMessageCodec = authMessageCodec;
	}

	public void setMessageRouter( TerminalMessageRouter messageRouter ) {
		this.messageRouter = messageRouter;
	}

}
