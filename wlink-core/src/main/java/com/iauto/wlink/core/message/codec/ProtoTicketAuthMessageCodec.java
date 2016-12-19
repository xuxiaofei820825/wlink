package com.iauto.wlink.core.message.codec;

import com.google.protobuf.InvalidProtocolBufferException;
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

	public TicketAuthMessage decode( byte[] bytes ) {
		// 解码
		com.iauto.wlink.core.message.proto.TicketAuthMessageProto.TicketAuthMessage msg;
		try {
			msg = com.iauto.wlink.core.message.proto.TicketAuthMessageProto.TicketAuthMessage.parseFrom( bytes );
			return new TicketAuthMessage( msg.getTicket() );
		} catch ( InvalidProtocolBufferException e ) {
			e.printStackTrace();
		}

		return null;
	}
}
