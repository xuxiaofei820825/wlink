package com.iauto.wlink.server.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.server.ServerStateStatistics;

public class StateStatisticsHandler extends ChannelInboundHandlerAdapter {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 状态统计 */
	private ServerStateStatistics statistics;

	public StateStatisticsHandler( ServerStateStatistics statistics ) {
		this.statistics = statistics;
	}

	@Override
	public void channelInactive( ChannelHandlerContext ctx ) throws Exception {

		// 对当前线程管理的客户端进行计数
		Integer currentNum = statistics.getClientsOfCurrentThread().get();

		// 通道失效时，客户端计数减1
		if ( currentNum == null ) {
			currentNum = new Integer( 0 );
		} else {
			int cnt = currentNum.intValue();
			currentNum = new Integer( cnt - 1 );
		}

		statistics.getClientsOfCurrentThread().set( currentNum );

		// info
		logger.info( "A channel is closed. {}", ctx.channel() );
	}

	@Override
	public void channelActive( ChannelHandlerContext ctx ) throws Exception {
		// info
		logger.info( "A channel is active. {}", ctx.channel() );

		// 对当前线程管理的客户端进行计数
		Integer currentNum = statistics.getClientsOfCurrentThread().get();

		// 通道有效时，客户端计数加1
		if ( currentNum == null ) {
			currentNum = new Integer( 1 );
		} else {
			int cnt = currentNum.intValue();
			currentNum = new Integer( cnt + 1 );
		}

		statistics.getClientsOfCurrentThread().set( currentNum );
	}

	@Override
	public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
		// do noting
	}

	@Override
	public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause )
			throws Exception {
		logger.error( "A error occured!", cause );
	}
}
