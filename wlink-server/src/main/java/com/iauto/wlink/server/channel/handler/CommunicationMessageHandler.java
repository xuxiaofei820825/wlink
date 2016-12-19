package com.iauto.wlink.server.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.message.MessageListener;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.server.NettySession;

/**
 * 该类管理终端消息的监听者，构建会话对象。<br/>
 * 并在终端消息到达时，向所有的消息监听者转发消息到达的事件。
 * 
 * @author xiaofei.xu
 * 
 */
public class CommunicationMessageHandler extends SimpleChannelInboundHandler<CommunicationMessage>
		implements InitializingBean {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 消息监听器 */
	private MessageListener messageListener;

	public void afterPropertiesSet() throws Exception {
		Assert.notNull( this.messageListener, "Listener of message is required." );
	}

	@Override
	protected void channelRead0( ChannelHandlerContext ctx, CommunicationMessage message ) throws Exception {
		// log
		logger.info( "A communication message received! type:{}", message.type() );

		// 构建会话
		Session session = ctx.channel().attr( NettySession.SessionKey ).get();

		// 向监听者转发消息到达的事件
		messageListener.onMessage( session, message );
	}

	// ===========================================================================
	// setter/getter

	public void setMessageListener( MessageListener messageListener ) {
		this.messageListener = messageListener;
	}
}
