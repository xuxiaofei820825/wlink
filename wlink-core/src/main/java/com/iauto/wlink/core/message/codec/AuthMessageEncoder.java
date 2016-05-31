package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import com.iauto.wlink.core.comm.proto.CommunicationHeaderProto.CommunicationHeader;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;

public class AuthMessageEncoder extends MessageToMessageEncoder<AuthMessage> {

	@Override
	protected void encode( ChannelHandlerContext ctx, AuthMessage msg, List<Object> out ) throws Exception {

		// 类型装换
		AuthMessage authMsg = (AuthMessage) msg;

		// 获取认证消息的ProtoBuffer编码
		byte[] authMsgBytes = authMsg.toByteArray();

		// 组织消息头
		CommunicationHeader header = CommunicationHeader.newBuilder()
			.setType( "auth" )
			.setContentLength( authMsgBytes.length )
			.build();

		// 传递到下一个处理器
		out.add( header );
		out.add( authMsgBytes );
	}
}
