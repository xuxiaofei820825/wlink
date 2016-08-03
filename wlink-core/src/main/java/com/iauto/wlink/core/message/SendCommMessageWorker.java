package com.iauto.wlink.core.message;

import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

import javax.jms.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.MessageWorker;
import com.iauto.wlink.core.message.proto.CommMessageHeaderProto.CommMessageHeader;
import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge;
import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType;
import com.iauto.wlink.core.mq.router.MessageSender;
import com.iauto.wlink.core.session.handler.SessionContextHandler;
import com.iauto.wlink.core.tools.Executor;

public class SendCommMessageWorker implements MessageWorker {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 消息发送者 */
	private final MessageSender sender;

	public SendCommMessageWorker( MessageSender sender ) {
		this.sender = sender;
	}

	public void process( final ChannelHandlerContext ctx, final byte[] header, final byte[] body ) throws Exception {

		// 获取当前IO线程的连接
		Connection conn = SessionContextHandler.getConnection();

		// 执行处理
		Executor.execute( new CommMessageSendTask( conn, ctx, header, body ) );
	}

	// =========================================================================
	// private functions

	private class CommMessageSendTask implements Runnable {

		/** Handler context */
		private final ChannelHandlerContext ctx;

		/** 消息发送者 */
		private final Connection conn;

		/** 消息头&消息体 */
		private final byte[] header;
		private final byte[] body;

		public CommMessageSendTask( final Connection conn, final ChannelHandlerContext ctx,
				final byte[] header, final byte[] body ) {
			this.conn = conn;
			this.ctx = ctx;
			this.header = header;
			this.body = body;
		}

		public void run() {

			// 创建一个消息编号
			final String msgId = UUID.randomUUID()
				.toString()
				.replace( "-", "" );

			try {

				// 解码
				CommMessageHeader msgHeader = CommMessageHeader.parseFrom( this.header );

				// debug
				logger.debug( "A message: [from:{}, to:{}, content:{} bytes]",
					msgHeader.getFrom(), msgHeader.getTo(), body.length );

				// 给终端返回一个确认信息，表明服务端已收到该消息，准备推送给接收者
				MessageAcknowledge ack_rev = MessageAcknowledge.newBuilder()
					.setResult( MessageAcknowledge.Result.SUCCESS )
					.setMessageId( msgId )
					.setAckType( AckType.RECEIVE )
					.build();
				this.ctx.writeAndFlush( ack_rev );

				// 把消息路由给接收者
				sender.send( conn, msgHeader.getFrom(), msgHeader.getTo(),
					msgHeader.getType(), body );

				// 给终端返回一个确认信息，表明服务端已经把消息推送给接收者
				MessageAcknowledge ack_send = MessageAcknowledge.newBuilder()
					.setResult( MessageAcknowledge.Result.SUCCESS )
					.setMessageId( msgId )
					.setAckType( AckType.SEND )
					.build();
				this.ctx.writeAndFlush( ack_send );

				// info
				logger.info( "Succeed to send the message[ID:{}], return a success acknowledge message.", msgId );
			} catch ( Exception e ) {

				// 创建表示失败的响应，并发送给客户端
				MessageAcknowledge ack = MessageAcknowledge.newBuilder()
					.setResult( MessageAcknowledge.Result.FAILURE )
					.setAckType( AckType.SEND )
					.setMessageId( msgId )
					.build();
				this.ctx.writeAndFlush( ack );

				// info
				logger.info( "Failed to send the message, return a failure acknowledge message." );
			}
		}
	}
}
