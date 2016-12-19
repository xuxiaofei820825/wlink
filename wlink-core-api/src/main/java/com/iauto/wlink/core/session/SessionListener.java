package com.iauto.wlink.core.session;

public interface SessionListener {

	/**
	 * 会话创建时的处理
	 * 
	 * @param session
	 *          会话
	 */
	void onCreated( Session session );

	/**
	 * 会话关闭时的处理
	 * 
	 * @param session
	 *          会话
	 */
	void onClosed( Session session );

}
