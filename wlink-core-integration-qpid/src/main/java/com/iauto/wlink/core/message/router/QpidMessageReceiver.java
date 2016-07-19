package com.iauto.wlink.core.message.router;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.CommMessage;
import com.iauto.wlink.core.mq.event.MQReconnectedEvent;
import com.iauto.wlink.core.mq.router.MessageReceiver;

/**
 * 该类用来创建与QPID的连接，并为登录用户注册消息监听器
 * 
 * @author xiaofei.xu
 * 
 */
public class QpidMessageReceiver implements MessageReceiver {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 连接用URL */
	private final String url;

	/** 只运行内部实例化 */
	public QpidMessageReceiver( String url ) {
		this.url = url;
	}

	/**
	 * 创建于MQ服务器的连接
	 * 
	 * @param ctx
	 *          通道处理器上下文
	 * 
	 * @throws Exception
	 */
	public Connection newConnection( final ChannelHandlerContext ctx ) throws Exception {

		// 与broker创建一个连接
		AMQConnection conn = new AMQConnection( url );

		// 添加异常监听器
		conn.setExceptionListener( new CommMessageExceptionListener( ctx, url ) );

		// 创建连接
		conn.start();

		return conn;
	}

	/**
	 * 创建消息监听器
	 * 
	 * @param channel
	 *          用户通道
	 * @param conn
	 *          与MQ服务器的连接
	 * @param userId
	 *          用户编号
	 * @return 创建的消息监听器
	 * @throws Exception
	 */
	public MessageConsumer createConsumer( final Channel channel, final Connection conn, final String userId )
			throws Exception {

		// 在当前的会话上创建消费者，监听发送给用户的消息
		String dest_url = String.format( "ADDR:message.topic/%s", userId );
		Destination dest = new AMQAnyDestination( dest_url );

		// debug
		logger.debug( "DEST: {}", dest );

		Session session = null;
		AMQConnection amqConn = (AMQConnection) conn;
		if ( amqConn.getSessions().size() == 0 ) {
			session = conn.createSession( false, Session.CLIENT_ACKNOWLEDGE );
		} else {
			session = amqConn.getSession( 0 );
		}

		// 设置监听器
		MessageConsumer consumer = session.createConsumer( dest );
		consumer.setMessageListener( new CommMessageLinstener( channel, userId ) );

		return consumer;
	}

	// =============================================================================
	// private class

	// 异常监听器
	private class CommMessageExceptionListener implements ExceptionListener {

		private final ChannelHandlerContext ctx;
		private final String url;

		public CommMessageExceptionListener( final ChannelHandlerContext ctx, final String url ) {
			this.ctx = ctx;
			this.url = url;
		}

		public void onException( JMSException exception ) {
			// error
			logger.error( "Connection exception occoured! Caused by: {}", exception.getMessage() );

			// 是否连接成功
			boolean isSuccess = false;

			do {
				// info
				logger.info( "5 seconds later, attempting to reconnect MQ server......" );

				// 等待5秒
				try {
					Thread.sleep( 5000 );
				} catch ( InterruptedException e ) {
					// ignore
				}

				try {

					// 与broker创建一个连接
					AMQConnection conn = new AMQConnection( this.url );

					// 添加异常监听器
					conn.setExceptionListener( this );

					// 创建连接
					conn.start();

					// info
					logger.info( "Succeed to reconnect MQ server!!!" );

					// 触发事件
					ctx.fireUserEventTriggered( new MQReconnectedEvent( conn, ctx ) );

					// 重连成功
					isSuccess = true;
				} catch ( Exception ex ) {
					// info
					logger.info( "Failed to reconnect to MQ server! Caused by: {}", ex.getMessage() );
				}
			} while ( !isSuccess );
		}
	}

	// =============================================================================
	// 消息监听器

	private class CommMessageLinstener implements MessageListener {

		/** 通道处理器上下文 */
		private final Channel channel;

		/** 监听消息的用户 */
		private final String userId;

		public CommMessageLinstener( Channel channel, String userId ) {
			this.channel = channel;
			this.userId = userId;
		}

		public void onMessage( Message message ) {

			try {

				// 获取属性
				String from = message.getStringProperty( "from" );
				String to = message.getStringProperty( "to" );
				String type = message.getStringProperty( "type" );

				if ( !StringUtils.equals( userId, to ) ) {
					// 消息接收者不一致
					logger.info( "The message receiver is not matched!!!" );
					return;
				}

				// log
				logger.info( "The user[ID:{}] receive a message. [from:{}, type:{}]", to, from, type );

				BytesMessage bytes = (BytesMessage) message;
				long len = bytes.getBodyLength();
				byte[] body = new byte[(int) len];
				bytes.readBytes( body );

				CommMessage commMsg = new CommMessage();
				commMsg.setFrom( from );
				commMsg.setTo( to );
				commMsg.setType( type );
				commMsg.setBody( body );

				// 发送给接收者
				this.channel.writeAndFlush( commMsg );

				// log
				logger.info( "Succeed to send the message to receiver!!! [ID:{}]", to );

				// 给MQ服务器发送确认消息
				message.acknowledge();
			} catch ( Exception ex ) {
				// error

				logger.info( "Failed to send the message to receiver! Caused by: {}", ex.getMessage() );
			}
		}
	}
}
