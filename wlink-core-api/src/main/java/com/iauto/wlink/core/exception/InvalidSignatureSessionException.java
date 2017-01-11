package com.iauto.wlink.core.exception;

@SuppressWarnings("serial")
public class InvalidSignatureSessionException extends MessageProcessException {

	private static final String ERROR_CODE = "INVALID_SESSION_SIGNATURE";

	public InvalidSignatureSessionException() {
		super( ERROR_CODE );
	}
}
