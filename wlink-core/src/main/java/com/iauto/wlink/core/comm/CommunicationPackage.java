package com.iauto.wlink.core.comm;

public class CommunicationPackage {

	/** 数据包类型 */
	private String type;

	/** 数据头 */
	private byte[] header = new byte[] {};

	/** 数据体 */
	private byte[] body = new byte[] {};

	public CommunicationPackage() {
	}

	// ==========================================================
	// setter/getter

	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public byte[] getHeader() {
		return header;
	}

	public void setHeader( byte[] header ) {
		this.header = header;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody( byte[] body ) {
		this.body = body;
	}
}
