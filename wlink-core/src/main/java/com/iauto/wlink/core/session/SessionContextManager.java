package com.iauto.wlink.core.session;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public abstract class SessionContextManager {

	/** 线程级会话上下文存储器 */
	private static ThreadLocal<Map<String, SessionContext>> sessions = new ThreadLocal<Map<String, SessionContext>>() {
		@Override
		protected Map<String, SessionContext> initialValue() {
			return new HashMap<String, SessionContext>();
		}
	};

	// =================================================================================
	// static functions

	/**
	 * 添加会话上下文
	 * 
	 * @param sessionCtx
	 *          会话上下文
	 */
	public static void add( SessionContext sessionCtx ) {
		// 判断是否为NULL
		if ( sessionCtx == null || sessionCtx.getSession() == null )
			throw new NullPointerException( "Session" );

		sessions.get().put( sessionCtx.getSession().getId(), sessionCtx );
	}

	/**
	 * 获取会话上下文
	 * 
	 * @param id
	 *          会话ID
	 * @return 会话上下文
	 */
	public static SessionContext get( String id ) {
		if ( StringUtils.isBlank( id ) )
			throw new IllegalArgumentException( "id is blank." );
		return sessions.get().get( id );
	}

	public static void remove( String id ) {
		if ( StringUtils.isBlank( id ) )
			throw new IllegalArgumentException( "id is blank." );
		sessions.get().remove( id );
	}

	/**
	 * 获取当前线程所有的会话上下文
	 */
	public static Map<String, SessionContext> getSessions() {
		return sessions.get();
	}
}
