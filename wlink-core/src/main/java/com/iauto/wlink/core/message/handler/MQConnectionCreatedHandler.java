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

import com.iauto.wlink.core.message.event.MQMessageConsumerCreatedEvent;
import com.iauto.wlink.core.message.event.MQConnectionCreatedEvent;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.message.router.QpidMessageListener;

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
			logger.info( "MQ-Session has been created, register a message listener for user[ID:{}]", event.getUserId() );

			// 设置当前线程的MQ会话
			SessionContextHandler.getConnections().set( event.getConnection() );

			// 为用户注册消息监听者
			executor.execute( new MessageConsumerCreateRunner( ctx, event.getConnection(), event.getUserId() ) );

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
		private final String userId;

		public MessageConsumerCreateRunner( ChannelHandlerContext ctx, AMQConnection conn, String userId ) {
			this.ctx = ctx;
			this.userId = userId;
			this.conn = conn;
		}

		public void run() {
			try {
				// info
				logger.info( "Creating a message listener for user[ID:{}]", userId );

				// 在指定的会话上创建消息监听器
				MessageConsumer consumer = QpidMessageListener.getInstance().createConsumer( ctx, this.conn, userId );

				// fire a consumer created event
				ctx.fireUserEventTriggered( new MQMessageConsumerCreatedEvent( userId, consumer ) );
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
