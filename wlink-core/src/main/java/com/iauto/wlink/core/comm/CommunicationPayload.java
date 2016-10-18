package com.iauto.wlink.core.comm;

public class CommunicationPayload {

	/** 荷载类型 */
	private String type;

	/** 荷载的属性 */
	//private byte[] properties;

	/** 有效荷载 */
	private byte[] payload;

	public CommunicationPayload() {

	}

	// ==========================================================
	// setter/getter

	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}

//	public byte[] getProperties() {
//		return properties;
//	}
//
//	public void setProperties( byte[] properties ) {
//		this.properties = properties;
//	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload( byte[] payload ) {
		this.payload = payload;
	}
}
