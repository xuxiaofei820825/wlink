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
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

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
			conn = factory.newConnection();

			for ( int idx = 0; idx < CHANNELS_SIZE; idx++ ) {
				channels[idx] = conn.createChannel();
			}

			// channel = conn.createChannel();
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
		// info log
		logger.info( "Starting to route a terminal message. type:{}, from:{}, to:{}, message-length:{}",
				type, from, to, message.length );

		final long sequence = messageTotal.incrementAndGet();
		final int idx = (int) ( ( sequence - 1 ) % CHANNELS_SIZE );
		final Channel channel = this.channels[idx];

		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put( "from", from );
		headers.put( "to", to );

		BasicProperties properties = new AMQP.BasicProperties.Builder()
				.deliveryMode( 2 )
				.priority( 0 )
				.headers( headers )
				.build();

		try {
			channel.basicPublish( EXCHANGE_NAME, "user." + to + ".message",
					properties, message );
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

		// sequence < 0 表示UID已被发放过，不需要重新监听
		if ( remain > 1 )
			return;

		final String uid = session.getUid();
		final int idx = (int) ( ( sequence - 1 ) % CHANNELS_SIZE );
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
			boolean autoAck = false;
			channel.basicConsume( uid, autoAck, uid, new DefaultConsumer( channel ) {
				@Override
				public void handleDelivery( String consumerTag, Envelope envelope,
						AMQP.BasicProperties properties, byte[] body ) throws IOException {

					Map<String, Object> headers = properties.getHeaders();
					String from = headers.get( "from" ).toString();
					String to = headers.get( "to" ).toString();

					logger.error( "from:{}, to:{}, message:{}", from, to, new String( body, "UTF-8" ) );

					long deliveryTag = envelope.getDeliveryTag();

					// (process the message components here ...)
					channel.basicAck( deliveryTag, false );
				}
			} );
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
		final int idx = (int) ( ( sequence - 1 ) % CHANNELS_SIZE );
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
}
