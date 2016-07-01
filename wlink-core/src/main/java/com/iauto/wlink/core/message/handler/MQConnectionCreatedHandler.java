package com.iauto.wlink.core.message.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.MessageConsumer;

import org.apache.qpid.client.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.event.MQConnectionCreatedEvent;
import com.iauto.wlink.core.message.event.MQMessageConsumerCreatedEvent;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.message.router.QpidMessageListener;
import com.iauto.wlink.core.session.SessionContext;

public class MQConnectionCreatedHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 消息监听业务线程池 */
	private static final ThreadPoolExecutor executor =
			new ThreadPoolExecutor( 10, 10, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>( 1000 ),
				new RejectedExecutionHandler() {
					public void rejectedExecution( Runnable r, ThreadPoolExecutor executor ) {
						System.err.println( String.format( "Task %d rejected.", r.hashCode() ) );
					}
				} );

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		// 处理消息监听者被注册的事件
		if ( evt instanceof MQConnectionCreatedEvent ) {

			MQConnectionCreatedEvent event = (MQConnectionCreatedEvent) evt;

			// info
			logger.info( "MQ-Connection has been created, create a message consumer for user[{}].",
				event.getSession().getUserId() );

			// 设置当前线程的MQ连接
			SessionContextHandler.addConnection( event.getConnection() );

			// 为用户注册消息监听者
			executor.execute( new MessageConsumerCreateRunner( ctx, event.getConnection(), event.getSession() ) );

			// 结束处理，返回
			return;
		}

		// 流转不能处理的事件
		super.userEventTriggered( ctx, evt );
	}

	// =======================================================================
	// private class

	private class MessageConsumerCreateRunner implements Runnable {

		/** logger */
		private final Logger logger = LoggerFactory.getLogger( getClass() );

		/** 成员变量定义 */
		private final ChannelHandlerContext ctx;
		private final AMQConnection conn;
		private final SessionContext session;

		public MessageConsumerCreateRunner( ChannelHandlerContext ctx, AMQConnection conn, SessionContext session ) {
			this.ctx = ctx;
			this.session = session;
			this.conn = conn;
		}

		public void run() {
			try {
				// info
				logger.info( "Creating a message listener for user[ID:{}]", session.getUserId() );

				// 在指定的会话上创建消息监听器
				MessageConsumer consumer = QpidMessageListener.getInstance().createConsumer( session.getChannel(), this.conn,
					session.getUserId() );

				// 触发事件
				ctx.fireUserEventTriggered( new MQMessageConsumerCreatedEvent( session.getId(), consumer ) );
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
