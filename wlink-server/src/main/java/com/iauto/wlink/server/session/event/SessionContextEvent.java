package com.iauto.wlink.server.session.event;

import com.iauto.wlink.core.session.SessionContext;

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
