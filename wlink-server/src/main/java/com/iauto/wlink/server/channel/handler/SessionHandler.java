package com.iauto.wlink.server.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionIdGenerator;
import com.iauto.wlink.core.session.SessionListener;
import com.iauto.wlink.core.session.UUIDSessionIdGenerator;
import com.iauto.wlink.server.NettySession;

public class SessionHandler extends ChannelInboundHandlerAdapter {

	/** 会话监听器 */
	private SessionListener sessionListener;

	/** 会话ID生成器 */
	private SessionIdGenerator sessionIdGenerator = new UUIDSessionIdGenerator();

	/** 会话超时时间(默认60 days) */
	private int expireDays = 60;

	public void channelInactive( ChannelHandlerContext ctx ) throws Exception {
		// 获取保存的Session对象
		Session session = ctx.channel().attr( NettySession.SessionKey ).get();

		if ( session != null && sessionListener != null )
			// 触发会话关闭的事件
			sessionListener.onClosed( session );

		ctx.fireChannelInactive();
	}

	public void channelActive( ChannelHandlerContext ctx ) throws Exception {
		// 创建会话
		Session session = new NettySession( ctx.channel() );
		String id = sessionIdGenerator.generate();
		session.setId( id );

		// 设置过期时间
		long expireTime = System.currentTimeMillis() + expireDays * 24 * 60 * 60 * 1000;
		session.setExpireTime( expireTime );

		// 保存在Channel的属性中
		ctx.channel().attr( NettySession.SessionKey ).set( session );

		// 触发会话创建的事件
		sessionListener.onCreated( session );

		ctx.fireChannelActive();
	}

	// =======================================================================
	// setter/getter

	public void setSessionListener( SessionListener sessionListener ) {
		this.sessionListener = sessionListener;
	}

	public void setSessionIdGenerator( SessionIdGenerator sessionIdGenerator ) {
		this.sessionIdGenerator = sessionIdGenerator;
	}

	public void setExpireDays( int expireDays ) {
		this.expireDays = expireDays;
	}
}
