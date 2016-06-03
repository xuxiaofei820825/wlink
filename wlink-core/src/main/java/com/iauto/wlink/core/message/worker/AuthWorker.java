package com.iauto.wlink.core.message.worker;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.codec.AuthenticationMessageCodec;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;
import com.iauto.wlink.core.session.SessionContext;

public class AuthWorker implements MessageWorker {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 业务线程池 */
	private static final ThreadPoolExecutor executor =
			new ThreadPoolExecutor( 5, 10, 30L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>( 100 ) );

	public void process( ChannelHandlerContext ctx, byte[] body ) throws Exception {
		// log
		logger.info( "Decoding the authentication message......" );

		AuthMessage message = AuthMessage.parseFrom( body );
		executor.execute( new AuthRunner( ctx, message.getTicket() ) );
	}
}

class AuthRunner implements Runnable {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 通道处理上下文 */
	private final ChannelHandlerContext ctx;

	public AuthRunner( ChannelHandlerContext ctx, String ticket ) {
		this.ctx = ctx;
	}

	public void run() {

		// log
		logger.info( "Processing the authentication......" );

		// 模拟耗时的网络请求
		try {
			Thread.sleep( 3000 );
		}
		catch ( InterruptedException e ) {
			// ignore
		}

		// log
		logger.info( "Finished to process the authentication." );

		AuthenticationMessageCodec authHandler = (AuthenticationMessageCodec) this.ctx.pipeline().get( "auth" );
		authHandler.finish( new SessionContext( "xiaofei.xu" ) );
	}
}
