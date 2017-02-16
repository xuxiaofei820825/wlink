package com.iauto.wlink.core;

import com.iauto.wlink.core.message.MessageListener;
import com.iauto.wlink.core.session.SessionManager;

/**
 * 该接口抽象NIO层向核心层必须提供的功能。<br/>
 * 
 * <ul>
 * <li>提供终端连接状态变化的通知</li>
 * <li>提供通讯消息到达的通知</li>
 * </ul>
 * 
 * @author xiaofei.xu
 * 
 */
public interface NioHandler {

	/**
	 * 注册会话管理器
	 * 
	 * @param sessionManager
	 *          会话管理器
	 */
	void registerSessionManager( SessionManager sessionManager );

	/**
	 * 注册通讯消息监听器
	 * 
	 * @param msgListener
	 *          消息监听器
	 */
	void registerMessageListener( MessageListener messageListener );

}
