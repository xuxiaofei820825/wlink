package com.iauto.wlink.core.session;

import com.iauto.wlink.core.exception.AuthenticationException;

public interface SessionSignHandler {
	String sign( Session session ) throws AuthenticationException;

	boolean validate( Session session, String signature ) throws AuthenticationException;
}
