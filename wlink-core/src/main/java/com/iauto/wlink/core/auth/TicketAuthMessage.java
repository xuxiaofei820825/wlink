package com.iauto.wlink.core.auth;

import com.iauto.wlink.core.AbstractMessage;
import com.iauto.wlink.core.Constant;

public class TicketAuthMessage extends AbstractMessage<TicketAuthentication> {

	public TicketAuthMessage( TicketAuthentication playload ) {
		super( Constant.MessageType.Auth, playload );
	}
}
