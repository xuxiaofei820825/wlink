package com.iauto.wlink.server.channel.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.iauto.wlink.core.Constant;
import com.iauto.wlink.core.comm.CommunicationPayload;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.TerminalMessage;
import com.iauto.wlink.core.message.TerminalMessageRouter;

@Sharable
public class TerminalMessageHandler extends SimpleChannelInboundHandler<CommunicationPayload> implements
		InitializingBean {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 终端消息路由器 */
	private TerminalMessageRouter messageRouter;

	/** 认证消息编解码器 */
	private MessageCodec<? extends TerminalMessage> codec;

	public void afterPropertiesSet() throws Exception {
		Assert.notNull( this.messageRouter, "Message router is required." );
		Assert.notNull( this.codec, "Terminal message codec is required." );
	}

	@Override
	protected void channelRead0( ChannelHandlerContext ctx, CommunicationPayload msg ) throws Exception {
		String type = msg.getType();
		if ( !StringUtils.equals( type, Constant.MessageType.Terminal ) ) {
			ctx.fireChannelRead( msg );
			return;
		}

		// info log
		logger.info( "Type of communication package: {}", msg.getType() );

		TerminalMessage message = codec.decode( msg.getPayload() );
		messageRouter.send( message.type(), message.from(), message.to(), message.payload() );
	}

	// ===========================================================================
	// setter/getter

	public void setMessageRouter( TerminalMessageRouter messageRouter ) {
		this.messageRouter = messageRouter;
	}

	public void setCodec( MessageCodec<? extends TerminalMessage> codec ) {
		this.codec = codec;
	}
}
