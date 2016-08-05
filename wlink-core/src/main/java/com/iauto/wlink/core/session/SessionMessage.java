package com.iauto.wlink.core.session;

import com.iauto.wlink.core.AbstractMessage;
import com.iauto.wlink.core.Constant;

public class SessionMessage extends AbstractMessage<Session> {

	public SessionMessage( Session layload ) {
		super( Constant.MessageType.Session, layload );
	}

}
