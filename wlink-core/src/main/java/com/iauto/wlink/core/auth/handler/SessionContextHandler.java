package com.iauto.wlink.core.auth.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.MessageConsumer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.auth.SessionContext;
import com.iauto.wlink.core.auth.event.SessionContextEvent;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage;
import com.iauto.wlink.core.mq.event.MQConnectionCreatedEvent;
import com.iauto.wlink.core.mq.router.MessageReceiver;
import com.iauto.wlink.core.tools.Executor;

/**
 * 完成用户会话创建后的处理 <br>
 * <ul>
 * <li>如果当前IO线程没有创建MQ连接，则创建MQ连接
 * <li>为当前用户创建消息监听
 * </ul>
 * 
 * @author xiaofei.xu
 * 
 */
public class SessionContextHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 与MQ服务端的会话(每个IO线程创建一个连接，每个连接创建一个会话(线程)) */
	private final static ThreadLocal<Connection> connections = new ThreadLocal<Connection>();

	/** 消息接收者 */
	private final MessageReceiver receiver;

	/** 签名密匙 */
	private final String signKey;

	/** 当前IO线程创建的所有消息监听器 */
	private final static ThreadLocal<Map<String, MessageConsumer>> consumers = new ThreadLocal<Map<String, MessageConsumer>>() {
		@Override
		protected Map<String, MessageConsumer> initialValue() {
			return new HashMap<String, MessageConsumer>();
		}
	};

	public SessionContextHandler( MessageReceiver receiver, String signKey ) {
		this.receiver = receiver;
		this.signKey = signKey;
	}

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		// 流转不能处理的事件
		if ( !( evt instanceof SessionContextEvent ) )
			super.userEventTriggered( ctx, evt );

		// 处理由认证处理器触发的建立会话上下文的事件
		// 获取会话上下文
		SessionContextEvent event = (SessionContextEvent) evt;
		SessionContext session = event.getSession();

		// 保存会话上下文到当前线程
		SessionContext.add( session );

		// info
		logger.info( "A session context is created. userId:{}, session:{}",
			session.getUserId(),
			session.getId() );

		// 创建会话上下文对象，并返回给终端
		// 终端可使用会话上下文重新建立与服务器的会话
		long timestamp = System.currentTimeMillis();
		String signature = SessionContext.sign( signKey, session );
		SessionMessage sessionMsg = SessionMessage.newBuilder()
			.setId( session.getId() )
			.setUserId( session.getUserId() )
			.setTimestamp( timestamp )
			.setSignature( signature )
			.build();

		// 把签名后的会话上下文返回给终端
		session.getChannel().writeAndFlush( sessionMsg );

		// 创建与MQ服务的会话
		createMQConnection( ctx, session );
	}

	private void createMQConnection( final ChannelHandlerContext ctx, SessionContext session ) {
		// 获取当前线程的会话
		Connection conn = SessionContextHandler.getConnection();

		if ( conn == null ) {
			// 如果当前IO线程的连接还未创建，则为当前IO线程创建一个

			// info
			logger.info( "MQ-Connection of current IO thread has not been created, create it first!!!" );

			// 创建MQ会话
			Executor.execute( new MQConnectionCreateTask( ctx, session, receiver ) );
		} else {

			// 直接发送会话已经创建的事件
			ctx.fireUserEventTriggered( new MQConnectionCreatedEvent( conn, session ) );
		}
	}

	@Override
	public void channelInactive( ChannelHandlerContext ctx ) throws Exception {

		SessionContext session = ctx.channel()
			.attr( AuthenticationHandler.SessionKey ).get();

		if ( session != null
				&& StringUtils.isNotBlank( session.getUserId() )
				&& StringUtils.isNotBlank( session.getId() ) ) {

			String userId = session.getUserId();
			String sessionId = session.getId();

			// log
			logger.info( "User[{}] is offline, remove user and message consumer.",
				userId );

			// 解绑消息监听器
			MessageConsumer consumer = SessionContextHandler.getConsumers()
				.get( sessionId );
			if ( consumer != null )
				consumer.close();

			// 删除当前管理的用户
			SessionContextHandler.getConsumers().remove( sessionId );
			SessionContext.getSessions().remove( sessionId );
		}

		// 流转到下一个处理器
		ctx.fireChannelInactive();
	}

	// ================================================================================================================
	// setter/getter

	public static Map<String, MessageConsumer> getConsumers() {
		return consumers.get();
	}

	public static Connection getConnection() {
		return connections.get();
	}

	public static void addConnection( Connection conn ) {
		connections.set( conn );
	}

	// ================================================================================================================
	// private class

	private class MQConnectionCreateTask implements Runnable {

		private final ChannelHandlerContext ctx;
		private final SessionContext session;
		private final MessageReceiver receiver;

		public MQConnectionCreateTask( ChannelHandlerContext ctx, SessionContext session, MessageReceiver receiver ) {
			this.ctx = ctx;
			this.session = session;
			this.receiver = receiver;
		}

		public void run() {
			try {
				// info
				logger.info( "Creating a MQ-Connection for current IO thread......" );

				// 创建MQ服务连接
				Connection conn = receiver.newConnection( ctx );

				// 触发MQ连接创建成功的事件
				ctx.fireUserEventTriggered( new MQConnectionCreatedEvent( conn, session ) );
			} catch ( Exception e ) {
				// error
				logger.info( "Failed to create a session for current thread! Caused by: {}", e.getMessage() );

				// 返回一个错误响应
				ErrorMessage error = ErrorMessage.newBuilder()
					.setError( "Session_Create_Failure" )
					.build();
				session.getChannel().writeAndFlush( error );
			}
		}
	}
}
