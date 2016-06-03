package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage;
import com.iauto.wlink.core.message.worker.MessageWorker;

public class SessionContextCodec extends MessageToMessageCodec<CommunicationPackage, SessionMessage> {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 消息处理器 */
	private final MessageWorker worker;

	public SessionContextCodec( MessageWorker worker ) {
		this.worker = worker;
	}

	@Override
	protected void encode( ChannelHandlerContext ctx, SessionMessage msg, List<Object> out ) throws Exception {
		// 获取会话消息的ProtoBuffer编码
		byte[] sessionBytes = msg.toByteArray();

		CommunicationPackage comm = new CommunicationPackage();
		comm.setType( "session" );
		comm.setBody( sessionBytes );

		// 传递到下一个处理器
		out.add( comm );
	}

	@Override
	protected void decode( ChannelHandlerContext ctx, CommunicationPackage msg, List<Object> out ) throws Exception {
		// 获取数据包类型
		String type = msg.getType();

		// 如果不是文本消息，则流转到下一个处理器
		if ( !StringUtils.equals( "session", type ) ) {
			out.add( msg );
			return;
		}

		// log
		logger.info( "Processing the session message......" );

		worker.process( ctx, msg.getBody() );
	}
}
