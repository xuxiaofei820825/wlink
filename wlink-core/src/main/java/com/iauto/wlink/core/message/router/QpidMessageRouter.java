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

	/** 与broker的连接 */
	private Connection conn;

	/** 与broker的会话 */
	private static Session session;

	private MessageProducer producer;

	private static final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();

	static {

		try {
			pooledConnectionFactory.setConnectionTimeout( 10000 );
			pooledConnectionFactory.setMaxPoolSize( 10 );
			pooledConnectionFactory.setConnectionURLString( url );
		}
		catch ( URLSyntaxException e ) {
			//
		}
	}

	public void connect() {
		// log
		logger.info( "Connect to MQ Server. url:{}", url );

		try {

			// 从连接池中获取连接
			conn = pooledConnectionFactory.createConnection();

			// 与broker建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

			session.close();
			conn.close();

// // 设置连接的节点名，如果不存在该topic节点，则新建一个
// Destination dest = new AMQAnyDestination( "ADDR:" + "message.topic" + "; {create: always, node:{ type: topic }}" );
//
// // 为指定的节点创建消息发送者
// producer = session.createProducer( dest );
		}
		catch ( JMSException e ) {
			throw new RuntimeException( e );
		}
// catch ( URISyntaxException e ) {
// e.printStackTrace();
// }
	}

	public void send( String sender, String receiver, byte[] message ) throws Exception {
		if ( session == null ) {
			// 如果还没有做初始化连接，先创建连接
			connect();
		}

		// 设置连接的节点名，如果不存在该topic节点，则新建一个
		Destination dest = new AMQAnyDestination( "ADDR:" + "message.topic" + "; {create: always, node:{ type: topic }}" );

		// 为指定的节点创建消息发送者
		MessageProducer producer = session.createProducer( dest );

		// 创建字节消息
		BytesMessage msg = session.createBytesMessage();
		msg.writeBytes( message );
		msg.setStringProperty( "from", sender );
		msg.setStringProperty( "to", receiver );

		// 发送消息
		producer.send( msg );
		producer.close();

		// log
		logger.info( "Succeed to send a message" );
	}

	public void receive( String receiver ) {

	}
}
