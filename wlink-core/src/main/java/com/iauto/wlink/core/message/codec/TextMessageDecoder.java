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
import com.iauto.wlink.core.message.proto.TextMessageProto.TextMessage;

public class TextMessageDecoder extends MessageToMessageDecoder<CommunicationPackage> {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 业务线程池 */
	private static final ThreadPoolExecutor executor = new ThreadPoolExecutor( 5, 10, 30L, TimeUnit.SECONDS,
		new ArrayBlockingQueue<Runnable>( 100 ) );

//	/**
//	 * 报文解析的当前状态，初始化为INIT
//	 */
//	private State currentState = State.INIT;
//
//	/**
//	 * 解析状态
//	 */
//	private enum State {
//		INIT,
//		BODY
//	}

	@Override
	protected void decode( ChannelHandlerContext ctx, CommunicationPackage msg, List<Object> out ) throws Exception {

		// 获取数据包类型
		String type = msg.getType();

		// 如果不是文本消息，则流转到下一个处理器
		if ( !StringUtils.equals( "text", type ) ) {
			out.add( msg );
			return;
		}

		// log
		logger.info( "Decoding the text message......" );

		TextMessage txtMsg = TextMessage.parseFrom( msg.getBody() );

//		switch ( currentState ) {
//		case INIT:
//			// 初始状态
//
//			// 期望读取一个通讯头
//			if ( !( msg instanceof CommunicationHeader ) ) {
//				out.add( msg );
//				break;
//			}
//
//			CommunicationHeader header = (CommunicationHeader) msg;
//			if ( StringUtils.equals( "text", header.getType() ) ) {
//				// log
//				logger.info( "The package is a text message, prepare to decode the message body." );
//
//				this.currentState = State.BODY;
//			} else {
//				// 传递到下一个处理器
//				out.add( msg );
//			}
//			break;
//		case BODY:
//			// 解码消息体
//
//			if ( msg instanceof ByteBuf ) {
//				// log
//				logger.info( "Decoding the message body......" );
//
//				ByteBuf body = (ByteBuf) msg;
//				TextMessage txtMsg = TextMessage.parseFrom( body.array() );
//
//				// 恢复为初始化状态
//				this.currentState = State.INIT;
//			}
//			break;
//		default:
//			break;
//		}
	}
}
