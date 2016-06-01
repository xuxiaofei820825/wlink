package com.iauto.wlink.core.message.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.comm.proto.CommunicationHeaderProto.CommunicationHeader;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;
import com.iauto.wlink.core.message.worker.AuthWorker;
import com.iauto.wlink.core.session.SessionContext;

public class AuthMessageDecoder extends MessageToMessageDecoder<CommunicationPackage> {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 当前会话上下文 */
	private SessionContext sessionContext;

	/** 业务线程池 */
	private static final ThreadPoolExecutor executor = new ThreadPoolExecutor( 5, 10, 30L, TimeUnit.SECONDS,
		new ArrayBlockingQueue<Runnable>( 100 ) );

	/**
	 * 报文解析的当前状态，初始化为INIT
	 */
	// private State currentState = State.INIT;

	/**
	 * 解析状态
	 */
	private enum State {
		INIT,
		BODY,
		FINISHED
	}

	@Override
	protected void decode( ChannelHandlerContext ctx, CommunicationPackage msg, List<Object> out ) throws Exception {

		if ( this.sessionContext != null ) {
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
			// log
			logger.info( "Session has not been created, ignore the message." );

			// TODO 否则，发送错误响应
			return;
		}

		int length = msg.getLength();
		ByteBuf body = msg.getBody();

		// 否则，尝试解码认证信息
		AuthMessage authMsg = AuthMessage.parseFrom( body.readBytes( length ).array() );
		// 需要释放
		body.release();

		// log
		logger.info( "Channel: {}, Ticket: {}", ctx.channel(), authMsg.getTicket() );

		// 进行用户身份验证
		executor.execute( new AuthWorker( ctx, authMsg.getTicket() ) );

// switch ( currentState ) {
// case INIT:
// if ( msg instanceof CommunicationHeader ) {
// // 如果是通讯头类型
// CommunicationHeader header = (CommunicationHeader) msg;
//
// if ( StringUtils.equals( "auth", header.getType() ) ) {
// // log
// logger.info( "The package is an authentication message, prepare to decode the message." );
//
// currentState = State.BODY;
// break;
// }
// }
//
// break;
// case BODY:
//
// if ( msg instanceof ByteBuf ) {
//
// ByteBuf body = (ByteBuf) msg;
//
// // 否则，尝试解码认证信息
// AuthMessage authMsg = AuthMessage.parseFrom( body.array() );
//
// // log
// logger.info( "Channel: {}, Ticket: {}", ctx.channel(), authMsg.getTicket() );
//
// // 进行用户身份验证
// executor.execute( new AuthWorker( ctx, authMsg.getTicket() ) );
//
// // 恢复初始状态
// currentState = State.INIT;
// }
//
// break;
// case FINISHED:
// // debug
// logger.debug( "Finished the authentication, so send the message to next handler. userId: {}",
// this.sessionContext.getUserId() );
//
// out.add( msg );
// break;
// default:
// break;
// }
	}

	public void finish( final SessionContext sessionContext ) {
		this.sessionContext = sessionContext;
	}

	// ============================================================================
	// setter/getter

	public SessionContext getSessionContext() {
		return sessionContext;
	}
}
