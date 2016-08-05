package com.iauto.wlink.core.message.event;

import io.netty.channel.ChannelHandlerContext;

import javax.jms.Connection;

public class QpidReconnectedEvent {

	private final Connection connection;
	private final ChannelHandlerContext ctx;

	public QpidReconnectedEvent( Connection conn, ChannelHandlerContext ctx ) {
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
