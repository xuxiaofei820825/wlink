package com.iauto.wlink.core.comm.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.proto.CommunicationHeaderProto.CommunicationHeader;

public class CommunicationDecoder extends ByteToMessageDecoder {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/**
	 * 报文解析的当前状态，初始化为INIT
	 */
	private State currentState = State.INIT;

	/** 实体头长度 */
	private int headerLen = 0;

	/** 实体内容长度 */
	private int bodyLen = 0;

	/** 通讯头 */
	private CommunicationHeader header;

	/**
	 * 报文解析状态
	 */
	private enum State {
		INIT,
		HEADER,
		BODY
	}

	@Override
	protected void decode( ChannelHandlerContext ctx, ByteBuf in, List<Object> out ) throws Exception {

		// ================================================================
		// 数据包为以下结构：
		// ------------------------------------------------------
		// | 描述通讯头长度(2个字节) | 通讯头(protoBuffer编码) | 通讯体 |
		// ------------------------------------------------------

		// 根据当前的解码状态做处理
		switch ( currentState ) {
		case INIT:

			// 如果ByteBuf中可读字节数 < 2，则等待下次通道可读时继续解析
			if ( in.readableBytes() < 2 )
				return;

			// 获取通讯头长度
			headerLen = in.readShort();

			// debug
			logger.info( "RECV: [header: {} bytes]. {}",
				this.headerLen, ctx.channel() );

			// 否则状态迁移到解析Header
			currentState = State.HEADER;
		case HEADER:

			if ( headerLen == 0 ) {
				// 如果Header的长度为0，则状态迁移到解析Body
				currentState = State.BODY;
				return;
			}

			// 如果Buffer的可读字节数不够读取Header，等待下次通道可读时继续解析
			if ( in.readableBytes() < headerLen )
				return;

			// 读取Header
			CommunicationHeader header = CommunicationHeader.parseFrom( in.readBytes( headerLen ).array() );

			// debug
			logger.debug( "Content type: {}, length: {}", header.getType(), header.getContentLength() );

			// 通讯头读取结束
			out.add( this.header );

			// 状态迁移到解析Body
			currentState = State.BODY;
		case BODY:

			if ( bodyLen == 0 ) {
				// 如果Body的长度为0，
				// 则报文解析完毕，初始化状态，等待解析下一段报文
				init();
				return;
			}

			// 如果Buffer的可读字节数不够读取Body，等待下次通道可读时继续解析
			if ( in.readableBytes() < bodyLen )
				return;

			// 读取Body，并处理
			ByteBuf data = in.readBytes( bodyLen );

			// 通讯体读取结束
			out.add( data );

			// 报文解析完毕，初始化状态，等待解析下一段报文
			init();
		default:
			break;
		}
	}

	/**
	 * 初始化解码状态
	 */
	private void init() {
		currentState = State.INIT;
		headerLen = 0;
		bodyLen = 0;

		this.header = null;
	}
}
