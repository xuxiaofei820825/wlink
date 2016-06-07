package com.iauto.wlink.core.message.router;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.qpid.AMQConnectionFailureException;
import org.apache.qpid.AMQException;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
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
	private Session session;

	public void connect() {
		// log
		logger.info( "Connect to MQ Server. url:{}", url );

		try {
			// 与broker建立连接
			conn = new AMQConnection( url );
			// 与broker建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );
		}
		catch ( URLSyntaxException e ) {
			// log
			logger.info( "Url of qpid Server is error, please check the configuration." );

			throw new RuntimeException( e );
		}
		catch ( AMQException e ) {
			if ( e instanceof AMQConnectionFailureException ) {
				// log
				logger.info( "Failed to connect to qpid server." );
			}
			throw new RuntimeException( e );
		}
		catch ( JMSException e ) {
			throw new RuntimeException( e );
		}
	}

	public void send( String sender, String receiver, byte[] message ) throws Exception {
		if ( session == null ) {
			// 如果还没有做初始化连接，先创建连接
			connect();
		}

		// 设置连接的节点名，如果不存在该topic节点，则新建一个
		Destination dest = new AMQAnyDestination( "ADDR:" + receiver + "; {create: always, node:{ type: topic }}" );
		// 为指定的节点创建消息发送者
		MessageProducer producer = session.createProducer( dest );

		// 创建字节消息
		BytesMessage msg = session.createBytesMessage();
		msg.writeBytes( message );
		msg.setStringProperty( "from", sender );
		msg.setStringProperty( "to", receiver );

		// 发送消息
		producer.send( msg );

		// log
		logger.info( "Succeed to send text message" );
	}

	public void receive( String receiver ) {

	}
}
