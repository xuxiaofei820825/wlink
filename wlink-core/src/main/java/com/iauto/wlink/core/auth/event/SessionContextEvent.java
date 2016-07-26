package com.iauto.wlink.core.auth.event;

import com.iauto.wlink.core.auth.SessionContext;

public class SessionContextEvent {

	/** 会话上下文 */
	private final SessionContext sessionContext;

	public SessionContextEvent( SessionContext sessionContext ) {
		this.sessionContext = sessionContext;
	}

	// =============================================================
	// setter/getter

	public SessionContext getSessionContext() {
		return sessionContext;
	}
}
