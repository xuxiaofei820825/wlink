package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.CommMessage;
import com.iauto.wlink.core.message.proto.CommMessageHeaderProto.CommMessageHeader;
import com.iauto.wlink.core.message.worker.MessageWorker;

public class CommMessageCodec extends MessageToMessageCodec<CommunicationPackage, CommMessage> {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 消息处理器 */
	private final MessageWorker worker;

	public CommMessageCodec( MessageWorker worker ) {
		this.worker = worker;
	}

	@Override
	protected void encode( ChannelHandlerContext ctx, CommMessage msg, List<Object> out ) throws Exception {

		CommMessageHeader commMsgHeader = CommMessageHeader.newBuilder()
			.setFrom( msg.getFrom() )
			.setTo( msg.getTo() )
			.setType( msg.getType() )
			.build();

		byte[] header = commMsgHeader.toByteArray();

		CommunicationPackage comm = new CommunicationPackage();
		comm.setType( "message" );
		comm.setHeader( header );
		comm.setBody( msg.getBody() );

		// 传递到下一个处理器
		out.add( comm );
	}

	@Override
	protected void decode( ChannelHandlerContext ctx, CommunicationPackage msg, List<Object> out ) throws Exception {
		// 获取数据包类型
		String type = msg.getType();

		// 如果不是文本消息，则流转到下一个处理器
		if ( !StringUtils.equals( "message", type ) ) {
			out.add( msg );
			return;
		}

		// log
		logger.info( "Processing the message......" );

		// process
		if ( this.worker != null )
			worker.process( ctx, msg.getHeader(), msg.getBody() );
	}
}
