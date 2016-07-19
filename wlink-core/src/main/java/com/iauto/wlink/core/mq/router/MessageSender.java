package com.iauto.wlink.core.mq.router;

import javax.jms.Connection;

public interface MessageSender {
	String send( Connection conn, String sender, String receiver, String type, byte[] message );
}
