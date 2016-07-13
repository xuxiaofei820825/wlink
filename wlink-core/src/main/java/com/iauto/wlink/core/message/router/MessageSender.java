package com.iauto.wlink.core.message.router;

import javax.jms.Connection;

public interface MessageSender {
	String send( Connection conn, String sender, String receiver, String type, byte[] message );
}
