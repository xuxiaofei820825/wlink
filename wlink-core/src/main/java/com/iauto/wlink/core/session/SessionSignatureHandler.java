package com.iauto.wlink.core.session;


public interface SessionSignatureHandler {
	String sign( Session session ) throws Exception;

	boolean validate( Session session, String signature ) throws Exception;
}
