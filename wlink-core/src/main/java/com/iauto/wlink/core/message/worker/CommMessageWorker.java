package com.iauto.wlink.core.message.worker;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.proto.CommMessageHeaderProto.CommMessageHeader;
import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge;
import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType;
import com.iauto.wlink.core.message.router.QpidMessageSender;

public class CommMessageWorker implements MessageWorker {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 业务线程池 */
	private static final ThreadPoolExecutor executor =
			new ThreadPoolExecutor( 5, 10, 30L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>( 10000 ) );

	public void process( final ChannelHandlerContext ctx, final byte[] header, final byte[] body ) throws Exception {
		// debug
		logger.debug( "Decoding the text message......" );

		executor.execute( new CommMessageRunner( ctx, header, body ) );
	}
}

class CommMessageRunner implements Runnable {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** Handler context */
	private ChannelHandlerContext ctx;

	/** 文本消息 */
	private byte[] header;
	private byte[] body;

	public CommMessageRunner( final ChannelHandlerContext ctx, final byte[] header, final byte[] body ) {
		this.ctx = ctx;
		this.header = header;
		this.body = body;
	}

	public void run() {

		// 模拟耗时的网络请求
		try {

			CommMessageHeader msgHeader = CommMessageHeader.parseFrom( this.header );

			// debug
			logger.debug( "A text message: [from:{}, to:{}, content:{} bytes]",
				msgHeader.getFrom(), msgHeader.getTo(), body.length );

			// 把消息路由给接收者
			String msgId = QpidMessageSender.getInstance().send( msgHeader.getFrom(), msgHeader.getTo(), msgHeader.getType(),
				body );

			// 创建表示成功的响应，并发送给客户端
			MessageAcknowledge ack = MessageAcknowledge.newBuilder()
				.setResult( MessageAcknowledge.Result.SUCCESS )
				.setMessageId( msgId )
				.setAckType( AckType.SEND )
				.build();
			this.ctx.writeAndFlush( ack );

			// info
			logger.info( "Succeed to send the message, return a success acknowledge message." );
		} catch ( Exception e ) {

			// 创建表示失败的响应，并发送给客户端
			MessageAcknowledge ack = MessageAcknowledge.newBuilder()
				.setResult( MessageAcknowledge.Result.FAILURE )
				.build();
			this.ctx.writeAndFlush( ack );

			// info
			logger.info( "Failed to send the message!!! return a failure acknowledge message." );
		}
	}
}
