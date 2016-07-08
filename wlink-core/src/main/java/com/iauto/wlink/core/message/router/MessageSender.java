package com.iauto.wlink.core.message.router;

public interface MessageSender {
	String send( String sender, String receiver, String type, byte[] message );
}
