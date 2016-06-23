package com.iauto.wlink.core.message.router;

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
import org.apache.qpid.client.BrokerDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.CommMessage;
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
	public AMQConnection newConnection() throws Exception {
		// 与broker创建一个连接
		AMQConnection conn = new AMQConnection( url );

		// 添加异常监听器
		conn.setExceptionListener( new CommMessageExceptionListener( conn ) );

		// 创建连接
		conn.start();

		return conn;
	}

	public MessageConsumer createConsumer( final ChannelHandlerContext ctx, final AMQConnection conn, final String userId )
			throws Exception {

		// 在当前的会话上创建消费者，监听发送给用户的消息
		String url = String.format( "ADDR:message.topic/%s", userId );
		Destination dest = new AMQAnyDestination( url );

		// debug
		logger.debug( "DEST: {}", dest );

		Session session = null;
		if ( conn.getSessions().size() == 0 ) {
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );
		} else {
			session = conn.getSession( 0 );
		}

		// 设置监听器
		MessageConsumer consumer = session.createConsumer( dest );
		consumer.setMessageListener( new CommMessageLinstener( ctx, userId ) );

		return consumer;
	}

	// =============================================================================
	// 异常监听器
	private class CommMessageExceptionListener implements ExceptionListener {

		/** 与MQ服务的连接 */
		private final AMQConnection conn;

		public CommMessageExceptionListener( final AMQConnection conn ) {
			this.conn = conn;
		}

		public void onException( JMSException exception ) {
			// error
			logger.error( "==========Exception occoured!!!==========", exception );

			boolean isConnected = false;

			do {
				// info
				logger.info( "Attempt to reconnect MQ server!!!" );

				// 尝试重新连接
				BrokerDetails details = conn.getConnectionURL().getBrokerDetails( 0 );
				isConnected = conn.attemptReconnection( details.getHost(), details.getPort(), true );

				// 连接成功
				if ( isConnected )
					logger.info( "Succeed to reconnect MQ server." );

				try {
					Thread.sleep( 5000 );
				} catch ( InterruptedException e ) {
					// ignore
				}
			} while ( !isConnected );
		}
	}

	// =============================================================================
	// 消息监听器

	private class CommMessageLinstener implements MessageListener {

		/** 通道处理器上下文 */
		private final ChannelHandlerContext ctx;

		/** 监听消息的用户 */
		private final String userId;

		public CommMessageLinstener( ChannelHandlerContext ctx, String userId ) {
			this.ctx = ctx;
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
				logger.info( "The user[ID: {}] receive a message. [from:{}, type:{}]", to, from, type );

				if ( StringUtils.equals( type, "msg_rev_ack" ) ) {

					String msgId = message.getStringProperty( "msgId" );

					MessageAcknowledge ack = MessageAcknowledge.newBuilder()
						.setAckType( AckType.RECEIVE )
						.setResult( Result.SUCCESS )
						.setMessageId( msgId )
						.build();

					// 发送给接收者
					this.ctx.writeAndFlush( ack );
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
					this.ctx.writeAndFlush( commMsg );

					// log
					logger.info( "Succeed to send the message to receiver!!! [ID:{}]", to );

					// 发送一个消息已发送给接收者的确认信息
					String msgId = bytes.getJMSMessageID().substring( 3 );
					QpidMessageSender.getInstance().sendReceiveAck( to, from, msgId );
				}
			} catch ( Exception ex ) {
				// error

				logger.info( "Failed to send the message to receiver!!!", ex );
			}
		}
	}
}
