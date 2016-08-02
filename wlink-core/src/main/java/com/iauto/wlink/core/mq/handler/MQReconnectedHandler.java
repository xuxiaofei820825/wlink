package com.iauto.wlink.core.mq.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.MessageConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.mq.event.MQReconnectedEvent;
import com.iauto.wlink.core.mq.router.MessageReceiver;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionContext;
import com.iauto.wlink.core.session.SessionContextManager;
import com.iauto.wlink.core.session.handler.SessionContextHandler;
import com.iauto.wlink.core.tools.Executor;

public class MQReconnectedHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 消息接收者 */
	private final MessageReceiver receiver;

	public MQReconnectedHandler( MessageReceiver receiver ) {
		this.receiver = receiver;
	}

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		if ( evt instanceof MQReconnectedEvent ) {
			// 处理MQ服务器重连事件

			MQReconnectedEvent event = (MQReconnectedEvent) evt;
			Connection conn = event.getConnection();

			// info
			logger.info( "MQ-Connection is reconnected, reset connection of current thread." );

			// 设置当前IO线程的MQ连接
			SessionContextHandler.addConnection( event.getConnection() );

			// 获取当前IO线程管理的用户
			Map<String, SessionContext> sessions = SessionContextManager.getSessions();
			Executor.execute( new ConsumerCreateTask( conn, sessions, receiver ) );

			// 处理结束，返回
			return;
		}

		// 流转不能处理的事件
		super.userEventTriggered( ctx, evt );
	}

	private class ConsumerCreateTask implements Runnable {

		private final Map<String, SessionContext> sessions;
		private final Connection conn;
		private final MessageReceiver receiver;

		public ConsumerCreateTask( Connection conn, Map<String, SessionContext> sessions,
				MessageReceiver receiver ) {
			this.sessions = sessions;
			this.conn = conn;
			this.receiver = receiver;
		}

		public void run() {

			for ( String sessionId : sessions.keySet() ) {
				SessionContext sessionCtx = this.sessions.get( sessionId );
				Session session = sessionCtx.getSession();

				// info
				logger.info( "Creating message consumer for user[ID:{}].", session.getUserId() );

				// 由于被重新连接，所以需要恢复原已注册的所有消息监听器
				boolean isSuccess = false;

				do {
					try {

						// 重建监听者
						MessageConsumer consumer = receiver
							.createConsumer( sessionCtx.getChannel(), conn, String.valueOf( session.getUserId() ) );

						// 设置会话的消息监听器
						sessionCtx.setConsumer( consumer );

						// info
						logger.info( "Succeed to create the message consumer for user[ID:{}].", session.getUserId() );

						// success
						isSuccess = true;
					} catch ( Exception e ) {
						// error
						logger.error( "Failed to create message consumer. Caused by: {}",
							e.getMessage() );

						// info
						logger.info( "5 seconds later, try again." );

						try {
							Thread.sleep( 5000 );
						} catch ( InterruptedException e1 ) {
							// ignore
						}
					}
				} while ( !isSuccess );
			}
		}
	}
}
