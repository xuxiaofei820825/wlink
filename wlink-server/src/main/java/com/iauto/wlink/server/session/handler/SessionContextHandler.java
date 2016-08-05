package com.iauto.wlink.server.session.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.MessageRouter;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionContext;
import com.iauto.wlink.core.session.SessionContextManager;
import com.iauto.wlink.server.auth.handler.AuthenticationHandler;
import com.iauto.wlink.server.session.event.SessionContextEvent;

/**
 * 完成用户会话创建后的处理 <br>
 * <ul>
 * <li>如果当前I/O线程没有创建MQ连接，则创建MQ连接
 * <li>为当前用户创建消息监听器
 * </ul>
 * 
 * @author xiaofei.xu
 * 
 */
public class SessionContextHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 消息路由实现 */
	private final MessageRouter messageRouter;

	public SessionContextHandler( MessageRouter messageRouter ) {
		this.messageRouter = messageRouter;
	}

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		// 流转不能处理的事件
		if ( !( evt instanceof SessionContextEvent ) ) {
			super.userEventTriggered( ctx, evt );
			return;
		}

		// 处理由认证处理器触发的建立会话上下文的事件
		// 获取会话上下文
		SessionContextEvent event = (SessionContextEvent) evt;
		SessionContext sessionCtx = event.getSessionContext();

		// 保存会话上下文到当前线程
		SessionContextManager.add( sessionCtx );

		// 为会话用户创建监听
		this.messageRouter.register( sessionCtx );
	}

	@Override
	public void channelInactive( ChannelHandlerContext ctx ) throws Exception {

		// 获取当前Channel的Session
		Session session = ctx.channel()
			.attr( AuthenticationHandler.SessionKey ).get();

		if ( session != null
				&& session.getUserId() > 0
				&& StringUtils.isNotBlank( session.getId() ) ) {

			long userId = session.getUserId();
			String sessionId = session.getId();

			// log
			logger.info( "User[{}] is offline, removing session......", userId );

			// 解绑消息监听器
			SessionContext sessionContext = SessionContextManager.get( sessionId );

			// 如果会话上下文不存在，则报错
			if ( sessionContext == null ) {
				throw new RuntimeException( String.format( "Session context of user[ID:%s] is not exist!", userId ) );
			}

			// 注销会话
			this.messageRouter.unregister( sessionContext );

			// 删除当前管理的用户会话
			SessionContextManager.remove( sessionId );

			// log
			logger.info( "Succeed to remove session of user[ID:{}]", userId );
		}

		// 流转到下一个处理器
		ctx.fireChannelInactive();
	}
}
