package com.iauto.wlink.server.channel.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
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

		// debug log
		logger.debug( "Type of communication package: {}", msg.getType() );

		TerminalMessage message = codec.decode( msg.getPayload() );
		ListenableFuture<?> future = messageRouter.send( message.type(), message.from(), message.to(), message.payload() );

		// 注册回调
		Futures.addCallback( future, new FutureCallback<Object>() {
			public void onSuccess( Object result ) {
				// info log
				logger.info( "Succeed to send message." );
			}
			public void onFailure( Throwable t ) {
				// info log
				logger.info( "Failed to send message." );
			}
		} );
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
