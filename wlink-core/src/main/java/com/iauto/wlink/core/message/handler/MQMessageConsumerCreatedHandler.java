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

			// 保存用户会话与消息监听者的对应关系
			// 便于用户会话结束时，注销消息监听者
			SessionContextHandler.getConsumers().put( event.getSessionId(), event.getConsumer() );

			// info
			logger.info( "A consumer is created, save the map user to consumer. session:{}", event.getSessionId() );

			// 处理结束，退出
			return;
		}

		// 流转不能处理的事件
		super.userEventTriggered( ctx, evt );
	}
}
