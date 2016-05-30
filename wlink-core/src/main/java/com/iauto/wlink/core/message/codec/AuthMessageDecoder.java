package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class AuthMessageDecoder extends MessageToMessageDecoder<Object> {

	@Override
	protected void decode( ChannelHandlerContext ctx, Object msg, List<Object> out ) throws Exception {

	}
}
