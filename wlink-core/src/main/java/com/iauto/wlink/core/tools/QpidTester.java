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

public class QpidTester {

	private static final String url =
			"amqp://guest:guest@test/test?brokerlist='tcp://172.26.188.173:5672'";

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
		// TODO Auto-generated method stub
		receive();

		System.out.println( "============================AAAAAAA==============================" );

		for ( int idx = 1; idx <= 100; idx++ ) {

			Destination dest2 = new AMQAnyDestination( "ADDR:" + "message.topic" + "/xuxiaofei" );
			MessageConsumer consumer2 = session.createConsumer( dest2 );

			consumer2.setMessageListener( new MessageListener() {
				public void onMessage( Message message ) {
					System.out.println( "====================QQQQQQQQQQQQQQ====================" );
				}
			} );
		}
	}

	public static void receive() throws Exception {
		Connection conn = null;

		try {

			// 从连接池中获取连接
			conn = pooledConnectionFactory.createConnection();

			// 与broker建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

			// 设置连接的节点名，如果不存在该topic节点，则新建一个
			Destination dest1 = new AMQAnyDestination( "ADDR:" + "message.topic" + "/xuhongjuan" );
			MessageConsumer consumer1 = session.createConsumer( dest1 );

			consumer1.setMessageListener( new MessageListener() {
				public void onMessage( Message message ) {
					System.out.println( "====================XXXXXXXXXXXXXX====================" );
				}
			} );

			System.out.println( "====================RRRRRRRRRRRRRRRR====================" );

			conn.start();

			// 接收消息
			// consumer.receive();

			System.out.println( "Succeed to create a message listener." );

		} finally {
// if ( conn != null ) {
// try {
// conn.close();
// } catch ( JMSException e ) {
// // ignore
// }
// }
		}
	}
}
