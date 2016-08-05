package com.iauto.wlink.core.message;

import io.netty.channel.Channel;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QPID消息处理器
 * 
 * @author xiaofei.xu
 * 
 */
public class QpidMessageListener implements MessageListener {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 通道 */
	private final Channel channel;

	/** 监听消息的用户 */
	private final long userId;

	public QpidMessageListener( Channel channel, long userId ) {
		this.channel = channel;
		this.userId = userId;
	}

	public void onMessage( Message message ) {
		try {
			// 获取属性
			long from = message.getLongProperty( "from" );
			long to = message.getLongProperty( "to" );
			String type = message.getStringProperty( "type" );

			if ( userId != to ) {
				// 消息接收者不一致
				logger.info( "The message receiver is not matched!!!" );
				return;
			}

			// log
			logger.info( "The user[ID:{}] receive a message. [from:{}, type:{}]", to, from, type );

			BytesMessage bytes = (BytesMessage) message;
			long len = bytes.getBodyLength();
			byte[] payload = new byte[(int) len];
			bytes.readBytes( payload );

			AbstractCommMessage<byte[]> commMsg = new DefaultCommMessage<byte[]>( type, payload, from, to );

			// 发送给接收者
			this.channel.writeAndFlush( commMsg );

			// 给MQ服务器发送确认消息
			message.acknowledge();
		} catch ( Exception ex ) {
			// error

			logger.info( "Failed to send the message to receiver! Caused by: {}", ex.getMessage() );
		}
	}
}
