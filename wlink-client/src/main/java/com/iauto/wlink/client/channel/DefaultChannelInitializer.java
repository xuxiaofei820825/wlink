package com.iauto.wlink.client.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import com.iauto.wlink.client.channel.handler.HeartbeatHandler;
import com.iauto.wlink.core.MessageWorker;
import com.iauto.wlink.core.auth.codec.AuthenticationMessageEncoder;
import com.iauto.wlink.core.auth.codec.SessionContextCodec;
import com.iauto.wlink.core.comm.codec.CommunicationPackageCodec;
import com.iauto.wlink.core.message.codec.CommMessageCodec;
import com.iauto.wlink.core.message.codec.ErrorMessageCodec;
import com.iauto.wlink.core.message.codec.MessageAcknowledgeCodec;
import com.iauto.wlink.core.message.proto.CommMessageHeaderProto.CommMessageHeader;

/**
 * 实现一个默认的客户端通道初始化器
 * 
 * @author xiaofei.xu
 * 
 */
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel( SocketChannel ch ) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		// 设置通讯包编解码器
		pipeline.addLast( "comm", new CommunicationPackageCodec() );

		// ===========================================================================
		// 1.心跳保活
		pipeline.addLast( new IdleStateHandler( 0, 0, 50, TimeUnit.SECONDS ) )
			.addLast( "heartbeat", new HeartbeatHandler() );

		// ===========================================================================
		// 2.设置服务端响应的解码器

		// 设置错误响应解码器
		pipeline.addLast( "error", new ErrorMessageCodec() );

		// 设置会话响应解码器
		pipeline.addLast( "session", new SessionContextCodec() );

		// 设置消息确认响应解码器
		pipeline.addLast( "msg_send_ack_decoder", new MessageAcknowledgeCodec() );

		// ===========================================================================
		// 3.设置请求编码器

		// 设置消息推送请求编码器
		pipeline.addLast( "message", new CommMessageCodec( new MessageWorker() {

			public void process( ChannelHandlerContext ctx, byte[] header, byte[] body ) throws Exception {
				CommMessageHeader commHeader = CommMessageHeader.parseFrom( header );

				System.err.println( "Type: " + commHeader.getType() );
				System.err.println( "From: " + commHeader.getFrom() );
				System.err.println( "To: " + commHeader.getTo() );
				System.err.println( "Content: " + new String( body, "UTF-8" ) );
			}
		} ) );

		// 设置身份认证请求编码器
		pipeline.addLast( "auth", new AuthenticationMessageEncoder() );
		
		
	}
}
