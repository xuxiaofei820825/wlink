package com.iauto.wlink.core.message.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.MessageConsumer;

import org.apache.commons.lang.StringUtils;
import org.apache.qpid.client.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.event.MQConnectionCreatedEvent;
import com.iauto.wlink.core.message.event.SessionContextEvent;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.message.router.QpidMessageListener;
import com.iauto.wlink.core.session.SessionContext;

public class SessionContextHandler extends ChannelInboundHandlerAdapter {

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

	/** 用户会话上下文 */
	private SessionContext sessionContext;

	/** 与MQ服务端的会话(每个IO线程创建一个连接，每个连接创建一个会话(线程)) */
	private final static ThreadLocal<AMQConnection> connections = new ThreadLocal<AMQConnection>();

	/** 当前IO线程管理的所有通道中的用户 */
	private final static ThreadLocal<Map<String, MessageConsumer>> users = new ThreadLocal<Map<String, MessageConsumer>>() {
		@Override
		protected Map<String, MessageConsumer> initialValue() {
			return new HashMap<String, MessageConsumer>();
		}
	};

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		// 处理由认证处理器触发的建立会话上下文的事件
		if ( evt instanceof SessionContextEvent ) {
			// 判断当前用户事件是否为SessionContextEvent

			// 获取会话上下文
			SessionContextEvent event = (SessionContextEvent) evt;
			String userId = event.getContext().getUserId();

			// 保存会话上下文
			this.sessionContext = event.getContext();

			// info
			logger.info( "A session context is created. userId:{}", userId );

			// 创建与MQ服务的会话
			createMQConnection( ctx, userId );

			return;
		}

		// 流转不能处理的事件
		super.userEventTriggered( ctx, evt );
	}

	private void createMQConnection( final ChannelHandlerContext ctx, final String userId ) {
		// 获取当前线程的会话
		AMQConnection conn = SessionContextHandler.getConnections().get();

		if ( conn == null ) {
			// 如果当前线程的会话还没有创建，则为当前线程创建一个

			// debug
			logger.info( "MQ-Connection of current thread has not been created, create it first!!!" );

			// 创建MQ会话
			executor.execute( new MQConnectionCreateRunner( ctx, userId ) );
		} else {

			// 直接发送会话已经创建的事件
			ctx.fireUserEventTriggered( new MQConnectionCreatedEvent( connections.get(), userId ) );
		}
	}

	@Override
	public void channelInactive( ChannelHandlerContext ctx ) throws Exception {

		if ( this.getSessionContext() != null
				&& StringUtils.isNotBlank( this.getSessionContext().getUserId() ) ) {
			String userId = this.getSessionContext().getUserId();

			// log
			logger.info(
				"Channel:{}, The channel is inactive, unbind the message consumer and remove user from current thread.",
				ctx.channel() );

			// 解绑监听器
			MessageConsumer consumer = SessionContextHandler.getUsers().get().get( userId );
			if ( consumer != null )
				consumer.close();

			// 删除当前管理的用户
			SessionContextHandler.getUsers().get().remove( userId );
		}

		// 流转到下一个处理器
		ctx.fireChannelInactive();
	}

	// ======================================================================
	// setter/getter

	public SessionContext getSessionContext() {
		return sessionContext;
	}

	public static ThreadLocal<Map<String, MessageConsumer>> getUsers() {
		return users;
	}

	public static ThreadLocal<AMQConnection> getConnections() {
		return connections;
	}

	// ==============================================================================
	// private class

	private class MQConnectionCreateRunner implements Runnable {

		private final ChannelHandlerContext ctx;
		private final String userId;

		public MQConnectionCreateRunner( ChannelHandlerContext ctx, String userId ) {
			this.ctx = ctx;
			this.userId = userId;
		}

		public void run() {
			try {
				// info
				logger.info( "Creating a connection for current thread......" );

				// 创建MQ会话
				AMQConnection conn = QpidMessageListener.getInstance().newConnection();

				// 触发MQ连接创建成功的事件
				ctx.fireUserEventTriggered( new MQConnectionCreatedEvent( conn, userId ) );
			} catch ( Exception e ) {
				// error

				logger.info( "Failed to create a session for current thread!!!", e );

				ErrorMessage error = ErrorMessage.newBuilder()
					.setError( "Session_Create_Failure" )
					.build();
				ctx.channel().writeAndFlush( error );
			}
		}
	}
}
