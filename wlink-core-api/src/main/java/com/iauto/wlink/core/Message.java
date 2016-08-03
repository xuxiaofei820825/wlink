package com.iauto.wlink.core;

public abstract class Message {

	/** 消息类型 */
	private String type;

	/** 有效荷载 */
	private byte[] playload;

	public Message( String type, byte[] playload ) {
		this.type = type;
		this.playload = playload;
	}

	// =========================================================
	// setter/getter

	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public byte[] getPlayload() {
		return playload;
	}

	public void setPlayload( byte[] playload ) {
		this.playload = playload;
	}
}
