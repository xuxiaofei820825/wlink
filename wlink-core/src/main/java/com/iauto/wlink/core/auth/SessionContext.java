package com.iauto.wlink.core.auth;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

import javax.jms.MessageConsumer;

import org.apache.commons.lang.StringUtils;

/**
 * 会话上下文环境<br/>
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

	/** 线程级会话存储 */
	private static ThreadLocal<Map<String, SessionContext>> sessions = new ThreadLocal<Map<String, SessionContext>>() {
		@Override
		protected Map<String, SessionContext> initialValue() {
			return new HashMap<String, SessionContext>();
		}
	};

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

	// =================================================================================
	// static functions

	public static void add( SessionContext sessionCtx ) {
		if ( sessionCtx == null || sessionCtx.getSession() == null )
			return;

		sessions.get().put( sessionCtx.getSession().getId(), sessionCtx );
	}

	public static SessionContext getSessionContext( String id ) {
		if ( StringUtils.isBlank( id ) )
			return null;
		return sessions.get().get( id );
	}

	public static Map<String, SessionContext> getSessions() {
		return sessions.get();
	}

}
