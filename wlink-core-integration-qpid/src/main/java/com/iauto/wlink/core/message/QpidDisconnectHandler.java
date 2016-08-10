package com.iauto.wlink.core.message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

import javax.jms.Connection;

import org.apache.qpid.client.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.event.QpidDisconnectEvent;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionContext;
import com.iauto.wlink.core.session.SessionContextManager;

/**
 * 处理QPID服务器重连
 * 
 * @author xiaofei.xu
 * 
 */
public class QpidDisconnectHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt ) throws Exception {

		if ( !( evt instanceof QpidDisconnectEvent ) ) {
			ctx.fireUserEventTriggered( evt );
			return;
		}

		QpidDisconnectEvent event = (QpidDisconnectEvent) evt;

		// 创建新的QPID连接
		Connection conn = newConnection( ctx, event.getUrl() );

		// 使用新的连接恢复所有的消息监听器
		createConsumers( conn );
	}

	/*
	 * 创建新的QPID服务连接
	 */
	private Connection newConnection( ChannelHandlerContext ctx, String url ) throws Exception {
		// info
		logger.info( "Connection of current I/O thread is not exist, create a connection first!" );

		// 与broker创建一个连接
		AMQConnection conn = new AMQConnection( url );

		// 添加异常监听器
		conn.setExceptionListener( new QpidExceptionListener( ctx, url ) );

		// 创建连接
		conn.start();

		// 添加到连接管理管理
		QpidConnectionManager.add( conn );

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
					// error
					logger.error( "Failed to create message consumer. Caused by: {}", ex.getMessage() );

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
