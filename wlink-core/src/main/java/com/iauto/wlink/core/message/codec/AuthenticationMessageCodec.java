package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.message.worker.MessageWorker;
import com.iauto.wlink.core.session.SessionContext;

public class AuthenticationMessageCodec extends MessageToMessageCodec<CommunicationPackage, AuthMessage> {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 当前会话上下文 */
	private SessionContext sessionContext;

	/** 表示是否已做身份认证 */
	private AtomicBoolean isAuthenticated = new AtomicBoolean( false );

	/** 消息处理器 */
	private final MessageWorker worker;

	public AuthenticationMessageCodec( MessageWorker worker ) {
		this.worker = worker;
	}

	@Override
	protected void encode( ChannelHandlerContext ctx, AuthMessage msg, List<Object> out ) throws Exception {
		// 获取认证消息的ProtoBuffer编码
		byte[] authMsgBytes = msg.toByteArray();

		CommunicationPackage comm = new CommunicationPackage();
		comm.setType( "auth" );
		comm.setHeader( new byte[] {} );
		comm.setBody( authMsgBytes );

		// 传递到下一个处理器
		out.add( comm );
	}

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

			// 否则，发送错误响应
			ErrorMessage error = ErrorMessage.newBuilder()
				.setError( "UNAUTHENTICATED" )
				.build();
			ctx.channel().writeAndFlush( error );

			return;
		}

		// 以下处理身份认证
		worker.process( ctx, msg.getHeader(), msg.getBody() );
	}

	public void finish( final SessionContext sessionContext ) {
		this.isAuthenticated.set( true );
		this.sessionContext = sessionContext;
	}
}
