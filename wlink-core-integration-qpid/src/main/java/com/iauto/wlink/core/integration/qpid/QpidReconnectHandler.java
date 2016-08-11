package com.iauto.wlink.core.integration.qpid;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.transport.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionContext;
import com.iauto.wlink.core.session.SessionContextManager;

/**
 * 处理QPID服务器重连
 * 
 * @author xiaofei.xu
 * 
 */
public class QpidReconnectHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 连接用URL */
	private final String url;

	public QpidReconnectHandler( String url ) {
		this.url = url;
	}

	@Override
	public void exceptionCaught( final ChannelHandlerContext ctx, Throwable cause )
			throws Exception {

		if ( !( cause.getCause() != null
		&& cause.getCause() instanceof ConnectionException ) ) {
			ctx.fireExceptionCaught( cause );
		}

		// log
		logger.info( "try to reconnect qpid server......" );

		// 创建新的QPID连接
		AMQConnection conn = newConnection( url );

		// 设置异常处理器
		conn.setExceptionListener( new ExceptionListener() {
			public void onException( JMSException exception ) {
				// error
				logger.error( "Error occoured. Caused by:{}",
					exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage() );
				ctx.fireExceptionCaught( exception );
			}
		} );

		// 使用新的连接恢复所有的消息监听器
		createConsumers( conn );
	}

	/*
	 * 创建新的QPID服务连接
	 */
	private AMQConnection newConnection( String url ) {

		AMQConnection conn = null;
		boolean isSuccess = false;

		while ( !isSuccess ) {
			try {

				// 与broker创建一个连接
				conn = new AMQConnection( url );

				// 创建连接
				conn.start();

				// succeed to reconnect
				isSuccess = true;

				// 添加到连接管理管理
				QpidConnectionManager.add( conn );
			} catch ( Exception ex ) {
				// error
				logger.error( "Failed to reconnect qpid server, 5 seconds later, try to reconnect again. Caused by:{}",
					ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage() );
				try {
					Thread.sleep( 5000 );
				} catch ( InterruptedException e ) {
					// ignore
				}
			}
		}

		return conn;
	}

	/*
	 * 恢复所有消息监听器
	 */
	private void createConsumers( Connection conn ) {

		// 获取当前I/O线程所有会话
		Map<String, SessionContext> ctxs = SessionContextManager.getSessions();

		for ( SessionContext ctx : ctxs.values() ) {
			Session session = ctx.getSession();

			// info
			logger.info( "Creating message consumer for user[ID:{}].", session.getUserId() );

			// 由于被重新连接，所以需要恢复原已注册的所有消息监听器
			boolean isSuccess = false;

			do {
				try {

					// 创建消息监听器
					QpidMessageRouter.createConsumer( ctx.getChannel(), conn, session.getUserId() );

					// info
					logger.info( "Succeed to create the message consumer for user[ID:{}].", session.getUserId() );

					// success
					isSuccess = true;
				} catch ( Exception ex ) {
					logger.error( "Failed to create message consumer, 5 seconds later, try again. Caused by:{}",
						ex.getMessage() );

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
