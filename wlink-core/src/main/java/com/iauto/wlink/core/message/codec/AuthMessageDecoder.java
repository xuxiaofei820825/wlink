package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.iauto.wlink.core.comm.proto.CommunicationHeaderProto.CommunicationHeader;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;
import com.iauto.wlink.core.message.worker.AuthWorker;
import com.iauto.wlink.core.session.SessionContext;

public class AuthMessageDecoder extends MessageToMessageDecoder<Object> {

	/** 当前会话上下文 */
	private SessionContext sessionContext;

	/** 业务线程池 */
	private static final ThreadPoolExecutor executor = new ThreadPoolExecutor( 5, 10, 30L, TimeUnit.SECONDS,
		new ArrayBlockingQueue<Runnable>( 100 ) );

	/**
	 * 报文解析的当前状态，初始化为INIT
	 */
	private State currentState = State.INIT;

	/**
	 * 解析状态
	 */
	private enum State {
		INIT,
		BODY,
		FINISHED
	}

	@Override
	protected void decode( ChannelHandlerContext ctx, Object msg, List<Object> out ) throws Exception {

		switch ( currentState ) {
		case INIT:
			if ( msg instanceof CommunicationHeader ) {
				// 如果是通讯头类型
				CommunicationHeader header = (CommunicationHeader) msg;
				if ( StringUtils.equals( "auth", header.getType() ) ) {
					currentState = State.BODY;
					break;
				}
			}

			// 否则，发送错误响应
		case BODY:
			if ( msg instanceof byte[] ) {
				// 否则，尝试解码认证信息

				AuthMessage authMsg = AuthMessage.parseFrom( (byte[]) msg );

				// 进行用户身份验证
				executor.execute( new AuthWorker( ctx, authMsg.getTicket() ) );

				break;
			}
		case FINISHED:
			ctx.fireChannelRead( msg );
			break;
		default:
			break;
		}
	}

	// ============================================================================
	// setter/getter

	public SessionContext getSessionContext() {
		return sessionContext;
	}
}
