package com.iauto.wlink.core.message.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.event.MQMessageConsumerCreatedEvent;

public class MQMessageConsumerCreatedHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		if ( evt instanceof MQMessageConsumerCreatedEvent ) {
			// 处理消息监听者被注册的事件

			MQMessageConsumerCreatedEvent event = (MQMessageConsumerCreatedEvent) evt;

			// info
			logger.info( "A consumer is created, save the map user to consumer.", event.getUserId() );

			// 保存用户与其消息监听者的对应关系
			SessionContextHandler.getUsers().get().put( event.getUserId(), event.getConsumer() );

			return;
		}

		// 流转不能处理的事件
		super.userEventTriggered( ctx, evt );
	}
}
