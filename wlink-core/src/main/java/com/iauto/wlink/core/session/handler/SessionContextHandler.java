package com.iauto.wlink.core.session.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.jms.Connection;
import javax.jms.MessageConsumer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.auth.handler.AuthenticationHandler;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.mq.event.MQConnectionCreatedEvent;
import com.iauto.wlink.core.mq.router.MessageReceiver;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionContext;
import com.iauto.wlink.core.session.SessionContextManager;
import com.iauto.wlink.core.session.event.SessionContextEvent;

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

	/** 与MQ服务端的会话(每个IO线程创建一个连接，每个连接创建一个会话) */
	private final static ThreadLocal<Connection> connections = new ThreadLocal<Connection>();

	/** 消息接收者 */
	private final MessageReceiver receiver;

	public SessionContextHandler( MessageReceiver receiver ) {
		this.receiver = receiver;
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

		// 创建与MQ服务的会话
		createMQConnection( ctx, sessionCtx );
	}

	private void createMQConnection( final ChannelHandlerContext ctx, SessionContext sessionCtx ) {

		// 获取当前I/O线程的连接
		Connection mqConn = SessionContextHandler.getConnection();

		if ( mqConn == null ) {
			// 如果当前IO线程的连接还未创建，则为当前IO线程创建一个

			try {

				// info
				logger.info( "Creating MQ-Connection for current I/O thread......" );

				// 创建MQ服务连接
				Connection conn = receiver.newConnection( ctx );

				// 触发MQ连接创建成功的事件
				ctx.fireUserEventTriggered( new MQConnectionCreatedEvent( conn, sessionCtx ) );
			} catch ( Exception e ) {
				// error
				logger.info( "Failed to create a session for current thread! Caused by: {}", e.getMessage() );

				// 返回一个错误响应
				ErrorMessage error = ErrorMessage.newBuilder()
					.setError( "Session_Create_Failure" )
					.build();
				sessionCtx.getChannel().writeAndFlush( error );
			}
		} else {

			// 直接发送会话已经创建的事件
			ctx.fireUserEventTriggered( new MQConnectionCreatedEvent( mqConn, sessionCtx ) );
		}
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

			// 关闭消息监听器
			MessageConsumer consumer = sessionContext.getConsumer();
			if ( consumer != null ) {
				// close
				consumer.close();
				// log
				logger.info( "Succeed to close message consumer for user[ID:{}]", userId );
			}

			// 删除当前管理的用户会话
			SessionContextManager.remove( sessionId );

			// log
			logger.info( "Succeed to remove session of user[ID:{}]", userId );
		}

		// 流转到下一个处理器
		ctx.fireChannelInactive();
	}

	// ================================================================================================================
	// setter/getter

	public static Connection getConnection() {
		return connections.get();
	}

	public static void addConnection( Connection conn ) {
		connections.set( conn );
	}
}
