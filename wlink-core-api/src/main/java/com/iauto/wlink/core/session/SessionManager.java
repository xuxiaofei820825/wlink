package com.iauto.wlink.core.session;

public interface SessionManager {

	/**
	 * 获取指定的Session
	 * 
	 * @param tuid
	 *          终端唯一识别号
	 * @param id
	 *          会话编号
	 * @return 匹配的会话实例
	 */
	Session get( String tuid, String id );

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
	 * @param tuid
	 *          终端唯一识别号
	 * @param id
	 *          会话编号
	 * @return 被删除的会话实例
	 */
	Session remove( String tuid, String id );

	/**
	 * 添加会话监听器
	 * 
	 * @param listener
	 *          会话监听器
	 */
	void addListener( SessionListener listener );
}
