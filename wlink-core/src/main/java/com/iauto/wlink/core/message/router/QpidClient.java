package com.iauto.wlink.core.message.router;

import java.net.URISyntaxException;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.qpid.AMQConnectionFailureException;
import org.apache.qpid.AMQException;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.url.URLSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QpidClient {

	/** logger */
	private static final Logger logger = LoggerFactory.getLogger( QpidClient.class );

	/** broker的URL */
	private String url;

	/** 节点名 */
	private String node;

	/** 与broker的连接 */
	private Connection conn;

	/** 与broker的会话 */
	private Session session;

	/** 目标节点 */
	private Destination dest;

	/** 目标节点的消息发送者 */
	private MessageProducer producer;

	public QpidClient( String url, String node ) {
		this.url = url;
		this.node = node;
	}

	public void connect() {
		// log
		logger.info( "Connect to MQ Server. url:{}, node:{}", this.url, this.node );

		try {

			// 与broker建立连接
			conn = new AMQConnection( url );
			// 与broker建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );
			// 设置连接的节点名，如果不存在该topic节点，则新建一个
			dest = new AMQAnyDestination( "ADDR:" + node + "; {create: always, node:{ type: topic }}" );
			// 为指定的节点创建消息发送者
			producer = session.createProducer( dest );
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
		catch ( URISyntaxException e ) {
			throw new RuntimeException( e );
		}
	}

	public void send( String message ) {
		if ( session == null || producer == null ) {
			// 如果还没有做初始化连接，先创建连接

			// connect
			connect();

			// send message
			send( message );

			return;
		}

		try {

			Message msg = session.createTextMessage( message );
			producer.send( msg );

			// log
			logger.info( "Succeed to send text message" );
		}
		catch ( JMSException e ) {
			// 如果发送失败，尝试重新连接并发送消息
			// 如果重新连接发生错误，则会丢弃该消息

			// log
			logger.warn( "Failed to send message, try to reconnect and send message." );

			// connect
			connect();

			// send message
			send( message );
		}
	}
}
