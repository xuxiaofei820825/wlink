package com.iauto.wlink.core.message;

public interface TerminalMessage {
	String type();

	String from();

	String to();

	byte[] payload();
}
