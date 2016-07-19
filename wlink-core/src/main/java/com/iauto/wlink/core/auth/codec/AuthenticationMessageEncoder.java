package com.iauto.wlink.core.auth.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import com.iauto.wlink.core.Constant;
import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;

/**
 * 对认证消息进行编码
 * 
 * @author xiaofei.xu
 * 
 */
public class AuthenticationMessageEncoder extends MessageToMessageEncoder<AuthMessage> {

	@Override
	protected void encode( ChannelHandlerContext ctx, AuthMessage msg, List<Object> out ) throws Exception {
		// 获取认证消息的ProtoBuffer编码
		byte[] authMsgBytes = msg.toByteArray();

		CommunicationPackage comm = new CommunicationPackage();
		comm.setType( Constant.MessageType.Auth );
		comm.setHeader( new byte[] {} );
		comm.setBody( authMsgBytes );

		// 传递到下一个处理器
		out.add( comm );
	}
}
