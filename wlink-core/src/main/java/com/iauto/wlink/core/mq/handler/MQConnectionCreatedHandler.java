package com.iauto.wlink.core.mq.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.jms.Connection;
import javax.jms.MessageConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.auth.Session;
import com.iauto.wlink.core.auth.SessionContext;
import com.iauto.wlink.core.auth.SessionSignatureHandler;
import com.iauto.wlink.core.auth.handler.SessionContextHandler;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage;
import com.iauto.wlink.core.mq.event.MQConnectionCreatedEvent;
import com.iauto.wlink.core.mq.router.MessageReceiver;
import com.iauto.wlink.core.tools.Executor;

public class MQConnectionCreatedHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 消息接收者 */
	private final MessageReceiver receiver;

	/** 签名处理器 */
	private final SessionSignatureHandler signHandler;

	public MQConnectionCreatedHandler( MessageReceiver receiver, SessionSignatureHandler signHandler ) {
		this.receiver = receiver;
		this.signHandler = signHandler;
	}

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		// 处理消息监听者被注册的事件
		if ( evt instanceof MQConnectionCreatedEvent ) {

			MQConnectionCreatedEvent event = (MQConnectionCreatedEvent) evt;
			SessionContext sessionContext = event.getSessionContext();

			// info
			logger.info( "MQ-Connection has been created, create a message consumer for user[ID:{}].",
				sessionContext.getSession().getUserId() );

			// 设置当前线程的MQ连接
			SessionContextHandler.addConnection( event.getConnection() );

			// 为用户注册消息监听者
			Executor.execute( new MessageConsumerCreateTask(
				event.getConnection(), event.getSessionContext(), receiver ) );

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
		private final Connection conn;
		private final SessionContext sessionCtx;

		public MessageConsumerCreateTask( Connection conn, SessionContext sessionCtx,
				MessageReceiver receiver ) {
			this.sessionCtx = sessionCtx;
			this.conn = conn;
		}

		public void run() {
			try {

				// info
				logger.info( "Creating the message consumer......" );

				// 获取会话
				Session session = sessionCtx.getSession();

				// 在指定的会话上创建消息监听器
				MessageConsumer consumer = receiver.createConsumer( sessionCtx.getChannel(),
					this.conn, String.valueOf( session.getUserId() ) );

				// 设置会话上下文的消息监听器
				sessionCtx.setConsumer( consumer );

				// info
				logger.info( "Succeed to create a message consumer for user[ID:{}]", session.getUserId() );

				// info
				logger.info( "Succeed to create a session for user. userId:{}, session:{}, channel:{}",
					session.getUserId(), session.getId(), sessionCtx.getChannel() );

				// 创建会话上下文对象，并返回给终端
				// 终端可使用会话上下文重新建立与服务器的会话
				long timestamp = System.currentTimeMillis();
				String signature = signHandler.sign( session );
				SessionMessage sessionMsg = SessionMessage.newBuilder()
					.setId( session.getId() )
					.setUserId( String.valueOf( session.getUserId() ) )
					.setTimestamp( timestamp )
					.setSignature( signature )
					.build();

				// 把签名后的会话上下文返回给终端
				sessionCtx.getChannel().writeAndFlush( sessionMsg );
			} catch ( Exception e ) {
				// info
				logger.info( "Failed to create a message listener for user!!!", e );

				// 给终端反馈该错误
				ErrorMessage error = ErrorMessage.newBuilder()
					.setError( "Session_Create_Failure" )
					.build();
				this.sessionCtx.getChannel().writeAndFlush( error );
			}
		}
	}
}
