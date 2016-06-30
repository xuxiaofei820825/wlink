package com.iauto.wlink.core.message.router;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import javax.jms.BytesMessage;
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
import com.iauto.wlink.core.message.event.MQReconnectedEvent;
import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge;
import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType;
import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result;

/**
 * 该类用来创建与QPID的连接，并为登录用户注册消息监听器
 * 
 * @author xiaofei.xu
 * 
 */
public class QpidMessageListener {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 连接用URL */
	private final static String url = "amqp://guest:guest@test/test?brokerlist='tcp://172.26.188.173:5672'";

	/** 单例 */
	private final static QpidMessageListener instance = new QpidMessageListener();

	/** 只运行内部实例化 */
	private QpidMessageListener() {
	}

	/** 返回单例 */
	public static QpidMessageListener getInstance() {
		return instance;
	}

	/**
	 * 创建于MQ服务器的连接
	 * 
	 * @throws Exception
	 */
	public AMQConnection newConnection( final ChannelHandlerContext ctx ) throws Exception {
		// 与broker创建一个连接
		AMQConnection conn = new AMQConnection( url );

		// 添加异常监听器
		conn.setExceptionListener( new CommMessageExceptionListener( ctx ) );

		// 创建连接
		conn.start();

		return conn;
	}

	/**
	 * 创建消息监听器
	 * 
	 * @param channel
	 * @param conn
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public MessageConsumer createConsumer( final Channel channel, final AMQConnection conn, final String userId )
			throws Exception {

		// 在当前的会话上创建消费者，监听发送给用户的消息
		// String url = String.format( "ADDR:message.topic/%s;{create:always,node:{type:topic}}", userId );
		String url = String.format( "ADDR:message.topic/%s", userId );
		Destination dest = new AMQAnyDestination( url );

		// debug
		logger.debug( "DEST: {}", dest );

		Session session = null;
		if ( conn.getSessions().size() == 0 ) {
			session = conn.createSession( false, Session.CLIENT_ACKNOWLEDGE );
		} else {
			session = conn.getSession( 0 );
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

		public CommMessageExceptionListener( final ChannelHandlerContext ctx ) {
			this.ctx = ctx;
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
					AMQConnection conn = new AMQConnection( url );

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

				if ( StringUtils.equals( type, "msg_rev_ack" ) ) {

					String msgId = message.getStringProperty( "msgId" );

					MessageAcknowledge ack = MessageAcknowledge.newBuilder()
						.setAckType( AckType.RECEIVE )
						.setResult( Result.SUCCESS )
						.setMessageId( msgId )
						.build();

					// 发送给接收者
					this.channel.writeAndFlush( ack );

					// 给MQ服务器发送确认消息
					message.acknowledge();
				} else {

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

					// 发送一个消息已发送给接收者的确认信息
					String msgId = bytes.getJMSMessageID().substring( 3 );
					QpidMessageSender.getInstance().sendReceiveAck( to, from, msgId );

					// 给MQ服务器发送确认消息
					message.acknowledge();
				}
			} catch ( Exception ex ) {
				// error

				logger.info( "Failed to send the message to receiver! Caused by: {}", ex.getMessage() );
			}
		}
	}
}
