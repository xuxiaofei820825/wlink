package com.iauto.wlink.core.exception;

@SuppressWarnings("serial")
public abstract class MessageProcessException extends Exception {

	protected String errorCode;
	
	public MessageProcessException(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
