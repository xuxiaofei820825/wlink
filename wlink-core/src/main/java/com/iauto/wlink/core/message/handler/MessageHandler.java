package com.iauto.wlink.core.message.handler;

import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.session.Session;

public interface MessageHandler {
	void handleMessage( Session session, CommunicationMessage message )
			throws MessageProcessException;
}
