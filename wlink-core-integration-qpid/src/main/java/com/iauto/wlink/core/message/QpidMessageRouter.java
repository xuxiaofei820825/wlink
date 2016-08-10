package com.iauto.wlink.core.message;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.iauto.wlink.core.exception.MessageRouteException;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.session.SessionContext;
import com.iauto.wlink.core.tools.Executor;

/**
 * 集成QPID实现消息路由
 * 
 * @author xiaofei.xu
 * 
 */
public class QpidMessageRouter implements MessageRouter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 会话编号与消息监听器的对应 */
	private final static Map<String, MessageConsumer> consumers = new HashMap<String, MessageConsumer>();

	/**
	 * 构造函数
	 * 
	 */
	public QpidMessageRouter() {
	}

	/**
	 * 发送消息
	 */
	public ListenableFuture<Object> send( AbstractCommMessage<byte[]> message )
			throws MessageRouteException {

		// 初始化为NULL
		ListenableFuture<Object> future = null;

		try {
			// 获取当前线程的连接
			AMQConnection conn = QpidConnectionManager.get();

			// 不能为NULL
			if ( conn == null ) {
				throw new MessageRouteException();
			}

			// 执行异步任务
			future = MoreExecutors.listeningDecorator( Executors.newFixedThreadPool( 10 ) )
				.submit( new MessageSendTask( conn, message ), null );
		} catch ( Exception ex ) {
			// error
			logger.error( "Failed to send the message!!! Caused by: {}", ex.getMessage() );
		}

		return future;
	}

	/**
	 * 注册会话
	 */
	public void register( SessionContext ctx ) throws MessageRouteException {
		try {
			// 获取当前I/O线程的连接
			AMQConnection conn = QpidConnectionManager.get();

			// 不能为NULL
			if ( conn == null ) {
				throw new MessageRouteException();
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
	// private class

	private class MessageSendTask implements Runnable {

		private final Connection conn;
		private final AbstractCommMessage<byte[]> message;

		public MessageSendTask( Connection conn, AbstractCommMessage<byte[]> message ) {
			this.conn = conn;
			this.message = message;
		}

		public void run() {

			// 初始化
			Session session = null;

			try {

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

	}

	/**
	 * 创建消息监听器任务
	 * 
	 * @author xiaofei.xu
	 */
	private class MessageConsumerCreateTask implements Runnable {

		/** QPID连接 */
		private final Connection conn;

		/** 会话上下文 */
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

	public static void addConsumer( String id, MessageConsumer consumer ) {
		consumers.put( id, consumer );
	}

	public static MessageConsumer getConsumer( String id ) {
		return consumers.get( id );
	}
}
