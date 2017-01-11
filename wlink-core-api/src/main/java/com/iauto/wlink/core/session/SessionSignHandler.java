package com.iauto.wlink.core.session;

import com.iauto.wlink.core.exception.AuthenticationException;
import com.iauto.wlink.core.exception.InvalidSignatureSessionException;

public interface SessionSignHandler {
	String sign( String id, String tuid, long expireTime ) throws AuthenticationException;

	void validate( String id, String tuid, long expireTime, String signature ) throws InvalidSignatureSessionException;
}
