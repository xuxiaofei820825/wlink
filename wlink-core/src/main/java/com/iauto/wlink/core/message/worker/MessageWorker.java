package com.iauto.wlink.core.message.worker;

import io.netty.channel.ChannelHandlerContext;

public interface MessageWorker {
	void process( ChannelHandlerContext ctx, byte[] body ) throws Exception;
}
