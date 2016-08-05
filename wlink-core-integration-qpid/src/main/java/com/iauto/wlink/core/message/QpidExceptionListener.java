package com.iauto.wlink.core.message;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.qpid.client.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionContext;
import com.iauto.wlink.core.session.SessionContextManager;

public class QpidExceptionListener implements ExceptionListener {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	private final ChannelHandlerContext ctx;
	private final String url;

	public QpidExceptionListener( final ChannelHandlerContext ctx, final String url ) {
		this.ctx = ctx;
		this.url = url;
	}

	public void onException( JMSException exception ) {
		// 显示错误日志，提醒运维尽快恢复环境
		logger.error( "Connection exception occoured! Caused by:{}", exception.getMessage() );

		// 是否连接成功
		boolean isSuccess = false;

		do {
			// info
			logger.info( "5 seconds later, attempting to reconnect MQ server......" );

			try {
				// 等待5秒
				Thread.sleep( 5000 );

				// 与broker创建一个连接
				AMQConnection conn = new AMQConnection( this.url );

				// 设置异常监听器
				conn.setExceptionListener( this );

				// 创建连接
				conn.start();

				// info
				logger.info( "Succeed to reconnect MQ server!!!" );

				// 触发事件
				// ctx.fireUserEventTriggered( new QpidReconnectedEvent( conn, ctx ) );

				// 重连成功
				isSuccess = true;

				// 执行重建连接的任务
				ctx.executor().execute( new MessageConsumerRecreateTask( conn ) );
			} catch ( Exception ex ) {
				// info
				logger.info( "Failed to reconnect to Qpid server! Caused by:{}", ex.getMessage() );
			}
		} while ( !isSuccess );
	}

	private class MessageConsumerRecreateTask implements Runnable {

		private final AMQConnection conn;

		public MessageConsumerRecreateTask( AMQConnection conn ) {
			this.conn = conn;
		}

		public void run() {
			
			QpidMessageRouter.addConnection( conn );
			
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
					} catch ( Exception e ) {
						// error
						logger.error( "Failed to create message consumer. Caused by: {}",
							e.getMessage() );

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
}
