package com.iauto.wlink.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.message.CommunicationMessage;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		// 判定是否为写空闲
		// 如果是写空闲，则立即发送一条心跳消息维持TCP连接
		if ( evt instanceof IdleStateEvent ) {
			IdleStateEvent e = (IdleStateEvent) evt;

			if ( e.state() == IdleState.ALL_IDLE ) {

				// debug
				logger.info( "Channel is idle, send a hearbeat message." );

				CommunicationMessage comm = new CommunicationMessage();
				comm.setType( MessageType.Heartbeat );
				comm.setPayload( new byte[] {} );

				// 发送一个无消息体的数据包
				// 这里必须调用writeAndFlush方法，立即发送一条消息
				ctx.channel().writeAndFlush( comm );
			}
		}
	}
}
