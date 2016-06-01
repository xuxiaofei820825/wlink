package com.iauto.wlink.core.comm;

public class CommunicationPackage {

	/** 数据包类型 */
	private String type;

	/** 数据体 */
	private byte[] body;

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

	public byte[] getBody() {
		return body;
	}

	public void setBody( byte[] body ) {
		this.body = body;
	}

}
