package com.iauto.wlink.client.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

	// logger
	private final static Logger logger = LoggerFactory.getLogger( HeartbeatHandler.class );

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		// 判定是否为写空闲
		// 如果是写空闲，则立即发送一条心跳消息维持TCP连接
		if ( evt instanceof IdleStateEvent ) {
			IdleStateEvent e = (IdleStateEvent) evt;

			if ( e.state() == IdleState.WRITER_IDLE ) {

				// debug
				logger.info( "Channel is idle, send a hearbeat message." );

				CommunicationPackage comm = new CommunicationPackage();
				comm.setType( "heartbeat" );
				comm.setHeader( new byte[] {} );
				comm.setBody( new byte[] {} );

				// 发送一个无消息体的数据包
				// 这里必须调用writeAndFlush方法，立即发送一条消息
				ctx.channel().writeAndFlush( comm );
			}
		}
	}
}
