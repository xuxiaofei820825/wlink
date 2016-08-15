package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant;
import com.iauto.wlink.core.MessageWorker;
import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.AbstractPointToPointMessage;
import com.iauto.wlink.core.message.proto.CommMessageHeaderProto.CommMessageHeader;

/**
 * 通讯消息编解码器<br/>
 * 
 * <ul>
 * <li>编码:把CommMessage对象转化为CommunicationPackage对象
 * <li>解码:把CommunicationPackage对象转化为CommMessage对象
 * </ul>
 * 
 * @author xiaofei.xu
 * 
 */
public class CommMessageCodec extends MessageToMessageCodec<CommunicationPackage, AbstractPointToPointMessage<byte[]>> {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 消息处理器 */
	private final MessageWorker worker;

	public CommMessageCodec( MessageWorker worker ) {
		this.worker = worker;
	}

	@Override
	protected void encode( ChannelHandlerContext ctx, AbstractPointToPointMessage<byte[]> msg, List<Object> out )
			throws Exception {

		// 对消息头进行编码
		CommMessageHeader commMsgHeader = CommMessageHeader.newBuilder()
			.setFrom( String.valueOf( msg.from() ) )
			.setTo( String.valueOf( msg.to() ) )
			.setType( msg.type() )
			.build();
		byte[] header = commMsgHeader.toByteArray();

		CommunicationPackage commPkg = new CommunicationPackage();
		commPkg.setType( Constant.MessageType.Message );
		commPkg.setHeader( header );
		commPkg.setBody( msg.payload() );

		// 传递到下一个处理器
		out.add( commPkg );
	}

	@Override
	protected void decode( ChannelHandlerContext ctx, CommunicationPackage msg, List<Object> out ) throws Exception {

		// 如果不是文本消息，则流转到下一个处理器
		if ( !StringUtils.equals( Constant.MessageType.Message, msg.getType() ) ) {
			out.add( msg );
			return;
		}

		// debug
		logger.debug( "Receive a message. Channel:{}", ctx.channel() );

		// process
		if ( this.worker != null )
			worker.process( ctx, msg.getHeader(), msg.getBody() );
	}
}
