package com.iauto.wlink.server;

import org.apache.commons.lang.StringUtils;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.session.AbstractSession;
import com.iauto.wlink.core.session.Session;

public class NettySession extends AbstractSession {

	/** 会话键值 */
	public static final AttributeKey<Session> SessionKey =
			AttributeKey.newInstance( "session" );

	/** 终端对应的Channel */
	private Channel channel;

	/**
	 * 构造函数
	 */
	public NettySession( final Channel channel ) {
		this.channel = channel;
	}

	/**
	 * 通道是否已认证
	 */
	public boolean isAuthenticated() {
		// check
		if ( this.channel == null )
			throw new IllegalArgumentException();

		// 判断终端会话是否已经建立
		Session session = this.channel.attr( SessionKey ).get();
		return session != null && StringUtils.isNotEmpty( session.getTUId() );
	}

	/**
	 * 发送响应消息
	 */
	public void send( CommunicationMessage message ) {
		// check
		if ( this.channel == null )
			throw new IllegalArgumentException();

		this.channel.writeAndFlush( message );
	}

	// ==================================================================
	// setter/getter

	public void setChannel( Channel channel ) {
		this.channel = channel;
	}
}
