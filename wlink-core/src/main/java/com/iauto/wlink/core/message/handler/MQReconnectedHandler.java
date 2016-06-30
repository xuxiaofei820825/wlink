package com.iauto.wlink.core.message.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.MessageConsumer;

import org.apache.qpid.client.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.event.MQMessageConsumerCreatedEvent;
import com.iauto.wlink.core.message.event.MQReconnectedEvent;
import com.iauto.wlink.core.message.router.QpidMessageListener;
import com.iauto.wlink.core.session.SessionContext;

public class MQReconnectedHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 业务线程池 */
	private static final ThreadPoolExecutor executor =
			new ThreadPoolExecutor( 5, 5, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>( 1000 ),
				new RejectedExecutionHandler() {
					public void rejectedExecution( Runnable r, ThreadPoolExecutor executor ) {
						System.err.println( String.format( "Task %d rejected.", r.hashCode() ) );
					}
				} );

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		if ( evt instanceof MQReconnectedEvent ) {
			// 处理MQ服务器重连事件

			MQReconnectedEvent event = (MQReconnectedEvent) evt;
			AMQConnection conn = event.getConnection();

			// info
			logger.info( "MQ-Connection is reconnected, reset connection of current thread." );

			// 设置当前IO线程的MQ连接
			SessionContextHandler.getConnections().set( event.getConnection() );

			// 获取当前IO线程管理的用户
			Map<String, SessionContext> sessions = SessionContext.getSessions();
			//executor.execute( new ConsumerCreateRunner( ctx, conn, sessions ) );
			executor.execute( new ConsumerCreateRunner( event.getContext(), conn, sessions ) );

			// 处理结束，返回
			return;
		}

		// 流转不能处理的事件
		super.userEventTriggered( ctx, evt );
	}

	private class ConsumerCreateRunner implements Runnable {

		private final Map<String, SessionContext> sessions;
		private final ChannelHandlerContext ctx;
		private final AMQConnection conn;

		public ConsumerCreateRunner( ChannelHandlerContext ctx, AMQConnection conn, Map<String, SessionContext> sessions ) {
			this.sessions = sessions;
			this.ctx = ctx;
			this.conn = conn;
		}

		public void run() {

			for ( String sessionId : sessions.keySet() ) {

				String userId = this.sessions.get( sessionId ).getUserId();
				Channel channel = this.sessions.get( sessionId ).getChannel();

				// info
				logger.info( "Creating message consumer for user[ID:{}].", userId );

				// 由于被重新连接，所以需要恢复原已注册的所有消息监听器
				try {

					// 重建监听者
//					MessageConsumer consumer = QpidMessageListener.getInstance()
//						.createConsumer( ctx.channel(), conn, userId );
					MessageConsumer consumer = QpidMessageListener.getInstance()
							.createConsumer( channel, conn, userId );

					// 触发消息监听器成功创建的事件
					ctx.fireUserEventTriggered( new MQMessageConsumerCreatedEvent( sessionId, consumer ) );

					// info
					logger.info( "Succeed to create the message consumer for user[ID:{}].", userId );
				} catch ( Exception e ) {
					// info
					logger.info( "Failed to create message consumer.", e );
				}
			}
		}
	}
}
