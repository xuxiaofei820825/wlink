package com.iauto.wlink.core.message.worker;

import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.Executor;
import com.iauto.wlink.core.message.proto.CommMessageHeaderProto.CommMessageHeader;
import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge;
import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType;
import com.iauto.wlink.core.message.router.MessageSender;

public class SendCommMessageWorker implements MessageWorker {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 消息发送者 */
	private MessageSender sender;

	public SendCommMessageWorker( MessageSender sender ) {
		this.sender = sender;
	}

	public void process( final ChannelHandlerContext ctx, final byte[] header, final byte[] body ) throws Exception {
		// 执行处理
		Executor.execute( new CommMessageSendRunner( this.sender, ctx, header, body ) );
	}

	// =========================================================================
	// private functions

	private class CommMessageSendRunner implements Runnable {

		/** Handler context */
		private ChannelHandlerContext ctx;

		/** 消息发送者 */
		private MessageSender sender;

		/** 文本消息 */
		private byte[] header;
		private byte[] body;

		public CommMessageSendRunner( MessageSender sender, final ChannelHandlerContext ctx, final byte[] header,
				final byte[] body ) {
			this.sender = sender;
			this.ctx = ctx;
			this.header = header;
			this.body = body;
		}

		public void run() {

			try {

				// 解码
				CommMessageHeader msgHeader = CommMessageHeader.parseFrom( this.header );

				// debug
				logger.debug( "A message: [from:{}, to:{}, content:{} bytes]",
					msgHeader.getFrom(), msgHeader.getTo(), body.length );

				// 把消息路由给接收者
				String msgId = sender.send( msgHeader.getFrom(), msgHeader.getTo(),
					msgHeader.getType(), body );

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
					.setAckType( AckType.SEND )
					.setMessageId( "" )
					.build();
				this.ctx.writeAndFlush( ack );

				// info
				logger.info( "Failed to send the message!!! return a failure acknowledge message." );
			}
		}
	}
}
