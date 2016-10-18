package com.iauto.wlink.core.message;

public interface TerminalMessageHandler {
	void process( String type, String from, String to, byte[] payload );
}
