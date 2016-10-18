package com.iauto.wlink.core.session;

/**
 * 用户会话
 * 
 * @author xiaofei.xu
 * 
 */
public class Session {

	/** 用户编号 */
	private final String uuid;

	/** 会话编号 */
	private final String id;

	/** 创建时间戳 */
	private final long timestamp;

	public Session( String id, String userId, long timestamp ) {
		this.id = id;
		this.uuid = userId;
		this.timestamp = timestamp;
	}

	// ============================================================
	// setter/getter

	public String getUuId() {
		return uuid;
	}

	public String getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
