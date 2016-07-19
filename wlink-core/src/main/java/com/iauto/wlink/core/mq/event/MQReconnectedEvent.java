package com.iauto.wlink.core.mq.event;

import io.netty.channel.ChannelHandlerContext;

import javax.jms.Connection;

public class MQReconnectedEvent {

	private final Connection connection;
	private final ChannelHandlerContext ctx;

	public MQReconnectedEvent( Connection conn, ChannelHandlerContext ctx ) {
		this.connection = conn;
		this.ctx = ctx;
	}

	public Connection getConnection() {
		return connection;
	}

	public ChannelHandlerContext getContext() {
		return ctx;
	}
}
