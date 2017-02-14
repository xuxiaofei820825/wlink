package com.iauto.wlink.server.channel.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.session.SessionManager;

public class HeartbeatHandler extends SimpleChannelInboundHandler<CommunicationMessage> {

	/** logger */
	private final static Logger logger = LoggerFactory.getLogger( HeartbeatHandler.class );

	/** 会话管理器 */
	private SessionManager sessionManager;

	public HeartbeatHandler() {
	}

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		// 判断当前事件是否为IdleStateEvent
		if ( evt instanceof IdleStateEvent ) {
			IdleStateEvent event = (IdleStateEvent) evt;

			// 通道(Channel)上读空闲，终端未发送心跳包维持连接，或者物理链路已经断开
			if ( event.state().equals( IdleState.READER_IDLE ) ) {

				// log
				logger.info( "The channel{} is idle, closing the channel......", ctx.channel() );

				// 关闭通道(同步执行)
				ChannelFuture future = ctx.channel().close().sync();

				// TODO 删除会话
				if ( future.isSuccess() ) {
					sessionManager.remove( "", "" );
				}
			}
		}

		super.userEventTriggered( ctx, evt );
	}

	@Override
	protected void channelRead0( ChannelHandlerContext ctx, CommunicationMessage msg ) throws Exception {
		if ( StringUtils.equals( MessageType.Heartbeat, msg.type() ) ) {
			// log
			logger.info( "Receive a heartbeat message! Channel:{}", ctx.channel() );
		} else {
			ctx.fireChannelRead( msg );
		}
	}
}
