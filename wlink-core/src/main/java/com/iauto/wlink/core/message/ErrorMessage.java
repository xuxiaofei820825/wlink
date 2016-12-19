package com.iauto.wlink.core.message;

public class ErrorMessage extends AbstractSystemMessage {

	private String code;

	public ErrorMessage( final String code ) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode( String code ) {
		this.code = code;
	}
}
