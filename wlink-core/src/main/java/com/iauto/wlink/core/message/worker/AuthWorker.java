package com.iauto.wlink.core.message.worker;

import io.netty.channel.ChannelHandlerContext;

public class AuthWorker implements Runnable {

	private final ChannelHandlerContext ctx;

	public AuthWorker( ChannelHandlerContext ctx ) {
		this.ctx = ctx;
	}

	public void run() {

		// 模拟耗时的网络请求
		try {
			Thread.sleep( 3000 );
		}
		catch ( InterruptedException e ) {
			// ignore
		}

		this.ctx.fireChannelRead( new String( "Success" ) );
	}
}
