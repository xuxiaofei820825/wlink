package com.iauto.wlink.core.session;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

public class SessionContext {

	/** 线程级会话存储 */
	private static ThreadLocal<Map<String, SessionContext>> sessions = new ThreadLocal<Map<String, SessionContext>>() {
		@Override
		protected Map<String, SessionContext> initialValue() {
			return new HashMap<String, SessionContext>();
		}
	};

	/** 用户编号 */
	private final String userId;

	/** 会话编号 */
	private final String id;

	private final Channel channel;

	public SessionContext( final String userId, final Channel channel ) {
		this.userId = userId;
		this.id = UUID.randomUUID().toString().replace( "-", "" );
		this.channel = channel;
	}

	// =====================================================
	// setter/getter

	public String getUserId() {
		return userId;
	}

	public String getId() {
		return id;
	}

	public static void addSession( SessionContext session ) {
		sessions.get().put( session.getId(), session );
	}

	public static SessionContext getSession( String id ) {
		if ( StringUtils.isBlank( id ) )
			return null;

		return sessions.get().get( id );
	}

	public static Map<String, SessionContext> getSessions() {
		return sessions.get();
	}

	public Channel getChannel() {
		return channel;
	}
}
