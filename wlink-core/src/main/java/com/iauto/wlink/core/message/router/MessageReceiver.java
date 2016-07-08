package com.iauto.wlink.core.message.router;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import javax.jms.Connection;
import javax.jms.MessageConsumer;

public interface MessageReceiver {
	Connection newConnection( ChannelHandlerContext ctx ) throws Exception;

	MessageConsumer createConsumer( Channel channel, Connection conn, final String userId )
			throws Exception;
}
