package com.iauto.wlink.core.message;

public class SessionMessage extends AbstractSystemMessage {

	private String id;
	private String uuid;
	private long timestamp;
	private String signature;

	// ========================================================
	// setter/getter

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid( String uuid ) {
		this.uuid = uuid;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp( long timestamp ) {
		this.timestamp = timestamp;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature( String signature ) {
		this.signature = signature;
	}

}
