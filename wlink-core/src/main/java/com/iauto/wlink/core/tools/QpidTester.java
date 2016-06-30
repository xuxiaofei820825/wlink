package com.iauto.wlink.core.tools;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.PooledConnectionFactory;
import org.apache.qpid.url.URLSyntaxException;

import com.iauto.wlink.core.session.SessionContext;

public class QpidTester {

	private static final String url =
			"amqp://guest:guest@test/test?brokerlist='tcp://172.26.188.173:5672'";

	private static Connection conn;
	private static Session session;

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

	public static void main( String[] args ) throws Exception {
		// 建立连接
		connect();

		for ( int idx = 1; idx <= 100; idx++ ) {

			System.out.println( "Create a message consumer!!! Count:" + idx );

			// 创建消息监听器
			createConsumer();
		}
	}

	public static void connect() throws Exception {

		try {

			// 从连接池中获取连接
			conn = pooledConnectionFactory.createConnection();

			// client
			System.out.println( "Client-ID: " + conn.getClientID() );

			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

			// 启动连接
			conn.start();

			System.out.println( "Succeed to create a connection to MQ server." );
		} finally {
			SessionContext.getSession("");
		}
	}

	private static void createConsumer() throws Exception {

		// 与broker建立Session
		// Session session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

		// 设置连接的节点名，如果不存在该topic节点，则新建一个
		Destination dest = new AMQAnyDestination( "ADDR:" + "message.topic" + "/xuhongjuan" );
		MessageConsumer consumer = session.createConsumer( dest );

		consumer.setMessageListener( new MessageListener() {
			public void onMessage( Message message ) {
				System.err.println( "==========Receive a message!!!==========" );
			}
		} );
	}
}
