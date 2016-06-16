package com.iauto.wlink.core.message.router;

import io.netty.channel.ChannelHandlerContext;

import javax.jms.BytesMessage;
import javax.jms.Connection;
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

	/** 与QPID服务端的会话(每个线程一个连接，一个会话) */
	private final static ThreadLocal<Session> sessions = new ThreadLocal<Session>();

	/** 单例 */
	private final static QpidMessageListener instance = new QpidMessageListener();

	/** 只运行内部实例化 */
	private QpidMessageListener() {
	}

	/** 返回单例 */
	public static QpidMessageListener getInstance() {
		return instance;
	}

	public void listen( final ChannelHandlerContext ctx, final String userId ) throws Exception {
		// 获取当前线程的会话
		Session session = sessions.get();

		if ( session == null ) {
			// 如果当前线程的会话还没有创建，则为当前线程创建一个

			// debug
			logger.debug( "Session of current thread has not been created, create it first" );

			// 与broker创建一个连接
			Connection conn = new AMQConnection( url );

			// 建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

			// 设置本线程的Session
			sessions.set( session );

			// 创建连接
			conn.start();
		}

		// 在当前的会话上创建消费者，监听发送给用户的消息
		Destination dest = new AMQAnyDestination( "ADDR:message.topic/" + userId
				+ ";{create:always, node:{ type:topic }}" );
		MessageConsumer consumer = session.createConsumer( dest );

		// 设置监听器
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

			if ( !StringUtils.equals( userId, to ) ) {
				// 消息接收者不一致
				logger.info( "The message receiver is not matched!!!" );
				return;
			}

			// log
			logger.info( "The user[ID: {}] receive a message. [from:{}]", to, from );

			BytesMessage bytes = (BytesMessage) message;
			byte[] body = new byte[] {};
			bytes.readBytes( body );

			CommMessage commMsg = new CommMessage();
			commMsg.setFrom( from );
			commMsg.setTo( to );
			commMsg.setType( "text" ); // TODO 必须传递消息类型
			commMsg.setBody( body );

			// 发送给接收者
			this.ctx.writeAndFlush( commMsg );

			// log
			logger.info( "Succeed to send the message to receiver!!! [ID:{}]", to );
		} catch ( Exception ex ) {

			logger.info( "Failed to send the message to receiver!!!", ex );
		}
	}
}
