package com.iauto.wlink.core.message.router;

import io.netty.channel.ChannelHandlerContext;

import javax.jms.BytesMessage;
import javax.jms.Destination;
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
import com.iauto.wlink.core.message.event.MQSessionCreatedEvent;
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

	public void createSession( final ChannelHandlerContext ctx, final String userId ) throws Exception {
		// 与broker创建一个连接
		AMQConnection conn = new AMQConnection( url );

		// 建立Session
		Session session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

		// 创建连接
		conn.start();

		// 触发MQ会话创建成功的事件
		ctx.fireUserEventTriggered( new MQSessionCreatedEvent( session, userId ) );
	}

	public void listen( final ChannelHandlerContext ctx, final Session session, final String userId ) throws Exception {

		// 在当前的会话上创建消费者，监听发送给用户的消息
		String url = String.format( "ADDR:message.topic/%s", userId );
		Destination dest = new AMQAnyDestination( url );

		// debug
		logger.debug( "DEST: {}", dest );

		// 设置监听器
		MessageConsumer consumer = session.createConsumer( dest );
		consumer.setMessageListener( new CommMessageLinstener( ctx, userId ) );
	}
}

class CommMessageLinstener implements MessageListener {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

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
			logger.info( "The user[ID: {}] receive a message. [from:{}]", to, from );

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
