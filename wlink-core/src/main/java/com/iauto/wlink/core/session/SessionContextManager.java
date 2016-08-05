package com.iauto.wlink.core.session;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.iauto.wlink.core.session.SessionContext;

/**
 * 该类实现对会话上下文的统一管理
 * 
 * @author xiaofei.xu
 * 
 */
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
	 * 添加会话上下文到当前的线程
	 * 
	 * @param sessionCtx
	 *          会话上下文
	 */
	public static void add( SessionContext sessionCtx ) {
		// 判断是否为NULL
		if ( sessionCtx == null || sessionCtx.getSession() == null )
			throw new NullPointerException( "Session" );

		// 保存
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

	/**
	 * 从当前线程中删除指定的会话上下文
	 * 
	 * @param id
	 *          会话ID
	 */
	public static void remove( String id ) {
		if ( StringUtils.isBlank( id ) )
			throw new IllegalArgumentException( "id is blank." );
		// 删除
		sessions.get().remove( id );
	}

	/**
	 * 获取当前线程所有的会话上下文
	 */
	public static Map<String, SessionContext> getSessions() {
		return sessions.get();
	}
}
