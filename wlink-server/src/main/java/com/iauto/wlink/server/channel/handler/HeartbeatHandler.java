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

		if ( evt instanceof IdleStateEvent ) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if ( event.state().equals( IdleState.READER_IDLE ) ) {
				// Channel读空闲

				// log
				logger.info( "Reader is idle, close the channel." );

				// 关闭Channel
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
