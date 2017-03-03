package com.iauto.wlink.server.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.NioHandler;
import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.message.MessageListener;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionIdGenerator;
import com.iauto.wlink.core.session.SessionManager;
import com.iauto.wlink.core.session.UUIDSessionIdGenerator;
import com.iauto.wlink.server.NettySession;

/**
 * 使用NETTY实现需要向核心提供的功能。<br>
 * 
 * <ul>
 * <li>给核心发送终端连接状态的通知</li>
 * <li>给核心发送通讯消息到达的通知</li>
 * </ul>
 * 
 * @author xiaofei.xu
 * 
 */
@Sharable
public class NettyNioHandler extends SimpleChannelInboundHandler<CommunicationMessage> implements NioHandler {

	/** logger */
	private final static Logger logger = LoggerFactory.getLogger( NettyNioHandler.class );

	/** 会话ID生成器 */
	private SessionIdGenerator sessionIdGenerator = new UUIDSessionIdGenerator();

	/** 会话超时时间(默认60 days) */
	private int expiredDays = 60;

	/** 会话管理器 */
	private SessionManager sessionManager;

	/** 消息监听器 */
	private MessageListener messageListener;

	@Override
	public void channelActive( ChannelHandlerContext ctx ) throws Exception {

		// 创建会话
		Session session = new NettySession( ctx.channel() );
		String id = sessionIdGenerator.generate();
		session.setId( id );

		// 设置过期时间
		long expiredTime = System.currentTimeMillis() + expiredDays * 24 * 60 * 60 * 1000;
		session.setExpiredTime( expiredTime );

		// 保存在Channel的属性中
		ctx.channel().attr( NettySession.SessionKey ).set( session );

	}

	public void channelInactive( ChannelHandlerContext ctx ) throws Exception {
		// 获取保存的Session对象
		Session session = ctx.channel().attr( NettySession.SessionKey ).get();

		if ( session != null ) {
			final String tuid = session.getUid();
			final String id = session.getId();

			// 删除Session
			sessionManager.remove( tuid, id );
		}
	}

	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt )
			throws Exception {

		// 指定时间间隔内未收到心跳消息时，关闭连接。
		if ( evt instanceof IdleStateEvent ) {
			// 判断当前事件是否为IdleStateEvent

			IdleStateEvent event = (IdleStateEvent) evt;

			// 通道(Channel)上读空闲，终端未发送心跳包维持连接，或者可能物理链路已断开
			if ( event.state().equals( IdleState.READER_IDLE ) ) {

				Session session = ctx.channel().attr( NettySession.SessionKey ).get();
				ctx.channel().attr( NettySession.SessionKey ).set( null );

				final String tuid = session.getUid();
				final String id = session.getId();

				if ( this.sessionManager != null ) {
					// 删除会话
					sessionManager.remove( tuid, id );
				}

				// info log
				logger.info( "The channel{} is idle, closing the channel......", ctx.channel() );

				// 关闭通道(异步)
				ChannelFuture future = ctx.channel().close();

				// 设置回调函数
				future.addListener( new ChannelFutureListener() {
					@Override
					public void operationComplete( ChannelFuture future ) throws Exception {
						if ( future.isSuccess() ) {
							logger.info( "Succeed to close channel of terminal. tuid:{}, id:{}", tuid, id );
						}
					}
				} );
			}

			// 终止处理
			return;
		}

		// 转发事件
		super.userEventTriggered( ctx, evt );
	}

	@Override
	protected void channelRead0( ChannelHandlerContext ctx, CommunicationMessage message ) throws Exception {

		// info log
		logger.info( "a communication message received. type:{}", message.type() );

		// 如果是心跳消息，就不要再提交到后端去处理了
		if ( StringUtils.equals( MessageType.Heartbeat, message.type() ) ) {
			return;
		}

		// 通知消息监听器
		if ( messageListener != null ) {

			// 获取Channel上存储的会话
			Session session = ctx.channel().attr( NettySession.SessionKey ).get();

			messageListener.onMessage( session, message );
		}
	}

	// ============================================================================
	// setter/getter

	public void setSessionIdGenerator( SessionIdGenerator sessionIdGenerator ) {
		this.sessionIdGenerator = sessionIdGenerator;
	}

	@Override
	public void setSessionManager( SessionManager sessionManager ) {
		this.sessionManager = sessionManager;
	}

	@Override
	public void setMessageListener( MessageListener messageListener ) {
		this.messageListener = messageListener;
	}
}
