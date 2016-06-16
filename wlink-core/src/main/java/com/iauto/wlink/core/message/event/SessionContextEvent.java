package com.iauto.wlink.core.message.event;

import com.iauto.wlink.core.session.SessionContext;

public class SessionContextEvent {

	private final SessionContext context;

	public SessionContextEvent( SessionContext context ) {
		this.context = context;
	}

	public SessionContext getContext() {
		return context;
	}

}
