package com.iauto.wlink.core.message;

public class TicketAuthMessage extends AbstractSystemMessage {

	/** 认证票据 */
	private String ticket;

	public TicketAuthMessage( String ticket ) {
		this.ticket = ticket;
	}

	// ========================================================
	// setter/getter

	public String getTicket() {
		return ticket;
	}

	public void setTicket( String ticket ) {
		this.ticket = ticket;
	}
}
