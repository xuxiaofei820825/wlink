package com.iauto.wlink.server.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class StateStatisticsHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	public StateStatisticsHandler(  ) {
	}

	@Override
	public void channelInactive( ChannelHandlerContext ctx ) throws Exception {
	}

	@Override
	public void channelActive( ChannelHandlerContext ctx ) throws Exception {
		// info
		logger.info( "A channel is active. {}", ctx.channel() );
	}

	@Override
	public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause )
			throws Exception {
		// error log
		logger.error( "A error occured.", cause );
	}
}
