package com.iauto.wlink.core.message.event;

import com.iauto.wlink.core.session.SessionContext;

public class SessionContextEvent {

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
