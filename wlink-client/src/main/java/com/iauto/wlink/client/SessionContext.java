package com.iauto.wlink.client;

public class SessionContext {

	/** 会话编号 */
	private String id;

	/** 用户编号 */
	private String userId;

	/** 时间戳 */
	private long timestamp;

	/** 签名 */
	private String signature;

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId( String userId ) {
		this.userId = userId;
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
