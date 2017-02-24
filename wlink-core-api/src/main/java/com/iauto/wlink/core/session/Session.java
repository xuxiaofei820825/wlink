package com.iauto.wlink.core.session;

import com.iauto.wlink.core.message.CommunicationMessage;

public interface Session {

	/**
	 * 终端会话是否已被认证
	 * 
	 * @return true:已认证,false:未认证
	 */
	boolean isAuthenticated();

	/**
	 * 向终端发送消息
	 * 
	 * @param message
	 *          被发送的消息
	 */
	void send( CommunicationMessage message );

	/**
	 * 设置会话编号
	 * 
	 * @param id
	 *          会话编号
	 */
	void setId( String id );

	/**
	 * 获取会话编号
	 * 
	 * @return 会话编号
	 */
	String getId();

	/**
	 * 设置终端唯一识别码
	 * 
	 * @param id
	 *          终端唯一识别码
	 */
	void setUid( String id );

	/**
	 * 获取终端唯一识别码
	 * 
	 * @return 终端唯一识别码
	 */
	String getUid();
	
	/**
	 * 设置会话超时时间
	 * 
	 * @return 会话超时时间
	 */
	void setExpiredTime(long expireTime);

	/**
	 * 获取会话超时时间
	 * 
	 * @return 会话超时时间
	 */
	long getExpireTime();
}
