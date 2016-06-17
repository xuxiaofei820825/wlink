package com.iauto.wlink.core.message.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.Session;

import org.apache.qpid.client.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.event.MQSessionCreatedEvent;
import com.iauto.wlink.core.message.event.SessionContextEvent;
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

	/** 与MQ服务端的会话(每个线程一个连接，一个会话) */
	private final static ThreadLocal<AMQConnection> connections = new ThreadLocal<AMQConnection>();
	private final static ThreadLocal<Session> sessions = new ThreadLocal<Session>();

	public static ThreadLocal<Session> getSessions() {
		return sessions;
	}

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
			createMqSession( ctx, userId );

			return;
		}

		// 流转不能处理的事件
		super.userEventTriggered( ctx, evt );
	}

	private void createMqSession( final ChannelHandlerContext ctx, String userId ) {
		// 获取当前线程的会话
		Session session = sessions.get();

		if ( session == null ) {
			// 如果当前线程的会话还没有创建，则为当前线程创建一个

			// debug
			logger.info( "MQ-Session of current thread has not been created, create it first" );

			// 创建MQ会话
			executor.execute( new MqSessionCreateRunner( ctx, userId ) );
		} else {
			ctx.fireUserEventTriggered( new MQSessionCreatedEvent( session, userId ) );
		}
	}

	// ======================================================================
	// setter/getter

	public SessionContext getSessionContext() {
		return sessionContext;
	}
}

class MqSessionCreateRunner implements Runnable {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	private final ChannelHandlerContext ctx;
	private final String userId;

	public MqSessionCreateRunner( ChannelHandlerContext ctx, String userId ) {
		this.ctx = ctx;
		this.userId = userId;
	}

	public void run() {
		try {

			// info
			logger.info( "Creating a session for current thread......" );

			// 创建MQ会话
			QpidMessageListener.getInstance().createSession( ctx, userId );
		} catch ( Exception e ) {
			// error

			logger.info( "Failed to create a session for current thread!!!", e );
		}
	}
}
