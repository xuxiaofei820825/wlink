package com.iauto.wlink.server.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatHandler extends SimpleChannelInboundHandler<String> {

	// logger
	private final static Logger logger = LoggerFactory.getLogger( HeartbeatHandler.class );

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		// 判断当前事件是否为IdleStateEvent
		if ( evt instanceof IdleStateEvent ) {
			IdleStateEvent event = (IdleStateEvent) evt;

			// 通道(Channel)上读空闲，终端未发送心跳包维持连接，或者物理链路已经断开
			if ( event.state().equals( IdleState.READER_IDLE ) ) {

				// log
				logger.info( "The channel is idle, closing the channel..." );

				// 关闭通道
				ctx.channel().close();
			}
		}

		super.userEventTriggered( ctx, evt );
	}

	@Override
	protected void channelRead0( ChannelHandlerContext ctx, String msg ) throws Exception {
		// log
		logger.debug( "Receive heartbeat message: {}.", msg );
	}
}
