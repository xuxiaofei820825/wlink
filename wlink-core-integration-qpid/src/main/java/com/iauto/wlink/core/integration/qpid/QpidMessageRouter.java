package com.iauto.wlink.core.integration.qpid;

import io.netty.channel.Channel;

import javax.jms.BytesMessage;
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
import com.iauto.wlink.core.Constant;
import com.iauto.wlink.core.exception.MessageRouteException;
import com.iauto.wlink.core.message.BroadcastMessage;
import com.iauto.wlink.core.message.MessageRouter;
import com.iauto.wlink.core.message.PointToPointMessage;
import com.iauto.wlink.core.session.SessionContext;

/**
 * 集成QPID实现消息路由
 * 
 * @author xiaofei.xu
 * 
 */
public class QpidMessageRouter implements MessageRouter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** exchange name */
	private final static String P2P_EXCHANGE_NAME = "wlink.message.p2p.topic";
	private final static String BROADCAST_EXCHANGE_NAME = "wlink.message.broadcast.topic";

	/**
	 * 注册用户会话
	 */
	public ListenableFuture<?> register( SessionContext ctx ) throws MessageRouteException {

		// 初始化为NULL
		ListenableFuture<?> future = null;

		// 获取当前I/O线程的连接
		AMQConnection conn = ConnectionManager.get();

		// 不能为NULL
		if ( conn == null || conn.isClosed() ) {
			throw new MessageRouteException();
		}

		// 执行异步任务
		future = MoreExecutors.listeningDecorator( Constant.executors )
			.submit( new PointToPointMessageConsumerCreateTask( conn, ctx ) );

		return future;
	}

	/**
	 * 发送消息
	 */
	public ListenableFuture<?> send( PointToPointMessage<byte[]> message )
			throws MessageRouteException {

		// 初始化为NULL
		ListenableFuture<?> future = null;

		// 获取当前线程的连接
		AMQConnection conn = ConnectionManager.get();

		// 不能为NULL
		if ( conn.isClosed() ) {
			throw new MessageRouteException();
		}

		// 执行异步任务
		future = MoreExecutors.listeningDecorator( Constant.executors )
			.submit( new PointToPointMessageSendTask( conn, message ) );

		return future;
	}

	/**
	 * 注销用户会话
	 */
	public void unregister( SessionContext ctx ) throws MessageRouteException {

		// 关闭消息监听器
		MessageConsumer consumer = ConsumerManager.get( ctx.getSession().getId() );

		try {
			if ( consumer != null ) {
				// close
				consumer.close();
				ConsumerManager.remove( ctx.getSession().getId() );
				// log
				logger.info( "Succeed to close message consumer for user[ID:{}]", ctx.getSession().getUserId() );
			}
		} catch ( Exception ex ) {
			throw new MessageRouteException();
		}
	}

	public ListenableFuture<?> broadcast( BroadcastMessage<byte[]> message ) throws MessageRouteException {

		// 初始化为NULL
		ListenableFuture<?> future = null;

		// 获取当前线程的连接
		AMQConnection conn = ConnectionManager.get();

		// 判定连接是否有效
		if ( conn == null || conn.isClosed() ) {
			throw new MessageRouteException();
		}

		// 执行异步任务
		future = MoreExecutors.listeningDecorator( Constant.executors )
			.submit( new MessageBroadcastTask( conn, message ) );

		return future;
	}

	public ListenableFuture<?> subscribe( SessionContext ctx, long userId ) throws MessageRouteException {
		// 初始化为NULL
		ListenableFuture<?> future = null;

		// 获取当前线程的连接
		AMQConnection conn = ConnectionManager.get();

		// 判定连接是否有效
		if ( conn == null || conn.isClosed() ) {
			throw new MessageRouteException();
		}

		// 执行异步任务
		future = MoreExecutors.listeningDecorator( Constant.executors )
			.submit( new BroadMessageSubscribeTask( conn, ctx, userId ) );

		return future;
	}

	// =======================================================================
	// private class

	private class PointToPointMessageSendTask implements Runnable {

		private final AMQConnection conn;
		private final PointToPointMessage<byte[]> message;

		public PointToPointMessageSendTask( AMQConnection conn, PointToPointMessage<byte[]> message ) {
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
				Destination dest = new AMQAnyDestination(
					String.format( "ADDR:%s/%s;{create:always,node:{type:topic}}",
						P2P_EXCHANGE_NAME, message.to() ) );

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
	private class PointToPointMessageConsumerCreateTask implements Runnable {

		/** QPID连接 */
		private final AMQConnection conn;

		/** 会话上下文 */
		private final SessionContext ctx;

		public PointToPointMessageConsumerCreateTask( AMQConnection conn, SessionContext ctx ) {
			this.ctx = ctx;
			this.conn = conn;
		}

		public void run() {
			try {
				// info
				logger.info( "Creating the message consumer......" );

				long userId = ctx.getSession().getUserId();
				String sessionId = ctx.getSession().getId();

				// 为指定的会话创建消息监听器
				MessageConsumer consumer = createConsumer( ctx.getChannel(), this.conn, userId );

				// 添加到管理
				ConsumerManager.add( sessionId, consumer );

				// 广播出席状态
				// presence( ctx.getSession().getUserId() );
			} catch ( Exception e ) {
				throw new RuntimeException( e );
			}
		}
	}

	private class BroadMessageSubscribeTask implements Runnable {

		private final AMQConnection conn;
		private final SessionContext ctx;
		private final long userId;

		public BroadMessageSubscribeTask( AMQConnection conn, SessionContext ctx, long userId ) {
			this.ctx = ctx;
			this.conn = conn;
			this.userId = userId;
		}

		public void run() {

			try {
				// 获取会话
				Session session = conn.getSessions().size() == 0 ?
						conn.createSession( false, Session.CLIENT_ACKNOWLEDGE ) : conn.getSession( 0 );

				// 监听指定用户的广播消息
				Destination dest = new AMQAnyDestination(
					String.format( "ADDR:%s/%s", BROADCAST_EXCHANGE_NAME, userId ) );
				MessageConsumer consumer = session.createConsumer( dest );
				consumer.setMessageListener( new BroadcastMessageListener( ctx.getChannel() ) );
			} catch ( Exception ex ) {
				throw new MessageRouteException();
			}
		}
	}

	private class MessageBroadcastTask implements Runnable {

		private final AMQConnection conn;
		private final BroadcastMessage<byte[]> message;

		public MessageBroadcastTask( AMQConnection conn, BroadcastMessage<byte[]> message ) {
			this.conn = conn;
			this.message = message;
		}

		public void run() {

			Session session = null;

			try {
				// 在当前的会话上创建消费者，监听发送给用户的消息
				String dest_url = String.format( "ADDR:%s/%s", BROADCAST_EXCHANGE_NAME, message.from() );
				Destination dest = new AMQAnyDestination( dest_url );

				// 创建会话
				session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );
				MessageProducer producer = session.createProducer( dest );

				// 创建字节消息
				BytesMessage msg = session.createBytesMessage();
				msg.setLongProperty( "from", message.from() );
				msg.setStringProperty( "type", message.type() );
				msg.writeBytes( message.payload() );

				// 发送
				producer.send( msg );
			} catch ( Exception ex ) {
				throw new MessageRouteException();
			} finally {
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

	// =======================================================================
	// static functions

	/*
	 * 创建消息监听器
	 */
	public static MessageConsumer createConsumer( final Channel channel, final AMQConnection conn, final long userId )
			throws Exception {

		// 在当前的会话上创建消费者，监听发送给用户的消息
		String dest_url = String.format( "ADDR:%s/%s", P2P_EXCHANGE_NAME, userId );
		Destination dest = new AMQAnyDestination( dest_url );

		Session session = null;
		if ( conn.getSessions().size() == 0 ) {
			session = conn.createSession( false, Session.CLIENT_ACKNOWLEDGE );
		} else {
			session = conn.getSession( 0 );
		}

		// 设置监听器
		MessageConsumer consumer = session.createConsumer( dest );
		consumer.setMessageListener( new PointToPointMessageListener( channel, userId ) );

		return consumer;
	}

}
