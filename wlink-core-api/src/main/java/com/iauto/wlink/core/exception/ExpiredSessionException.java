package com.iauto.wlink.core.exception;

@SuppressWarnings("serial")
public class ExpiredSessionException extends MessageProcessException {

	private static final String ERROR_CODE = "EXPIRED_SESSION";

	public ExpiredSessionException() {
		super( ERROR_CODE );
	}

}
