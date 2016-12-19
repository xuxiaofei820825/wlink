package com.iauto.wlink.core.message;

public class CommunicationMessage {

	private String type;
	private byte[] payload;

	public CommunicationMessage() {
	}

	public CommunicationMessage( final String type, final byte[] payload ) {
		this.type = type;
		this.payload = payload;
	}

	public String type() {
		return this.type;
	}

	public byte[] payload() {
		return this.payload;
	}

	// ===================================================
	// setter

	public void setType( String type ) {
		this.type = type;
	}

	public void setPayload( byte[] payload ) {
		this.payload = payload;
	}
}
