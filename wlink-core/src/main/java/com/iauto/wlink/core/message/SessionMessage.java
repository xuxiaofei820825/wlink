package com.iauto.wlink.core.message;

public class SessionMessage extends AbstractSystemMessage {

	private String id;
	private String tuid;
	private long expireTime;
	private String signature;

	// ========================================================
	// setter/getter

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String getTuid() {
		return tuid;
	}

	public void setTuid( String tuid ) {
		this.tuid = tuid;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime( long expireTime ) {
		this.expireTime = expireTime;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature( String signature ) {
		this.signature = signature;
	}

}
