package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import com.iauto.wlink.core.comm.proto.CommunicationHeaderProto.CommunicationHeader;
import com.iauto.wlink.core.message.proto.TextMessageProto.TextMessage;

public class TextMessageEncoder extends MessageToMessageEncoder<TextMessage> {

	@Override
	protected void encode( ChannelHandlerContext ctx, TextMessage msg, List<Object> out ) throws Exception {
		// 获取认证消息的ProtoBuffer编码
		byte[] txtMsgBytes = msg.toByteArray();

		// 组织消息头
		CommunicationHeader header = CommunicationHeader.newBuilder()
			.setType( "text" )
			.setContentLength( txtMsgBytes.length )
			.build();

		// 传递到下一个处理器
		out.add( header );
		out.add( txtMsgBytes );
	}
}
