package com.iauto.wlink.core.auth;

import com.iauto.wlink.core.Message;
import com.iauto.wlink.core.MessageCodec;
import com.iauto.wlink.core.auth.proto.AuthMessageProto.AuthMessage;

public class DefaultTicketAuthMessageCodec implements MessageCodec<TicketAuthentication> {

	public byte[] encode( TicketAuthentication payload ) {
		String ticket = (String) payload.credential();
		AuthMessage msg = AuthMessage.newBuilder()
			.setTicket( ticket )
			.build();

		return msg.toByteArray();
	}

	public Message<TicketAuthentication> decode( byte[] bytes ) throws Exception {
		// 解码
		AuthMessage authMsg = AuthMessage.parseFrom( bytes );
		TicketAuthentication authentication = new TicketAuthentication( authMsg.getTicket() );
		return new com.iauto.wlink.core.auth.TicketAuthMessage( authentication );
	}
}
