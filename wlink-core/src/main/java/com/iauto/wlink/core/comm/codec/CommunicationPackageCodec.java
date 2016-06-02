package com.iauto.wlink.core.comm.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.comm.proto.CommunicationHeaderProto.CommunicationHeader;

public class CommunicationPackageCodec extends ByteToMessageCodec<CommunicationPackage> {

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

	/** 通讯包 */
	private CommunicationPackage commPackage;

	/**
	 * 报文解析状态
	 */
	private enum State {
		INIT,
		HEADER,
		BODY
	}

	@Override
	protected void encode( ChannelHandlerContext ctx, CommunicationPackage msg, ByteBuf out ) throws Exception {

		// 创建消息头
		CommunicationHeader header = CommunicationHeader.newBuilder()
			.setType( msg.getType() )
			.setContentLength( msg.getBody().length )
			.build();

		// log
		logger.info( "Encoding a message package. [type: {}, content-length: {}]",
			msg.getType(), msg.getBody().length );

		byte[] header_bytes = header.toByteArray();

		// 输出消息头的长度
		out.writeShort( header_bytes.length );

		// 输出消息头
		out.writeBytes( header_bytes );

		// 输出消息体
		out.writeBytes( msg.getBody() );
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

			// info
			logger.info( "Channel:{}, Receive a package:[Content-Type: {}, Content-Length: {}]",
				ctx.channel(), header.getType(), header.getContentLength() );

			// 获取内容长度
			this.bodyLen = header.getContentLength();

			// 创建通讯包实例
			this.commPackage = new CommunicationPackage();
			this.commPackage.setType( header.getType() );

			// 通讯头读取结束
			// 状态迁移到解析Body
			currentState = State.BODY;
		case BODY:

			if ( bodyLen != 0 ) {
				// 如果Buffer的可读字节数不够读取Body，等待下次通道可读时继续解析
				if ( in.readableBytes() < bodyLen )
					return;

				// 读取消息体
				byte[] body = new byte[bodyLen];
				in.readBytes( body );

				this.commPackage.setBody( body );
			}

			// 通讯体读取结束
			out.add( this.commPackage );

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
		// 恢复初始状态
		this.currentState = State.INIT;

		this.headerLen = 0;
		this.bodyLen = 0;
	}
}
