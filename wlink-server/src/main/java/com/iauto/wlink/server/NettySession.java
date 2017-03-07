package com.iauto.wlink.server;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.session.AbstractSession;
import com.iauto.wlink.core.session.Session;

/**
 * 使用NETTY实现会话
 * 
 * @author xiaofei.xu
 * 
 */
public final class NettySession extends AbstractSession {

	/** logger */
	private final static Logger logger = LoggerFactory.getLogger( NettySession.class );

	/** 会话键值 */
	public static final AttributeKey<Session> SessionKey =
			AttributeKey.newInstance( "Terminal_Session" );

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
		return session != null && StringUtils.isNotEmpty( session.getUid() );
	}

	/**
	 * 发送响应消息
	 */
	public void send( CommunicationMessage message ) {
		// check
		if ( this.channel == null )
			throw new IllegalArgumentException( "channel is null." );

		// check
		if ( message == null || message.payload() == null )
			throw new IllegalArgumentException( "invalid communication message." );

		// debug log
		logger.debug( "type:{}, payload:{}", message.type(), message.payload().length );

		this.channel.writeAndFlush( message );
	}

	// ==================================================================
	// setter/getter

	public void setChannel( Channel channel ) {
		this.channel = channel;
	}
}
