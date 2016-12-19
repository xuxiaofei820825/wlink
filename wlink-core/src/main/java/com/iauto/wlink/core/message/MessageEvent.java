package com.iauto.wlink.core.message;

import com.iauto.wlink.core.session.Session;

public class MessageEvent {

	/** 通信消息 */
	private CommunicationMessage message;

	/** 当前会话 */
	private Session session;

	// =====================================================================
	// setter/getter

	public void setMessage( CommunicationMessage message ) {
		this.message = message;
	}

	public void setSession( Session session ) {
		this.session = session;
	}

	public CommunicationMessage getMessage() {
		return message;
	}

	public Session getSession() {
		return session;
	}
}
