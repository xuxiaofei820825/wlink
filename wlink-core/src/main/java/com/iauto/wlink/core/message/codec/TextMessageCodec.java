package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.proto.TextMessageProto.TextMessage;
import com.iauto.wlink.core.message.worker.TextMessageWorker;

public class TextMessageCodec extends MessageToMessageCodec<CommunicationPackage, TextMessage> {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 业务线程池 */
	private static final ThreadPoolExecutor executor = new ThreadPoolExecutor( 5, 10, 30L, TimeUnit.SECONDS,
		new ArrayBlockingQueue<Runnable>( 100 ) );

	@Override
	protected void encode( ChannelHandlerContext ctx, TextMessage msg, List<Object> out ) throws Exception {
		// 获取文本消息的ProtoBuffer编码
		byte[] txtMsgBytes = msg.toByteArray();

		CommunicationPackage comm = new CommunicationPackage();
		comm.setType( "text" );
		comm.setBody( txtMsgBytes );

		// 传递到下一个处理器
		out.add( comm );
	}

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

		// 解码消息体
		TextMessage txtMsg = TextMessage.parseFrom( msg.getBody() );

		// 异步执行
		executor.execute( new TextMessageWorker( ctx, txtMsg ) );
	}
}
