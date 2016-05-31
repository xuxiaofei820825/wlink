package com.iauto.wlink.core.comm.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.iauto.wlink.core.comm.proto.CommunicationHeaderProto.CommunicationHeader;

public class CommunicationEncoder extends MessageToByteEncoder<Object> {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	@Override
	protected void encode( ChannelHandlerContext ctx, Object msg, ByteBuf out ) throws Exception {

		// 发送消息头
		if ( msg instanceof CommunicationHeader ) {
			CommunicationHeader header = (CommunicationHeader) msg;
			byte[] headerBytes = header.toByteArray();

			// debug
			logger.debug( "Content-Type: {}, Content-Length: {}",
				header.getType(), header.getContentLength() );

			out.writeShort( headerBytes.length );
			out.writeBytes( headerBytes );
		}

		// 发送消息体
		if ( msg instanceof byte[] ) {
			out.writeBytes( (byte[]) msg );
		}
	}
}
