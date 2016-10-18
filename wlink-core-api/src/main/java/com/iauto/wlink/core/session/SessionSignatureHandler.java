package com.iauto.wlink.core.session;

import com.iauto.wlink.core.exception.AuthenticationException;

public interface SessionSignatureHandler {
	String sign( Session session ) throws AuthenticationException;

	boolean validate( Session session, String signature ) throws AuthenticationException;
}
