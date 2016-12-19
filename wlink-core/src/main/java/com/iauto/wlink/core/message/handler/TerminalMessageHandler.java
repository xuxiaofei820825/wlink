package com.iauto.wlink.core.message.handler;

import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.session.Session;

public class TerminalMessageHandler extends AbstractMessageHandler {

	@Override
	protected boolean handleMessage( Session session, CommunicationMessage message ) {

		
		// 传递给下一个处理器处理
		return false;
	}
}
