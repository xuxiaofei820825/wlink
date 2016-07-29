package com.iauto.wlink.core.auth;

public interface SessionSignatureHandler {
	String sign( Session session ) throws Exception;

	boolean validate( Session session, String signature ) throws Exception;
}
