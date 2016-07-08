package com.iauto.wlink.core.message.router;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.PooledConnectionFactory;
import org.apache.qpid.url.URLSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.router.MessageSender;

public class QpidMessageSender implements MessageSender {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 连接池 */
	private final PooledConnectionFactory pooledConnectionFactory;

	public QpidMessageSender( final String url ) {

		// 创建连接池
		pooledConnectionFactory = new PooledConnectionFactory();
		pooledConnectionFactory.setConnectionTimeout( 10000 );
		pooledConnectionFactory.setMaxPoolSize( 10 );

		try {
			pooledConnectionFactory.setConnectionURLString( url );
		} catch ( URLSyntaxException e ) {
			// URL无法解析(配置项错误)
			throw new RuntimeException( e );
		}
	}

	public String send( String sender, String receiver, String type, byte[] message ) {

		Connection conn = null;
		Session session = null;

		try {

			// info
			logger.info( "Send a message. [from:{}, to:{}]", sender, receiver );

			// 从连接池中获取连接
			conn = pooledConnectionFactory.createConnection();

			// 与broker建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

			// 设置连接的节点名，如果不存在该topic节点，则新建一个
			Destination dest = new AMQAnyDestination( "ADDR:message.topic/" + receiver
					+ ";{create:always, node:{ type:topic }}" );

			// 为指定的节点创建消息发送者
			MessageProducer producer = session.createProducer( dest );

			// 创建字节消息
			BytesMessage msg = session.createBytesMessage();
			msg.writeBytes( message );
			msg.setStringProperty( "from", sender );
			msg.setStringProperty( "to", receiver );
			msg.setStringProperty( "type", type );

			// 发送消息
			producer.send( msg );

			// 获取消息编号
			String msgId = msg.getJMSMessageID().substring( 3 );

			// info
			logger.info( "Succeed to send the message. ID:{}", msgId );

			return msgId;
		} catch ( Exception ex ) {
			// error
			logger.error( "Failed to send the message!!! Caused by: {}", ex.getMessage() );

		} finally {
			// 关闭会话
			if ( session != null ) {
				try {
					session.close();
				} catch ( JMSException e ) {
					// ignore
				}
			}
			if ( conn != null ) {
				try {
					conn.close();
				} catch ( JMSException e ) {
					// ignore
				}
			}
		}

		return null;
	}

	public void sendReceiveAck( final String sender, final String receiver, final String msgId ) {
		Connection conn = null;
		Session session = null;

		try {

			// 从连接池中获取连接
			conn = pooledConnectionFactory.createConnection();

			// 与broker建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

			// 设置连接的节点名，如果不存在该topic节点，则新建一个
			Destination dest = new AMQAnyDestination( "ADDR:message.topic/" + receiver
					+ ";{create:always, node:{ type:topic }}" );

			// 为指定的节点创建消息发送者
			MessageProducer producer = session.createProducer( dest );

			// 创建字节消息
			BytesMessage msg = session.createBytesMessage();
			msg.setStringProperty( "from", sender );
			msg.setStringProperty( "to", receiver );
			msg.setStringProperty( "msgId", msgId );
			msg.setStringProperty( "type", "msg_rev_ack" );

			// 发送消息
			producer.send( msg );

			// info
			logger.info( "Succeed to send the message receive acknowledge. [from:{}, to:{}]", sender, receiver );

		} catch ( Exception ex ) {
			// error
			logger.error( "Failed to send the message!!!", ex );

		} finally {
			// 关闭会话
			if ( session != null ) {
				try {
					session.close();
				} catch ( JMSException e ) {
					// ignore
				}
			}
			if ( conn != null ) {
				try {
					conn.close();
				} catch ( JMSException e ) {
					// ignore
				}
			}
		}
	}
}
