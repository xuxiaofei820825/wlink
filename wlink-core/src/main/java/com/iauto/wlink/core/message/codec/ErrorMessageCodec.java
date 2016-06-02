package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;

public class ErrorMessageCodec extends MessageToMessageCodec<CommunicationPackage, ErrorMessage> {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	@Override
	protected void encode( ChannelHandlerContext ctx, ErrorMessage msg, List<Object> out ) throws Exception {
		// 获取文本消息的ProtoBuffer编码
		byte[] errMsgBytes = msg.toByteArray();

		CommunicationPackage comm = new CommunicationPackage();
		comm.setType( "error" );
		comm.setBody( errMsgBytes );

		// 传递到下一个处理器
		out.add( comm );
	}

	@Override
	protected void decode( ChannelHandlerContext ctx, CommunicationPackage msg, List<Object> out ) throws Exception {
		// 获取数据包类型
		String type = msg.getType();

		// 如果不是文本消息，则流转到下一个处理器
		if ( !StringUtils.equals( "error", type ) ) {
			out.add( msg );
			return;
		}

		// log
		logger.info( "Decoding the error message......" );

		// 解码错误消息
		ErrorMessage error = ErrorMessage.parseFrom( msg.getBody() );

		// debug
		logger.debug( "Error: {}", error.getError() );
	}

}
