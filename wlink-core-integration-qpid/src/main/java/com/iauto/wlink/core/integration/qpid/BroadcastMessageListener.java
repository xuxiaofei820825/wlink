package com.iauto.wlink.core.integration.qpid;

import io.netty.channel.Channel;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.BroadcastMessage;
import com.iauto.wlink.core.message.DefaultBroadcastMessage;

public class BroadcastMessageListener implements MessageListener {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 用户通道 */
	private final Channel channel;

	public BroadcastMessageListener( Channel channel ) {
		this.channel = channel;
	}

	public void onMessage( Message message ) {
		try {
			// 获取属性
			long from = message.getLongProperty( "from" );
			String type = message.getStringProperty( "type" );

			// info
			logger.info( "Receive a broadcast message from user[ID:{}]", from );

			BytesMessage bytes = (BytesMessage) message;
			long len = bytes.getBodyLength();
			byte[] payload = new byte[(int) len];
			bytes.readBytes( payload );

			BroadcastMessage<byte[]> commMsg =
					new DefaultBroadcastMessage<byte[]>( type, payload, from );

			// 发送给接收者
			channel.writeAndFlush( commMsg );

			// 给MQ服务器发送确认消息
			message.acknowledge();
		} catch ( Exception ex ) {
			// error
		}
	}
}
