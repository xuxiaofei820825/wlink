package com.iauto.wlink.core.message;

public class SessionMessage extends AbstractSystemMessage {

	private String id;
	private String uid;
	private long expiredTime;
	private String signature;

	// ========================================================
	// setter/getter

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid( String uid ) {
		this.uid = uid;
	}

	public long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime( long expiredTime ) {
		this.expiredTime = expiredTime;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature( String signature ) {
		this.signature = signature;
	}

}
