package com.iauto.wlink.core.exception;

@SuppressWarnings("serial")
public class InvalidMessageRouterException extends MessageProcessException {

	private static final String ERROR_CODE = "INVALID_MESSAGE_ROUTER";

	public InvalidMessageRouterException() {
		super( ERROR_CODE );
	}
}
