package com.iauto.wlink.core.message.worker;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.codec.AuthenticationMessageCodec;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;
import com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage;
import com.iauto.wlink.core.session.SessionContext;

public class AuthWorker implements MessageWorker {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 签名密匙 */
	private final String key;

	/** 业务线程池 */
	private static final ThreadPoolExecutor executor =
			new ThreadPoolExecutor( 5, 10, 30L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>( 100 ) );

	/**
	 * 构造函数
	 */
	public AuthWorker( final String key ) {
		this.key = key;
	}

	public void process( ChannelHandlerContext ctx, byte[] header, byte[] body ) throws Exception {
		// log
		logger.info( "Decoding the authentication message......" );

		AuthMessage message = AuthMessage.parseFrom( body );
		executor.execute( new AuthRunner( ctx, message.getTicket(), this.key ) );
	}
}

class AuthRunner implements Runnable {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

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
		logger.info( "Processing the authentication......" );

		try {

			// log
			logger.info( "Received service ticket: {}", this.ticket );

			// 模拟耗时的网络请求
			Thread.sleep( 3000 );

			final String userId = "xuxiaofei";
			final String timestamp = String.valueOf( System.currentTimeMillis() );

			String session = userId + ";" + timestamp;

			// log
			logger.info( "Creating the session context......" );

			// 生成签名
			SecretKeySpec signingKey = new SecretKeySpec( Base64.decodeBase64( key ), HMAC_SHA256 );
			Mac mac = Mac.getInstance( HMAC_SHA256 );
			mac.init( signingKey );
			byte[] rawHmac = mac.doFinal( session.getBytes() );

			String signature = Base64.encodeBase64URLSafeString( rawHmac );

			SessionMessage sessionMsg = SessionMessage.newBuilder()
				.setUserId( userId )
				.setTimestamp( timestamp )
				.setSignature( signature )
				.build();

			// 返回给客户端
			ctx.writeAndFlush( sessionMsg );

			// log
			logger.info( "Finished to process the authentication." );

			AuthenticationMessageCodec authHandler = (AuthenticationMessageCodec) this.ctx.pipeline().get( "auth" );
			authHandler.finish( new SessionContext( "xiaofei.xu" ) );
		}
		catch ( Exception e ) {
			// ignore
			
			logger.info( "Error occoured when processing the authentication." );
		}
	}
}
