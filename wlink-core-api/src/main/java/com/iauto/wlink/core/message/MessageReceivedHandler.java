package com.iauto.wlink.core.message;

public interface MessageReceivedHandler {
	void onMessage( String type, String from, String to, byte[] payload );
}
