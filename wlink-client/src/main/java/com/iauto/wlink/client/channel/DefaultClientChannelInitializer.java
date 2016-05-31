package com.iauto.wlink.client.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import com.iauto.wlink.client.channel.handler.HeartbeatHandler;
import com.iauto.wlink.core.comm.codec.CommunicationEncoder;
import com.iauto.wlink.core.message.codec.AuthMessageEncoder;

/**
 * 实现一个默认的客户端通道初始化器
 * 
 * @author xiaofei.xu
 * 
 */
public class DefaultClientChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel( SocketChannel ch ) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		// 心跳保活
		pipeline.addLast( new IdleStateHandler( 0, 6, 0, TimeUnit.SECONDS ) )
			.addLast( "heartbeat", new HeartbeatHandler() );

		// 设置通讯编码器
		pipeline.addLast( "comm", new CommunicationEncoder() );

		// 设置身份认证编码器
		pipeline.addLast( "auth", new AuthMessageEncoder() );
	}
}
