package com.iauto.wlink.core.message.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

			// 为用户注册消息监听者
			executor.execute( new MessageListenRunner( ctx, userId ) );

			return;
		}

		// 流转不能处理的事件
		super.userEventTriggered( ctx, evt );
	}

	// ======================================================================
	// setter/getter

	public SessionContext getSessionContext() {
		return sessionContext;
	}
}

class MessageListenRunner implements Runnable {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 成员变量定义 */
	private final ChannelHandlerContext ctx;
	private final String userId;

	public MessageListenRunner( ChannelHandlerContext ctx, String userId ) {
		this.ctx = ctx;
		this.userId = userId;
	}

	public void run() {
		try {

			// info
			logger.info( "Creating a message listener for user[ID:{}]", userId );

			QpidMessageListener.getInstance().listen( ctx, userId );
		} catch ( Exception e ) {
			// error

			logger.info( "Failed to create a message listener for user!!!", e );
		}
	}
}
