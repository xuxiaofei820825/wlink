package com.iauto.wlink.core.message;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.exception.MessageRouteException;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.session.SessionContext;
import com.iauto.wlink.core.tools.Executor;

/**
 * 使用QPID实现消息路由
 * 
 * @author xiaofei.xu
 * 
 */
public class QpidMessageRouter implements MessageRouter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 与MQ服务端的会话(每个IO线程创建一个连接，每个连接创建一个会话) */
	private final static ThreadLocal<Connection> connections = new ThreadLocal<Connection>();

	/** 会话编号与消息监听器的对应 */
	private final static Map<String, MessageConsumer> consumers = new HashMap<String, MessageConsumer>();

	/** 连接用URL */
	private final String url;

	/** 连接异常监听器 */
	private ExceptionListener exceptionListener;

	public QpidMessageRouter( String url ) {
		this.url = url;
	}

	/**
	 * 发送消息
	 */
	public void send( AbstractCommMessage<byte[]> message ) throws MessageRouteException {

		// 初始化
		Session session = null;

		try {
			// 获取当前线程的连接
			Connection conn = getConnection();

			// 如果当前线程没有连接，则为当前线程创建一个
			if ( conn == null ) {
				// 新建一个连接
				conn = newConnection( this.exceptionListener );
				// 添加到连接管理管理
				addConnection( conn );
			}

			// 与broker建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

			// 设置连接的节点名，如果不存在该topic节点，则新建一个
			Destination dest = new AMQAnyDestination( "ADDR:wlink.message.topic/" + message.to()
					+ ";{create:always, node:{ type:topic }}" );

			// 为指定的节点创建消息发送者
			MessageProducer producer = session.createProducer( dest );

			// 创建字节消息
			BytesMessage msg = session.createBytesMessage();
			msg.writeBytes( message.payload() );
			msg.setLongProperty( "from", message.from() );
			msg.setLongProperty( "to", message.to() );
			msg.setStringProperty( "type", message.type() );

			// 发送消息
			producer.send( msg );
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
		}
	}

	/**
	 * 注册会话
	 */
	public void register( SessionContext ctx ) throws MessageRouteException {
		try {
			// 获取当前线程的连接
			Connection conn = getConnection();

			// 如果当前线程没有连接，则为当前线程创建一个
			if ( conn == null ) {
				// 新建一个连接
				conn = newConnection( this.exceptionListener );
				// 添加到连接管理管理
				addConnection( conn );
			}

			// 为用户注册消息监听者
			Executor.execute( new MessageConsumerCreateTask( conn, ctx ) );
		} catch ( Exception ex ) {
			throw new MessageRouteException();
		}
	}

	/**
	 * 注销会话
	 */
	public void unregister( SessionContext ctx ) throws MessageRouteException {

		// 关闭消息监听器
		MessageConsumer consumer = getConsumer( ctx.getSession().getId() );

		try {
			if ( consumer != null ) {
				// close
				consumer.close();
				// log
				logger.info( "Succeed to close message consumer for user[ID:{}]", ctx.getSession().getUserId() );
			}
		} catch ( Exception ex ) {
			throw new MessageRouteException();
		}
	}

	// =======================================================================
	// private functions

	private Connection newConnection( final ExceptionListener exceptionListener ) throws Exception {
		// 与broker创建一个连接
		AMQConnection conn = new AMQConnection( url );
		// 添加异常监听器
		conn.setExceptionListener( exceptionListener );
		// 创建连接
		conn.start();
		return conn;
	}

	// =======================================================================
	// private class

	private class MessageConsumerCreateTask implements Runnable {

		/** 成员变量定义 */
		private final Connection conn;
		private final SessionContext ctx;

		public MessageConsumerCreateTask( Connection conn, SessionContext ctx ) {
			this.ctx = ctx;
			this.conn = conn;
		}

		public void run() {
			try {
				// info
				logger.info( "Creating the message consumer......" );

				// 在指定的会话上创建消息监听器
				MessageConsumer consumer = createConsumer( ctx.getChannel(), this.conn, ctx.getSession().getUserId() );

				// 添加到管理
				addConsumer( ctx.getSession().getId(), consumer );

				// info
				logger.info( "Succeed to create a message consumer for user[ID:{}]", ctx.getSession().getUserId() );
			} catch ( Exception e ) {
				// info
				logger.info( "Failed to create a message listener for user!!!", e );

				// 给终端反馈该错误
				ErrorMessage error = ErrorMessage.newBuilder()
					.setError( "Session_Create_Failure" )
					.build();
				ctx.getChannel().writeAndFlush( error );
			}
		}
	}

	// =======================================================================
	// static functions
	
	/*
	 * 创建消息监听器
	 */
	public static MessageConsumer createConsumer( final Channel channel, final Connection conn, final long userId )
			throws Exception {

		// 在当前的会话上创建消费者，监听发送给用户的消息
		String dest_url = String.format( "ADDR:wlink.message.topic/%s", userId );
		Destination dest = new AMQAnyDestination( dest_url );

		Session session = null;
		AMQConnection amqConn = (AMQConnection) conn;
		if ( amqConn.getSessions().size() == 0 ) {
			session = conn.createSession( false, Session.CLIENT_ACKNOWLEDGE );
		} else {
			session = amqConn.getSession( 0 );
		}

		// 设置监听器
		MessageConsumer consumer = session.createConsumer( dest );
		consumer.setMessageListener( new QpidMessageListener( channel, userId ) );

		return consumer;
	}

	public static void addConnection( Connection conn ) {
		connections.set( conn );
	}

	public static void addConsumer( String id, MessageConsumer consumer ) {
		consumers.put( id, consumer );
	}

	public static Connection getConnection() {
		return connections.get();
	}

	public static MessageConsumer getConsumer( String id ) {
		return consumers.get( id );
	}
}
