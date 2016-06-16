package com.iauto.wlink.core.message.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.event.AuthenticationEvent;
import com.iauto.wlink.core.message.event.SessionContextEvent;
import com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage;
import com.iauto.wlink.core.session.SessionContext;

public class AuthenticationHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 签名密匙 */
	private final String key;

	/** 认证业务线程池 */
	private static final ThreadPoolExecutor executor =
			new ThreadPoolExecutor( 5, 10, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>( 1000 ) );

	public AuthenticationHandler( final String key ) {
		this.key = key;
	}

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		// 判断当前用户事件是否为AuthenticationEvent
		if ( evt instanceof AuthenticationEvent ) {

			// info
			logger.info( "Processing the authentication...... Channel:{}", ctx.channel() );

			AuthenticationEvent event = (AuthenticationEvent) evt;
			String ticket = event.getTicket();

			// 为用户执行消息监听
			executor.execute( new AuthRunner( ctx, ticket, this.key ) );

			return;
		}

		// 流转不能处理的事件
		super.userEventTriggered( ctx, evt );
	}
}

class AuthRunner implements Runnable {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 签名算法 */
	private final static String HMAC_SHA256 = "HmacSHA256";

	/** 通道处理上下文 */
	private final ChannelHandlerContext ctx;

	/** 票据 */
	private final String ticket;

	/** 签名密匙 */
	private final String key;

	public AuthRunner( ChannelHandlerContext ctx, String ticket, String key ) {
		this.ctx = ctx;
		this.ticket = ticket;
		this.key = key;
	}

	public void run() {
		// log
		logger.info( "Processing the authentication. ST:{}", this.ticket );

		try {

			// log
			logger.info( "Creating the session context......" );

			// 模拟耗时的网络请求
			Thread.sleep( 1000 );

			// 生成用来进行签名的字符串
			final String userId = StringUtils.replace( ticket, "T", "U" );
			final String timestamp = String.valueOf( System.currentTimeMillis() );
			String session = userId + ";" + timestamp;

			// 生成会话信息的签名
			SecretKeySpec signingKey = new SecretKeySpec( Base64.decodeBase64( key ), HMAC_SHA256 );
			Mac mac = Mac.getInstance( HMAC_SHA256 );
			mac.init( signingKey );
			byte[] rawHmac = mac.doFinal( session.getBytes() );

			// 创建会话上下文对象，并返回给终端
			// 终端可使用会话上下文重新建立与服务器的会话
			SessionMessage sessionMsg = SessionMessage.newBuilder()
				.setUserId( userId )
				.setTimestamp( timestamp )
				.setSignature( Base64.encodeBase64URLSafeString( rawHmac ) )
				.build();

			// 返回给终端
			ctx.writeAndFlush( sessionMsg );

			// 发送设置会话上下文的事件
			ctx.fireUserEventTriggered( new SessionContextEvent( new SessionContext( userId ) ) );

			// 不再需要认证处理器了，删除掉
			this.ctx.pipeline().remove( "auth" );

			// log
			logger.info( "Succeed to process the authentication. userID:{}", userId );
		} catch ( Exception e ) {
			// ignore
			logger.info( "Error occoured when processing the authentication.", e );
		}
	}
}
