package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge;

public class MessageAcknowledgeCodec extends MessageToMessageCodec<CommunicationPackage, MessageAcknowledge> {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	@Override
	protected void encode( ChannelHandlerContext ctx, MessageAcknowledge msg, List<Object> out ) throws Exception {
		// 获取文本消息的ProtoBuffer编码
		byte[] ackBytes = msg.toByteArray();

		CommunicationPackage comm = new CommunicationPackage();
		comm.setType( "msg_ack" );
		comm.setBody( ackBytes );

		// 传递到下一个处理器
		out.add( comm );
	}

	@Override
	protected void decode( ChannelHandlerContext ctx, CommunicationPackage msg, List<Object> out ) throws Exception {
		// 获取数据包类型
		String type = msg.getType();

		// 如果不是文本消息，则流转到下一个处理器
		if ( !StringUtils.equals( "msg_ack", type ) ) {
			out.add( msg );
			return;
		}

		// info
		logger.info( "Decoding the acknowledge message......" );

		// 解码消息体
		MessageAcknowledge ack = MessageAcknowledge.parseFrom( msg.getBody() );

		if ( ack.getResult() == MessageAcknowledge.Result.SUCCESS ) {
			// info
			logger.info( "Succeed to send a message!!! ID:{}, Type:{}", ack.getMessageId(), ack.getAckType() );
		}
	}
}
