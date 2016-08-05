package com.iauto.wlink.core.auth;

public class TicketAuthentication implements Authentication {

	/** 票据 */
	private final String ticket;

	private long userId;

	public TicketAuthentication( String ticket ) {
		this.ticket = ticket;
	}

	public long principal() {
		return this.userId;
	}

	public Object credential() {
		return this.ticket;
	}
	
	public void setPrincipal(long userId) {
		this.userId = userId;
	}
}
