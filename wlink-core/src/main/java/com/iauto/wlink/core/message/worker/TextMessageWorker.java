package com.iauto.wlink.core.message.worker;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge;
import com.iauto.wlink.core.message.proto.TextMessageProto.TextMessage;
import com.iauto.wlink.core.message.router.MessageRouter;
import com.iauto.wlink.core.message.router.QpidMessageRouter;

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

	/** 消息处理器 */
	private MessageRouter router = new QpidMessageRouter();

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

			// 把消息路由给接收者
			router.send( this.txtMsg.getFrom(), txtMsg.getTo(), txtMsg.getText().getBytes() );

			// log
			logger.info( "Finished to process the text message." );

			// 创建表示成功的响应，并发送给客户端
			MessageAcknowledge ack = MessageAcknowledge.newBuilder()
				.setResult( MessageAcknowledge.Result.SUCCESS )
				.build();
			this.ctx.writeAndFlush( ack );
		}
		catch ( Exception e ) {
			// log
			logger.info( "Failed to process the text message!!!" );

			// 创建表示失败的响应，并发送给客户端
			MessageAcknowledge ack = MessageAcknowledge.newBuilder()
				.setResult( MessageAcknowledge.Result.FAILURE )
				.build();
			this.ctx.writeAndFlush( ack );
		}
	}
}
