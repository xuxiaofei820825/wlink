package com.iauto.wlink.client;

public class Session {

	/** 会话编号 */
	private String id;

	/** 用户编号 */
	private String tUId;

	/** 时间戳 */
	private long expireTime;

	/** 签名 */
	private String signature;

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String gettUId() {
		return tUId;
	}

	public void settUId( String tUId ) {
		this.tUId = tUId;
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
