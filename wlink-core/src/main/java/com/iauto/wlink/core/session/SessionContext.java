package com.iauto.wlink.core.session;

import io.netty.channel.Channel;

import javax.jms.MessageConsumer;

/**
 * 用户会话上下文环境<br/>
 * 包括:<br/>
 * <ul>
 * <li>用户会话
 * <li>用户连接通道
 * <li>用户的消息监听器
 * </ul>
 * 
 * @author xiaofei.xu
 * 
 */
public class SessionContext {

	/** 会话 */
	private final Session session;

	/** 用户对应的Channel */
	private final Channel channel;

	/** MQ消息监听器 */
	private MessageConsumer consumer;

	public SessionContext( Session session, Channel channel ) {
		this.session = session;
		this.channel = channel;
	}

	// =================================================================================
	// setter/getter

	public Channel getChannel() {
		return channel;
	}

	public MessageConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer( MessageConsumer consumer ) {
		this.consumer = consumer;
	}

	public Session getSession() {
		return session;
	}
}
