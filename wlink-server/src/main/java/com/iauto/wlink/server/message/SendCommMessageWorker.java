package com.iauto.wlink.server.message;

import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.iauto.wlink.core.MessageWorker;
import com.iauto.wlink.core.message.DefaultPointToPointMessage;
import com.iauto.wlink.core.message.MessageRouter;
import com.iauto.wlink.core.message.proto.CommMessageHeaderProto.CommMessageHeader;
import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge;
import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType;

/**
 * 处理消息的发送
 * 
 * @author xiaofei.xu
 * 
 */
public class SendCommMessageWorker implements MessageWorker {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 消息发送者 */
	private final MessageRouter messageRouter;

	public SendCommMessageWorker( MessageRouter messageRouter ) {
		this.messageRouter = messageRouter;
	}

	public void process( final ChannelHandlerContext ctx, final byte[] header, final byte[] body ) throws Exception {
		// 解码
		CommMessageHeader msgHeader = CommMessageHeader.parseFrom( header );

		// debug
		logger.debug( "A message: [from:{}, to:{}, content:{} bytes]",
			msgHeader.getFrom(), msgHeader.getTo(), body.length );

		// 创建一个消息编号
		final String msgId = UUID.randomUUID()
			.toString().replace( "-", "" );

		// 给终端返回一个确认信息，表明服务端已收到该消息，准备推送给接收者
		MessageAcknowledge ack_rev = MessageAcknowledge.newBuilder()
			.setResult( MessageAcknowledge.Result.SUCCESS )
			.setMessageId( msgId )
			.setAckType( AckType.RECEIVE )
			.build();
		ctx.writeAndFlush( ack_rev );

		// 把消息路由给接收者
		ListenableFuture<?> future = messageRouter.send( new DefaultPointToPointMessage<byte[]>(
			msgHeader.getType(), body,
			Long.valueOf( msgHeader.getFrom() ),
			Long.valueOf( msgHeader.getTo() ) ) );

		Futures.addCallback( future, new FutureCallback<Object>() {
			public void onSuccess( Object result ) {
				// 成功

				// 给终端返回一个确认信息，表明服务端已经把消息推送给接收者
				MessageAcknowledge ack_send = MessageAcknowledge.newBuilder()
					.setResult( MessageAcknowledge.Result.SUCCESS )
					.setMessageId( msgId )
					.setAckType( AckType.SEND )
					.build();
				ctx.writeAndFlush( ack_send );

				// info
				logger.info( "Succeed to send the message[ID:{}], return a success acknowledge message.", msgId );
			}

			public void onFailure( Throwable t ) {
				// 失败

				// 创建表示失败的响应，并发送给客户端
				MessageAcknowledge ack = MessageAcknowledge.newBuilder()
					.setResult( MessageAcknowledge.Result.FAILURE )
					.setAckType( AckType.SEND )
					.setMessageId( msgId )
					.build();
				ctx.writeAndFlush( ack );

				// info
				logger.info( "Failed to send the message, return a failure acknowledge message." );
			}
		} );
	}
}
