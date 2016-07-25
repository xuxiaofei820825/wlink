package com.iauto.wlink.core.mq.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.auth.SessionContext;
import com.iauto.wlink.core.auth.handler.SessionContextHandler;
import com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage;
import com.iauto.wlink.core.mq.event.MQMessageConsumerCreatedEvent;

/**
 * 进行消息监听器被创建后的处理
 * 
 * @author xiaofei.xu
 * 
 */
public class MQMessageConsumerCreatedHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 签名密匙 */
	private final String signKey;

	public MQMessageConsumerCreatedHandler( String signKey ) {
		this.signKey = signKey;
	}

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		if ( evt instanceof MQMessageConsumerCreatedEvent ) {
			// 处理消息监听者被创建的事件

			MQMessageConsumerCreatedEvent event = (MQMessageConsumerCreatedEvent) evt;
			SessionContext session = event.getSession();

			// 保存用户会话与消息监听者的对应关系，便于用户会话结束时，注销消息监听者
			SessionContextHandler.getConsumers().put( session.getId(), event.getConsumer() );

			// info
			logger.info( "Succeed to create a session for user[ID:{}]. session:{}, channel:{}",
				session.getUserId(), session.getId(), session.getChannel() );

			// 创建会话上下文对象，并返回给终端
			// 终端可使用会话上下文重新建立与服务器的会话
			long timestamp = System.currentTimeMillis();
			String signature = SessionContext.sign( signKey, session );
			SessionMessage sessionMsg = SessionMessage.newBuilder()
				.setId( session.getId() )
				.setUserId( session.getUserId() )
				.setTimestamp( timestamp )
				.setSignature( signature )
				.build();

			// 把签名后的会话上下文返回给终端
			session.getChannel().writeAndFlush( sessionMsg );

			// 处理结束，退出
			return;
		}

		// 流转其他不能处理的事件
		super.userEventTriggered( ctx, evt );
	}
}
