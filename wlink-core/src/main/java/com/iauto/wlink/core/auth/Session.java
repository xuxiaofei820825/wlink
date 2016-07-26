package com.iauto.wlink.core.auth;

public class Session {

	/** 用户编号 */
	private final long userId;

	/** 会话编号 */
	private final String id;

	/** 创建时间戳 */
	private final long timestamp;

	public Session( String id, long userId, long timestamp ) {
		this.id = id;
		this.userId = userId;
		this.timestamp = timestamp;
	}

	public long getUserId() {
		return userId;
	}

	public String getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
