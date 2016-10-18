package com.iauto.wlink.core.integration.qpid;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.client.Closeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.iauto.wlink.core.exception.MessageRouteException;
import com.iauto.wlink.core.message.TerminalMessageHandler;
import com.iauto.wlink.core.message.TerminalMessageRouter;

/**
 * 集成QPID实现终端消息路由器
 * 
 * @author xiaofei.xu
 * 
 */
public class QpidMessageRouter implements TerminalMessageRouter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 执行线程池 */
	private final ExecutorService executors = Executors.newFixedThreadPool( 10 );

	/** exchange name */
	public final static String P2P_EXCHANGE_NAME = "wlink.message.p2p.topic";
	public final static String BROADCAST_EXCHANGE_NAME = "wlink.message.broadcast.topic";

	/** QPID服务器连接URL */
	private final String url;

	/** QPID服务器连接 */
	private AMQConnection conn;

	/** 订阅者列表 */
	private final Set<Long> consumers = ConcurrentHashMap.newKeySet();

	private TerminalMessageHandler messageHandler;

	/** 会话集合 */
	private final static ThreadLocal<Session> sessions = new ThreadLocal<Session>();

	public QpidMessageRouter( final String url ) {
		this.url = url;
	}

	/**
	 * 开始
	 */
	public void start() {
		createConnection();
	}

	/**
	 * 订阅指定用户的消息。
	 * 
	 * @param publisher
	 *          消息发布者(被订阅者)
	 * @param listener
	 *          消息监听器
	 */
	public ListenableFuture<?> subscribe( final long publisher )
			throws MessageRouteException {

		// 初始化为NULL
		ListenableFuture<?> future = null;

		future = MoreExecutors.listeningDecorator( this.executors ).submit( new Runnable() {
			public void run() {
				try {
					Session session = getSession();

					// 设置监听器
					Destination dest = new AMQAnyDestination(
						String.format( "ADDR:%s/%s", P2P_EXCHANGE_NAME, publisher ) );
					MessageConsumer consumer = session.createConsumer( dest );
					consumer.setMessageListener( new QpidMessageListener() );

					consumers.add( publisher );
				} catch ( Exception ex ) {
					throw new MessageRouteException( ex );
				}
			}
		} );

		return future;
	}

	public void onMessageReceived( String type, String from, String to, byte[] payload ) {
		// 处理消息
		messageHandler.process( type, from, to, payload );
	}

	/**
	 * 发送一个点对点消息
	 * 
	 * @param type
	 *          消息类型
	 * @param from
	 *          发送者
	 * @param to
	 *          接收者
	 * @param message
	 *          消息有效荷载
	 */
	public ListenableFuture<?> send( final String type, final String from, final String to, final byte[] message )
			throws MessageRouteException {

		ListenableFuture<?> future = null;

		// 执行异步任务
		future = MoreExecutors.listeningDecorator( executors ).submit( new Runnable() {
			public void run() {
				try {
					Session session = getSession();

					// 设置连接的节点名，如果不存在该topic节点，则新建一个
					Destination dest = new AMQAnyDestination(
						String.format( "ADDR:%s/%s;{create:always,node:{type:topic}}",
							P2P_EXCHANGE_NAME, to ) );

					// 为指定的节点创建消息发送者
					MessageProducer producer = session.createProducer( dest );

					// 创建字节消息
					BytesMessage msg = session.createBytesMessage();
					msg.writeBytes( message );
					msg.setStringProperty( "from", from );
					msg.setStringProperty( "to", to );
					msg.setStringProperty( "type", type );

					// 发送消息
					producer.send( msg );
				} catch ( Exception ex ) {
					throw new MessageRouteException( ex );
				}
			}
		} );

		return future;
	}

	public ListenableFuture<?> broadcast( final String type, final String from, final byte[] message )
			throws MessageRouteException {

		// 初始化为NULL
		ListenableFuture<?> future = null;

		// 执行异步任务
		future = MoreExecutors.listeningDecorator( executors ).submit( new Runnable() {
			public void run() {
				try {

					Session session = getSession();

					// 在当前的会话上创建消费者，监听发送给用户的消息
					String dest_url = String.format( "ADDR:%s/%s", BROADCAST_EXCHANGE_NAME, from );
					Destination dest = new AMQAnyDestination( dest_url );

					MessageProducer producer = session.createProducer( dest );

					// 创建字节消息
					BytesMessage msg = session.createBytesMessage();
					msg.setStringProperty( "from", from );
					msg.setStringProperty( "type", type );
					msg.writeBytes( message );

					// 发送
					producer.send( msg );
				} catch ( Exception ex ) {
					throw new MessageRouteException( ex );
				} finally {

				}
			}
		} );

		return future;
	}

	// =======================================================================
	// private functions

	/*
	 * 创建与QPID服务器的连接
	 */
	private void createConnection() {

		try {
			// 与broker创建一个连接
			conn = new AMQConnection( url );

			// 设置异常处理器
			conn.setExceptionListener( new QpidConnectionExceptionListener() );

			// 创建连接
			conn.start();
		} catch ( Exception ex ) {
			throw new RuntimeException( ex );
		}
	}

	/*
	 * 获取当前线程的Session,如果还未建立,则新建一个
	 */
	private Session getSession() throws JMSException {
		Session session = sessions.get();

		if ( ( session == null || ( (Closeable) session ).isClosed() )
				&& conn != null && !conn.isClosed() ) {
			session = conn.createSession( false, Session.CLIENT_ACKNOWLEDGE );
			sessions.set( session );
		}
		return session;
	}

	// =======================================================================
	// private class

	/**
	 * <p>
	 * QPID连接异常处理类
	 * </p>
	 * 需要做如下处理：
	 * <ul>
	 * <li>重新建立与QPID服务器的连接</li>
	 * <li>为已连接的用户重新建立消息的监听</li>
	 * </ul>
	 * 
	 * @author xiaofei.xu
	 * 
	 */
	private class QpidConnectionExceptionListener implements ExceptionListener {

		public void onException( JMSException exception ) {
			logger.error( "Error occoured. Caused by:{}",
				exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage() );

			boolean isSuccess = false;

			while ( !isSuccess ) {
				try {

					// 与QPID服务器创建一个连接
					conn = new AMQConnection( url );
					conn.start();

					// 重新订阅用户消息
					for ( Long uuid : consumers ) {
						subscribe( uuid );
					}

					// succeed to reconnect QPID server
					isSuccess = true;
				} catch ( Exception ex ) {
					// error
					logger.error( "Failed to reconnect qpid server, 5 seconds later, try to reconnect again. Caused by:{}",
						ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage() );

					// sleep 5 seconds
					try {
						Thread.sleep( 5000 );
					} catch ( InterruptedException e ) {
						// ignore
					}
				}
			}
		}
	}

	private class QpidMessageListener implements MessageListener {

		/** logger */
		private final Logger logger = LoggerFactory.getLogger( getClass() );

		public void onMessage( Message message ) {
			try {

				// 获取属性
				String from = message.getStringProperty( "from" );
				String to = message.getStringProperty( "to" );
				String type = message.getStringProperty( "type" );

				BytesMessage bytes = (BytesMessage) message;
				long len = bytes.getBodyLength();
				byte[] payload = new byte[(int) len];
				bytes.readBytes( payload );

				onMessageReceived( type, from, to, payload );

				// 给MQ服务器发送确认消息
				message.acknowledge();
			} catch ( Exception ex ) {
				// error
				logger.info( "Exception occurred when processing the message! Caused by: {}", ex.getMessage() );
			}
		}
	}
}
