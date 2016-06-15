package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;
import com.iauto.wlink.core.message.worker.MessageWorker;

public class AuthenticationMessageCodec extends MessageToMessageCodec<CommunicationPackage, AuthMessage> {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

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

		// 如果不是认证消息，流转到下一个处理器
		if ( !StringUtils.equals( "auth", msg.getType() ) ) {
			out.add( msg );
			return;
		}

		// info
		logger.info( "Decoding the authentication message......" );

		// 以下处理身份认证
		if ( worker != null )
			worker.process( ctx, msg.getHeader(), msg.getBody() );
	}
}
