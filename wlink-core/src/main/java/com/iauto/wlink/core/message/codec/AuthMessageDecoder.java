package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;
import com.iauto.wlink.core.message.worker.AuthWorker;
import com.iauto.wlink.core.session.SessionContext;

public class AuthMessageDecoder extends MessageToMessageDecoder<CommunicationPackage> {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 当前会话上下文 */
	private SessionContext sessionContext;

	/** 表示是否已做身份认证 */
	private AtomicBoolean isAuthenticated = new AtomicBoolean( false );

	/** 业务线程池 */
	private static final ThreadPoolExecutor executor = new ThreadPoolExecutor( 5, 10, 30L, TimeUnit.SECONDS,
		new ArrayBlockingQueue<Runnable>( 100 ) );

	@Override
	protected void decode( ChannelHandlerContext ctx, CommunicationPackage msg, List<Object> out ) throws Exception {

		if ( isAuthenticated.get() ) {
			// 该通道已完成了用户身份验证

			// info
			logger.info( "Session has been created, so send the communication package to next handler. userId: {}",
				this.sessionContext.getUserId() );

			// 消息流转到下一个处理器
			out.add( msg );
			return;
		}

		// 获取消息类型
		String type = msg.getType();

		if ( !StringUtils.equals( "auth", type ) ) {
			// 如果不是认证消息，则忽略所有的通讯包
			// 并返回需要认证用户身份的消息

			// log
			logger.info( "Session has not been created, ignore the message." );

			// TODO 否则，发送错误响应
			return;
		}

		// 以下处理身份认证
		// log
		logger.info( "Decoding the authentication message......" );

		// 否则，尝试解码认证信息
		AuthMessage authMsg = AuthMessage.parseFrom( msg.getBody() );

		// log
		logger.info( "Channel: {}, Ticket: {}", ctx.channel(), authMsg.getTicket() );

		// 异步进行用户身份验证
		executor.execute( new AuthWorker( ctx, authMsg.getTicket() ) );
	}

	public void finish( final SessionContext sessionContext ) {
		this.isAuthenticated.set( true );
		this.sessionContext = sessionContext;
	}

	// ============================================================================
	// setter/getter

	public SessionContext getSessionContext() {
		return sessionContext;
	}
}
