package com.iauto.wlink.server.message;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.comm.CommunicationPayload;
import com.iauto.wlink.core.message.DefaultTerminalMessage;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.MessageReceivedHandler;
import com.iauto.wlink.core.message.TerminalMessage;
import com.iauto.wlink.server.channel.SessionManager;

/**
 * <p>
 * 针对Netty4实现的终端消息处理器。
 * </p>
 * 实现以下功能:
 * <ul>
 * <li>接收到路由消息时，写入对应的用户通道</li>
 * </ul>
 * 
 * @author xiaofei.xu
 * 
 */
public class NettyTerminalMessageHandler implements MessageReceivedHandler,
		InitializingBean {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 终端消息编/解码器 */
	private MessageCodec<TerminalMessage> messageCodec;

	public void afterPropertiesSet() throws Exception {
		Assert.notNull( messageCodec, "Terminal message codec is required." );
	}

	public void onMessage( String type, String from, String to, byte[] payload ) {
		// debug log
		logger.debug( "Receive a message. [type:{}, from:{}, to:{}, payload-length:{}]", type, from, to, payload.length );

		// 获得所有唯一号对应的通道
		ConcurrentHashMap<String, Channel> channels = SessionManager.get( String.valueOf( to ) );
		if ( channels == null || channels.size() == 0 ) {
			// debug log
			logger.debug( "Channel is not exist, do nothing." );
			return;
		}

		DefaultTerminalMessage message =
				new DefaultTerminalMessage( type, from, to, payload );

		// 封装成通讯包
		CommunicationPayload comm = new CommunicationPayload();
		comm.setType( MessageType.P2PMessage );
		comm.setPayload( messageCodec.encode( message ) );

		for ( Channel channel : channels.values() ) {
			channel.writeAndFlush( comm );
		}

		// debug log
		logger.debug( "Send message to receiver(UUID:{}).", to );
	}

	public void setMessageCodec( MessageCodec<TerminalMessage> messageCodec ) {
		this.messageCodec = messageCodec;
	}
}
