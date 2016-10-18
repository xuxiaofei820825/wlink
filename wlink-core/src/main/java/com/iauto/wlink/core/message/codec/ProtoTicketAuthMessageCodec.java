package com.iauto.wlink.core.message.codec;

import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.TicketAuthMessage;

public class ProtoTicketAuthMessageCodec implements MessageCodec<TicketAuthMessage> {

	public byte[] encode( TicketAuthMessage msg ) {
		// 编码
		String ticket = msg.getTicket();
		return com.iauto.wlink.core.message.proto.TicketAuthMessageProto.TicketAuthMessage
			.newBuilder().setTicket( ticket )
			.build().toByteArray();
	}

	public TicketAuthMessage decode( byte[] bytes ) throws Exception {
		// 解码
		com.iauto.wlink.core.message.proto.TicketAuthMessageProto.TicketAuthMessage msg =
				com.iauto.wlink.core.message.proto.TicketAuthMessageProto.TicketAuthMessage.parseFrom( bytes );
		return new TicketAuthMessage( msg.getTicket() );
	}
}
