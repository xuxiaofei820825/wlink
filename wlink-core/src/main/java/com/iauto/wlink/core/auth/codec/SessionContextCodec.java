package com.iauto.wlink.core.auth.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant;
import com.iauto.wlink.core.MessageWorker;
import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage;

/**
 * 编解码用户会话
 * 
 * @author xiaofei.xu
 * 
 */
public class SessionContextCodec extends MessageToMessageCodec<CommunicationPackage, SessionMessage> {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 消息处理器 */
	private final MessageWorker worker;

	public SessionContextCodec( MessageWorker worker ) {
		this.worker = worker;
	}

	// ==================================================================================
	// 编码

	@Override
	protected void encode( ChannelHandlerContext ctx, SessionMessage msg, List<Object> out ) throws Exception {
		// 获取会话消息的ProtoBuffer编码
		byte[] sessionBytes = msg.toByteArray();

		CommunicationPackage comm = new CommunicationPackage();
		comm.setType( Constant.MessageType.Session );
		comm.setBody( sessionBytes );

		// 传递到下一个处理器
		out.add( comm );
	}

	// ==================================================================================
	// 解码

	@Override
	protected void decode( ChannelHandlerContext ctx, CommunicationPackage msg, List<Object> out ) throws Exception {

		// 判断是否为会话消息，不是则流转
		if ( !StringUtils.equals( Constant.MessageType.Session, msg.getType() ) ) {
			out.add( msg );
			return;
		}

		// 如果是会话消息，则尝试重建会话上下文
		// log
		logger.info( "Processing the session message......" );

		if ( this.worker != null )
			worker.process( ctx, msg.getHeader(), msg.getBody() );

		// 返回
		return;
	}
}
