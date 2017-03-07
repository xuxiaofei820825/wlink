package com.iauto.wlink.core.exception;

@SuppressWarnings("serial")
public class NoCommMessageHandlerException extends MessageProcessException {

	private static final String ERROR_CODE = "NO_COMM_MESSAGE_HANDLER";

	public NoCommMessageHandlerException() {
		super( ERROR_CODE );
	}
}
