package com.iauto.wlink.core.message.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.Executor;
import com.iauto.wlink.core.message.event.SessionContextEvent;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage;
import com.iauto.wlink.core.session.SessionContext;

/**
 * 判断当前用户是否已建立会话。如果未建立，进行用户身份的认证。
 * 
 * @author xiaofei.xu
 * 
 */
public class AuthenticationHandler extends SimpleChannelInboundHandler<CommunicationPackage> {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 会话键 */
	public static final AttributeKey<SessionContext> SessionKey =
			AttributeKey.newInstance( "session" );

	/** 签名密匙 */
	private final String key;

	public AuthenticationHandler( final String key ) {
		this.key = key;
	}

	@Override
	protected void channelRead0( ChannelHandlerContext ctx, CommunicationPackage msg ) throws Exception {

		// 获取用户会话
		SessionContext session = ctx.channel().attr( SessionKey ).get();

		// 检查用户会话是否存在
		// 如果已经存在，则直接流转消息
		if ( session != null ) {
			ctx.fireChannelRead( msg );
			return;
		}

		// 以下判断消息类型是否为认证类型
		// 如果是，则进行认证处理
		// 如果不是，则直接返回错误
		if ( StringUtils.equals( msg.getType(), "auth" ) ) {

			// info
			logger.info( "Type of communication package: {}", msg.getType() );

			// 解码
			AuthMessage authMsg = AuthMessage.parseFrom( msg.getBody() );

			// 进行用户身份认证
			Executor.execute( new AuthenticationRunner( ctx, authMsg.getTicket(), key ) );
		} else {
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
	private class AuthenticationRunner implements Runnable {

		/** 签名算法 */
		private final static String HMAC_SHA256 = "HmacSHA256";

		/** 通道处理上下文 */
		private final ChannelHandlerContext ctx;

		/** 票据 */
		private final String ticket;

		/** 签名密匙 */
		private final String key;

		public AuthenticationRunner( ChannelHandlerContext ctx, String ticket, String key ) {
			this.ctx = ctx;
			this.ticket = ticket;
			this.key = key;
		}

		public void run() {
			// log
			logger.info( "Processing the authentication. ticket:{}", this.ticket );

			try {

				// log
				logger.info( "Creating the session context......" );

				// 模拟耗时的网络请求
				Thread.sleep( 1000 );

				// 生成用来进行签名的字符串
				final String userId = StringUtils.replace( ticket, "T", "U" );
				final String timestamp = String.valueOf( System.currentTimeMillis() );
				String sessionContent = userId + ";" + timestamp;

				// 生成会话信息的签名
				SecretKeySpec signingKey = new SecretKeySpec( Base64.decodeBase64( key ), HMAC_SHA256 );
				Mac mac = Mac.getInstance( HMAC_SHA256 );
				mac.init( signingKey );
				byte[] rawHmac = mac.doFinal( sessionContent.getBytes() );

				// 创建会话上下文对象，并返回给终端
				// 终端可使用会话上下文重新建立与服务器的会话
				SessionMessage sessionMsg = SessionMessage.newBuilder()
					.setUserId( userId )
					.setTimestamp( timestamp )
					.setSignature( Base64.encodeBase64URLSafeString( rawHmac ) )
					.build();

				// 返回给终端
				this.ctx.writeAndFlush( sessionMsg );

				// 创建会话上下文
				SessionContext session = new SessionContext( userId, ctx.channel() );

				// 保存到上下文中
				this.ctx.channel().attr( SessionKey ).set( session );

				// 触发用户会话创建成功的事件
				this.ctx.fireUserEventTriggered( new SessionContextEvent( session ) );

				// success log
				logger.info( "Succeed to process the authentication of user:{}", userId );
			} catch ( Exception e ) {
				// ignore
				logger.info( "Error occoured when processing the authentication.", e );
			}
		}
	}
}
