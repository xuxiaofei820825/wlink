package com.iauto.wlink.core.mq.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.jms.Connection;
import javax.jms.MessageConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.auth.SessionContext;
import com.iauto.wlink.core.auth.handler.SessionContextHandler;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.mq.event.MQConnectionCreatedEvent;
import com.iauto.wlink.core.mq.event.MQMessageConsumerCreatedEvent;
import com.iauto.wlink.core.mq.router.MessageReceiver;
import com.iauto.wlink.core.tools.Executor;

public class MQConnectionCreatedHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	private final MessageReceiver receiver;

	public MQConnectionCreatedHandler( MessageReceiver receiver ) {
		this.receiver = receiver;
	}

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		// 处理消息监听者被注册的事件
		if ( evt instanceof MQConnectionCreatedEvent ) {

			MQConnectionCreatedEvent event = (MQConnectionCreatedEvent) evt;

			// info
			logger.info( "MQ-Connection has been created, create a message consumer for user[ID:{}].",
				event.getSession().getUserId() );

			// 设置当前线程的MQ连接
			SessionContextHandler.addConnection( event.getConnection() );

			// 为用户注册消息监听者
			Executor.execute( new MessageConsumerCreateTask( ctx, event.getConnection(), event.getSession(), receiver ) );

			// 结束处理，返回
			return;
		}

		// 流转不能处理的事件
		super.userEventTriggered( ctx, evt );
	}

	// =======================================================================
	// private class

	private class MessageConsumerCreateTask implements Runnable {

		/** 成员变量定义 */
		private final ChannelHandlerContext ctx;
		private final Connection conn;
		private final SessionContext session;
		private final MessageReceiver receiver;

		public MessageConsumerCreateTask( ChannelHandlerContext ctx, Connection conn, SessionContext session,
				MessageReceiver receiver ) {
			this.ctx = ctx;
			this.session = session;
			this.conn = conn;
			this.receiver = receiver;
		}

		public void run() {
			try {
				// info
				logger.info( "Creating a message consumer for user[ID:{}]", session.getUserId() );

				// 在指定的会话上创建消息监听器
				MessageConsumer consumer = receiver.createConsumer( session.getChannel(),
					this.conn, session.getUserId() );

				// 触发事件
				ctx.fireUserEventTriggered( new MQMessageConsumerCreatedEvent( session, consumer ) );
			} catch ( Exception e ) {
				// error

				logger.info( "Failed to create a message listener for user!!!", e );

				// 给终端反馈该错误
				ErrorMessage error = ErrorMessage.newBuilder()
					.setError( "Session_Create_Failure" )
					.build();
				ctx.channel().writeAndFlush( error );
			}
		}
	}
}
