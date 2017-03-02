package com.iauto.wlink.core.session;

import java.util.List;

public interface SessionManager {

	/**
	 * 获取指定的Session
	 * 
	 * @param uid
	 *          终端唯一识别号
	 * @param id
	 *          会话编号
	 * @return 匹配的会话实例
	 */
	Session get( String uid, String id );

	/**
	 * 获取指定的Session
	 * 
	 * @param uid
	 *          终端唯一识别号
	 * @param id
	 *          会话编号
	 * @return 匹配的会话实例
	 */
	List<Session> get( String uid );

	/**
	 * 添加会话到会话容器
	 * 
	 * @param session
	 *          会话实例
	 */
	void add( Session session );

	/**
	 * 删除指定的Session
	 * 
	 * @param uid
	 *          终端唯一识别号
	 * @param id
	 *          会话编号
	 * @return 被删除的会话实例
	 */
	Session remove( String uid, String id );

	/**
	 * 添加会话监听器
	 * 
	 * @param listener
	 *          会话监听器
	 */
	void addListener( SessionListener listener );
}
