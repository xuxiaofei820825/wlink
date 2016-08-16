package com.iauto.wlink.core.integration.qpid;

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
	public final static String P2P_EXCHANGE_NAME = "wlink.message.p2p.topic";
	public final static String BROADCAST_EXCHANGE_NAME = "wlink.message.broadcast.topic";

	/**
	 * 注册用户会话
	 */
	public ListenableFuture<?> subscribe( final SessionContext ctx ) throws MessageRouteException {

		// 初始化为NULL
		ListenableFuture<?> future = null;

		// 获取当前I/O线程的连接
		final AMQConnection conn = ConnectionManager.get();

		// 不能为NULL
		if ( conn == null || conn.isClosed() ) {
			throw new MessageRouteException();
		}

		// 执行异步任务
		final long userId = ctx.getSession().getUserId();
		final String sessionId = ctx.getSession().getId();

		future = MoreExecutors.listeningDecorator( Constant.executors )
			.submit( new Runnable() {
				public void run() {
					try {

						// 创建消息监听
						MessageConsumer consumer = QpidTools.createConsumer(
							String.format( "ADDR:%s/%s", P2P_EXCHANGE_NAME, userId ),
							conn, new PointToPointMessageListener( ctx.getChannel() ) );

						// 添加到管理
						ConsumerManager.add( sessionId, consumer );
					} catch ( Exception ex ) {
						throw new RuntimeException( ex );
					}
				}
			} );

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
			throw new RuntimeException( ex );
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

	public ListenableFuture<?> subscribe( final SessionContext ctx, final long userId ) throws MessageRouteException {
		// 初始化为NULL
		ListenableFuture<?> future = null;

		// 获取当前线程的连接
		final AMQConnection conn = ConnectionManager.get();

		// 判定连接是否有效
		if ( conn == null || conn.isClosed() ) {
			throw new MessageRouteException();
		}

		// 执行异步任务
		future = MoreExecutors.listeningDecorator( Constant.executors )
			.submit( new Runnable() {
				public void run() {
					try {
						// 创建消息监听
						QpidTools.createConsumer( String.format( "ADDR:%s/%s", BROADCAST_EXCHANGE_NAME, userId ),
							conn, new BroadcastMessageListener( ctx.getChannel() ) );
					} catch ( Exception ex ) {
						throw new RuntimeException( ex );
					}
				}
			} );

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
				throw new RuntimeException( ex );
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
}
