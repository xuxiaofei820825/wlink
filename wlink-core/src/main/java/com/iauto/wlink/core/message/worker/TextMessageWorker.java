package com.iauto.wlink.core.message.worker;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge;
import com.iauto.wlink.core.message.proto.TextMessageProto.TextMessage;

public class TextMessageWorker implements MessageWorker {
	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 业务线程池 */
	private static final ThreadPoolExecutor executor =
			new ThreadPoolExecutor( 5, 10, 30L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>( 100 ) );

	public void process( final ChannelHandlerContext ctx, final byte[] data ) throws Exception {
		// log
		logger.info( "Decoding the text message......" );

		TextMessage message = TextMessage.parseFrom( data );
		executor.execute( new TextMessageRunner( ctx, message ) );
	}
}

class TextMessageRunner implements Runnable {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** Handler context */
	private ChannelHandlerContext ctx;

	/** 文本消息 */
	private TextMessage txtMsg;

	public TextMessageRunner( final ChannelHandlerContext ctx, final TextMessage txtMsg ) {
		this.ctx = ctx;
		this.txtMsg = txtMsg;
	}

	public void run() {

		// debug
		logger.debug( "A text message: [from:{}, to:{}, text:PROTECTED]",
			this.txtMsg.getFrom(), txtMsg.getTo() );

		// 模拟耗时的网络请求
		try {
			Thread.sleep( 3000 );
		}
		catch ( InterruptedException e ) {
			// ignore
		}

		// log
		logger.info( "Finished to process the text message." );

		MessageAcknowledge ack = MessageAcknowledge.newBuilder()
			.setResult( MessageAcknowledge.Result.SUCCESS )
			.build();

		this.ctx.writeAndFlush( ack );
	}
}
