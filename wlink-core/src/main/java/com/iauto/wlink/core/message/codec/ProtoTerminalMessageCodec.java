package com.iauto.wlink.core.message.codec;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.iauto.wlink.core.message.DefaultTerminalMessage;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.TerminalMessage;

public class ProtoTerminalMessageCodec implements MessageCodec<TerminalMessage> {

	public byte[] encode( TerminalMessage payload ) {

		// 编码
		return com.iauto.wlink.core.message.proto.TerminalMessageProto.TerminalMessage.newBuilder()
			.setType( payload.type() )
			.setFrom( String.valueOf( payload.from() ) )
			.setTo( String.valueOf( payload.to() ) )
			.setPayload( ByteString.copyFrom( payload.payload() ) )
			.build().toByteArray();
	}

	public TerminalMessage decode( byte[] bytes ) {

		com.iauto.wlink.core.message.proto.TerminalMessageProto.TerminalMessage msg;
		try {
			msg = com.iauto.wlink.core.message.proto.TerminalMessageProto.TerminalMessage.parseFrom( bytes );

			byte[] payload = msg.getPayload().toByteArray();

			TerminalMessage terminalMsg = new DefaultTerminalMessage( msg.getType(), msg.getFrom(), msg.getTo(),
				payload );

			return terminalMsg;
		} catch ( InvalidProtocolBufferException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
