package com.iauto.wlink.core.exception;

@SuppressWarnings("serial")
public class UnAuthenticatedException extends MessageProcessException {

	private static final String ERROR_CODE = "UN_AUTHENTICATED";

	public UnAuthenticatedException() {
		super( ERROR_CODE );
	}

}
