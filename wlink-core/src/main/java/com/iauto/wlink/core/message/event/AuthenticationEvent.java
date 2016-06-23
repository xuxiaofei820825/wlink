package com.iauto.wlink.core.message.event;

public class AuthenticationEvent {
	private final String ticket;

	public AuthenticationEvent( String ticket ) {
		this.ticket = ticket;
	}

	// =============================================================
	// setter/getter

	public String getTicket() {
		return ticket;
	}
}
