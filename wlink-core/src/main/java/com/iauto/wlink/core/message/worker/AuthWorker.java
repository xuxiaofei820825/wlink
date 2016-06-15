package com.iauto.wlink.core.message.worker;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.codec.SessionContextCodec;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;
import com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage;
import com.iauto.wlink.core.session.SessionContext;

public class AuthWorker implements MessageWorker {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 签名密匙 */
	private final String key;

	/** 业务线程池 */
	private static final ThreadPoolExecutor executor =
			new ThreadPoolExecutor( 1, 5, 30L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>( 1000 ) );

	public AuthWorker( final String key ) {
		this.key = key;
	}

	public void process( ChannelHandlerContext ctx, byte[] header, byte[] body ) throws Exception {
		// log
		logger.info( "Decoding the authentication message......" );

		// 解码
		AuthMessage message = AuthMessage.parseFrom( body );

		// 异步执行认证
		executor.execute( new AuthRunner( ctx, message.getTicket(), this.key ) );
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
		logger.info( "Processing the authentication. Received service ticket: {}", this.ticket );

		try {

			// log
			logger.info( "Creating the session context......" );

			Thread.sleep( 2000 );

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

			// 设置上下文
			SessionContextCodec sessionHandler = (SessionContextCodec) this.ctx.pipeline().get( "session" );
			sessionHandler.setSessionContext( new SessionContext( userId ), ctx );

			// 不再需要认证处理器了，删除掉
			this.ctx.pipeline().remove( "auth" );

			// log
			logger.info( "Finished to process the authentication." );
		} catch ( Exception e ) {
			// ignore
			logger.info( "Error occoured when processing the authentication.", e );
		}
	}
}
