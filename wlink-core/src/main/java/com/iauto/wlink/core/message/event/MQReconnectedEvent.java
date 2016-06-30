package com.iauto.wlink.core.message.event;

import io.netty.channel.ChannelHandlerContext;

import org.apache.qpid.client.AMQConnection;

public class MQReconnectedEvent {

	private final AMQConnection connection;
	private final ChannelHandlerContext ctx;

	public MQReconnectedEvent( AMQConnection conn, ChannelHandlerContext ctx ) {
		this.connection = conn;
		this.ctx = ctx;
	}

	public AMQConnection getConnection() {
		return connection;
	}

	public ChannelHandlerContext getContext() {
		return ctx;
	}
}
