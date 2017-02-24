package com.iauto.wlink.core.session;

public interface SessionListener {

	/**
	 * 会话创建时的处理
	 * 
	 * @param session
	 *          会话
	 * @param sequence
	 *          终端识别号的序列号(sequence<0:表示已发放过序列号，sequence>0:表示新发放的序列号)
	 */
	void onCreated( Session session, long sequence, int remain );

	/**
	 * 会话关闭时的处理
	 * 
	 * @param session
	 *          被删除的会话
	 * @param sequence
	 *          被删除的会话所对应的UID的序列号
	 * @param remain
	 *          剩下的UID对应的会话个数
	 */
	void onRemoved( Session session, long sequence, int remain );

}
