package com.iauto.wlink.core.message;

import com.iauto.wlink.core.Message;

public class CommMessage extends Message {

	/** 发送者 */
	private String from;

	/** 接收者 */
	private String to;

	public CommMessage( String type, byte[] playload ) {
		super( type, playload );
	}

	// =============================================================
	// setter/getter

	public String getFrom() {
		return from;
	}

	public void setFrom( String from ) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo( String to ) {
		this.to = to;
	}
}
