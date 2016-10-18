package com.iauto.wlink.server.channel.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.server.Constant;
import com.iauto.wlink.server.channel.ChannelTableManager;

@Sharable
public class ChannelTableManagementHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelInactive( ChannelHandlerContext ctx ) throws Exception {

		// 判断终端会话是否已经建立
		Session session = ctx.channel().attr( Constant.SessionKey ).get();

		if ( session != null ) {
			String sessionId = session.getId();
			String uuid = session.getUuId();
			ChannelTableManager.remove( uuid, sessionId );
		}

		ctx.fireChannelInactive();
	}
}
