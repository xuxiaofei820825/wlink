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

public class QpidMessageRouter implements MessageRouter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	private static final String url =
			"amqp://guest:guest@test/test?brokerlist='tcp://172.26.188.173:5672'";

	/** 连接池(单例) */
	private static final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();

	static {

		try {
			pooledConnectionFactory.setConnectionTimeout( 10000 );
			pooledConnectionFactory.setMaxPoolSize( 10 );
			pooledConnectionFactory.setConnectionURLString( url );
		} catch ( URLSyntaxException e ) {
			//
		}
	}

	public QpidMessageRouter() {
	}

	public void send( String sender, String receiver, byte[] message ) {

		Connection conn = null;
		Session session = null;

		try {

			// 从连接池中获取连接
			conn = pooledConnectionFactory.createConnection();

			// 与broker建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

			// 设置连接的节点名，如果不存在该topic节点，则新建一个
			Destination dest = new AMQAnyDestination( "ADDR:message.topic/" + receiver
					+ "; {create: always, node:{ type: topic }}" );

			// 为指定的节点创建消息发送者
			MessageProducer producer = session.createProducer( dest );

			// 创建字节消息
			BytesMessage msg = session.createBytesMessage();
			msg.writeBytes( message );
			msg.setStringProperty( "from", sender );
			msg.setStringProperty( "to", receiver );

			// 发送消息
			producer.send( msg );

			// info
			logger.info( "Succeed to send the message. [from: {}, to: {}]", sender, receiver );
		} catch ( Exception ex ) {
			// error
			logger.info( "Failed to send a message" );

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

	public void receive( String receiver ) {

	}
}
