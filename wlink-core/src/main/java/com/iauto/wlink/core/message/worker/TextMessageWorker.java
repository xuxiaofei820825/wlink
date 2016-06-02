package com.iauto.wlink.core.message.worker;

import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge;
import com.iauto.wlink.core.message.proto.TextMessageProto.TextMessage;

public class TextMessageWorker implements Runnable {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	private ChannelHandlerContext ctx;

	/** 文本消息 */
	private TextMessage txtMsg;

	public TextMessageWorker( final ChannelHandlerContext ctx, final TextMessage txtMsg ) {
		this.ctx = ctx;
		this.txtMsg = txtMsg;
	}

	public void run() {
		// log
		logger.info( "Processing the text message......" );

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

		ctx.writeAndFlush( ack );
	}
}
