package com.iauto.wlink.core.integration.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.iauto.wlink.core.exception.MessageRouteException;
import com.iauto.wlink.core.message.MessageReceivedHandler;
import com.iauto.wlink.core.message.TerminalMessageRouter;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionListener;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;

public class RabbitMQMessageRouter implements TerminalMessageRouter, SessionListener {

	/** logger */
	private final static Logger logger = LoggerFactory.getLogger( RabbitMQMessageRouter.class );

	/** 交换机(exchange)名 */
	private final static String EXCHANGE_NAME = "terminal.message.topic";

	/** Channel的个数 */
	private final static int CHANNELS_SIZE = 8;

	/** Channel数组 */
	private Channel[] channels = new Channel[CHANNELS_SIZE];

	/** 与Broker的连接 */
	private Connection conn;

	private AtomicLong messageTotal = new AtomicLong( 0 );

	/** RabbitMQ消息监听器 */
	private Consumer consumer;

	@Override
	public void init() {
		// info log
		logger.info( "Initializing the rabbitMQ message router......" );

		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername( "guest" );
		factory.setPassword( "guest" );
		factory.setVirtualHost( "/" );
		factory.setHost( "172.26.188.165" );
		factory.setPort( 5672 );

		try {
			// 创建一个新连接
			conn = factory.newConnection();

			// 创建固定数目的Channel
			for ( int idx = 0; idx < CHANNELS_SIZE; idx++ ) {
				channels[idx] = conn.createChannel();
			}

			// 使用第一个Channel定义一个交换器(exchange)
			channels[0].exchangeDeclare( EXCHANGE_NAME, "topic", true );

			// info log
			logger.info( "Succeed to init the rabbitMQ message router." );
		}
		catch ( IOException | TimeoutException e ) {
			// error log
			logger.error( "Failed to init rabbitmq message router.", e );
		}
	}

	@Override
	public ListenableFuture<?> send( String type, String from, String to, byte[] message ) throws MessageRouteException {
		// debug log
		logger.debug( "starting to route a terminal message. type:{}, from:{}, to:{}, payload:{}bytes",
				type, from, to, message.length );

		final long sequence = messageTotal.incrementAndGet();
		final int idx = calculateIndex( sequence );
		final Channel channel = this.channels[idx];

		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put( "from", from );
		headers.put( "to", to );
		headers.put( "type", type );

		BasicProperties properties = new AMQP.BasicProperties.Builder()
				.deliveryMode( 2 )
				.priority( 0 )
				.headers( headers )
				.build();

		try {
			channel.basicPublish( EXCHANGE_NAME, "user." + to + ".message",
					properties, message );

			// info log
			logger.info( "succeed to route a terminal message. type:{}, from:{}, to:{}, payload:{}bytes",
					type, from, to, message.length );
		}
		catch ( IOException e ) {
			// error log
			logger.error( "Failed to send message.", e );
		}

		return null;
	}

	@Override
	public ListenableFuture<?> broadcast( String type, String from, byte[] message ) throws MessageRouteException {
		return null;
	}

	@Override
	public void setMessageReceivedHandler( MessageReceivedHandler messageReceivedHandler ) {

	}

	// ===================================================================
	// 实现会话监听器的接口

	@Override
	public void onCreated( final Session session, final long sequence, final int remain ) {

		if ( remain > 1 )
			return;

		final String uid = session.getUid();
		final int idx = calculateIndex( sequence );
		final Channel channel = this.channels[idx];

		try {
			// 为用户创建一个队列，并把队列绑定到exchange
			// durable:false 服务器重启就删除
			// exclusive:false 不被当前连接独占
			// autoDelete:true 没有监听者时就自动删除
			channel.queueDeclare( uid, false, false, true, null );
			// 队列接收用户的所有消息
			channel.queueBind( uid, EXCHANGE_NAME, "user." + uid + ".*" );

			// 设置消息监听
			boolean autoAck = true;
			channel.basicConsume( uid, autoAck, uid, consumer );
		}
		catch ( IOException e ) {
			// warn
			logger.warn( "Error occoured.", e );

			// info log
			logger.info( "Failed to remove consumer for uid:{}.", uid );
		}
	}

	@Override
	public void onRemoved( Session session, long sequence, int remain ) {
		if ( remain > 0 )
			return;

		final String uid = session.getUid();
		final int idx = calculateIndex( sequence );
		final Channel channel = this.channels[idx];

		try {
			// 解除消息订阅
			channel.basicCancel( uid );
		}
		catch ( IOException e ) {
			// warn
			logger.warn( "Error occoured.", e );

			// info log
			logger.info( "Failed to remove consumer for uid:{}.", uid );
		}
	}

	private int calculateIndex( long sequence ) {
		return (int) ( ( sequence - 1 ) % CHANNELS_SIZE );
	}

	public void setConsumer( Consumer consumer ) {
		this.consumer = consumer;
	}
}
