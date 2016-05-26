package com.iauto.wlink.server.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateStatisticsHandler extends ChannelInboundHandlerAdapter {

	private ThreadLocal<Integer> number;

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	public StateStatisticsHandler( ThreadLocal<Integer> number ) {
		this.number = number;
	}

	@Override
	public void channelInactive( ChannelHandlerContext ctx ) throws Exception {

		// log
		logger.info( "Thread ID: {}", Thread.currentThread().getId() );
		
		Integer currentNum = number.get();
		if ( currentNum == null ) {
			currentNum = new Integer( 0 );
		} else {
			int cnt = currentNum.intValue();
			currentNum = new Integer( cnt - 1 );
		}
		
		number.set( currentNum );
		
		logger.info( "Thread ID: {}, Number of channel: {}", Thread.currentThread().getId(), currentNum );

		// info
		logger.info( "A channel is closed. {}", ctx.channel() );
	}

	@Override
	public void channelActive( ChannelHandlerContext ctx ) throws Exception {
		// info
		logger.info( "A channel is active. {}", ctx.channel() );

		Integer currentNum = number.get();
		if ( currentNum == null ) {
			currentNum = new Integer( 1 );
		} else {
			int cnt = currentNum.intValue();
			currentNum = new Integer( cnt + 1 );
		}

		number.set( currentNum );

		logger.info( "Thread ID: {}, Number of channel: {}", Thread.currentThread().getId(), currentNum );


	}
}
