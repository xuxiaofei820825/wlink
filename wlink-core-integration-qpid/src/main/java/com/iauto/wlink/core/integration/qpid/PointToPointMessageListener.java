package com.iauto.wlink.core.integration.qpid;

import io.netty.channel.Channel;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.PointToPointMessage;
import com.iauto.wlink.core.message.DefaultPointToPointMessage;

/**
 * QPID消息处理器，该类执行接收到用户消息后的动作
 * 
 * @author xiaofei.xu
 * 
 */
public class PointToPointMessageListener implements MessageListener {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 用户通道 */
	private final Channel channel;

	/** 用户编号 */
	private final long userId;

	/**
	 * 构造函数
	 * 
	 * @param channel
	 *          用户通道
	 * @param userId
	 *          用户编号
	 */
	public PointToPointMessageListener( Channel channel, long userId ) {
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

			PointToPointMessage<byte[]> commMsg =
					new DefaultPointToPointMessage<byte[]>( type, payload, from, to );

			// 发送给接收者
			channel.writeAndFlush( commMsg );

			// 给MQ服务器发送确认消息
			message.acknowledge();
		} catch ( Exception ex ) {
			// error

			logger.info( "Failed to send the message to receiver! Caused by: {}", ex.getMessage() );
		}
	}
}
