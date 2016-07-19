package com.iauto.wlink.core.auth.event;

import com.iauto.wlink.core.auth.SessionContext;

public class SessionContextEvent {

	/** 会话上下文 */
	private final SessionContext session;

	public SessionContextEvent( SessionContext session ) {
		this.session = session;
	}

	// =============================================================
	// setter/getter

	public SessionContext getSession() {
		return session;
	}
}
