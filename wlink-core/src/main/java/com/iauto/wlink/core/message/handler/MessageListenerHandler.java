package com.iauto.wlink.core.message.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.event.MQSessionCreatedEvent;
import com.iauto.wlink.core.message.router.QpidMessageListener;

public class MessageListenerHandler extends ChannelInboundHandlerAdapter {

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
		if ( evt instanceof MQSessionCreatedEvent ) {

			MQSessionCreatedEvent event = (MQSessionCreatedEvent) evt;

			// info
			logger.info( "MQ-Session has been created, register a message listener for user[ID:{}]", event.getUserId() );

			// 设置当前线程的MQ会话
			SessionContextHandler.getSessions().set( event.getSession() );

			// 为用户注册消息监听者
			executor.execute( new MessageListenRunner( ctx, event.getSession(), event.getUserId() ) );

			return;
		}

		// 流转不能处理的事件
		super.userEventTriggered( ctx, evt );
	}
}

class MessageListenRunner implements Runnable {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 成员变量定义 */
	private final ChannelHandlerContext ctx;
	private final Session session;
	private final String userId;

	public MessageListenRunner( ChannelHandlerContext ctx, Session session, String userId ) {
		this.ctx = ctx;
		this.userId = userId;
		this.session = session;
	}

	public void run() {
		try {

			// info
			logger.info( "Creating a message listener for user[ID:{}]", userId );

			QpidMessageListener.getInstance().listen( ctx, session, userId );
		} catch ( Exception e ) {
			// error

			logger.info( "Failed to create a message listener for user!!!", e );
		}
	}
}
