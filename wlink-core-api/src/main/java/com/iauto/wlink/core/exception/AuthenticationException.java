package com.iauto.wlink.core.exception;

@SuppressWarnings("serial")
public class AuthenticationException extends MessageProcessException {

	private static final String ERROR_CODE = "AUTHENTICATE_FAILURE";

	public AuthenticationException() {
		super( ERROR_CODE );
	}

}
