package com.iauto.wlink.core.session;

public class SessionContext {

	/** 用户编号 */
	private String userId;

	public SessionContext( final String userId ) {
		this.userId = userId;
	}

	// =====================================================
	// setter/getter

	public String getUserId() {
		return userId;
	}

	public void setUserId( String userId ) {
		this.userId = userId;
	}
}
