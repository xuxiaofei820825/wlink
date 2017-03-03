package com.iauto.wlink.client.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import com.iauto.wlink.client.CommunicationMessageListener;
import com.iauto.wlink.client.ConnectionListener;
import com.iauto.wlink.client.handler.HeartbeatHandler;
import com.iauto.wlink.core.comm.protocol.CommunicationMessageCodec;
import com.iauto.wlink.core.message.CommunicationMessage;

/**
 * 实现一个默认的客户端通道初始化器
 * 
 * @author xiaofei.xu
 * 
 */
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

	/** 通讯消息监听器 */
	private CommunicationMessageListener commMessageListener;

	/** 连接监听器 */
	private ConnectionListener connectionListener;

	@Override
	protected void initChannel( SocketChannel ch ) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		// 设置通讯包编解码器
		pipeline.addLast( "comm", new CommunicationMessageCodec() );

		// 心跳保活
		pipeline.addLast( new IdleStateHandler( 0, 0, 50, TimeUnit.SECONDS ) )
				.addLast( "heartbeat", new HeartbeatHandler() );

		// 消息处理
		pipeline.addLast( "message", new SimpleChannelInboundHandler<CommunicationMessage>() {
			@Override
			protected void channelRead0( ChannelHandlerContext ctx, CommunicationMessage msg ) throws Exception {

				// 通知消息监听器
				if ( commMessageListener != null )
					commMessageListener.onMessage( msg );
			}
		} );

		// 消息处理
		pipeline.addLast( "conn_monitor", new ChannelInboundHandlerAdapter() {
			@Override
			public void channelInactive( ChannelHandlerContext ctx ) throws Exception {

				// 通知连接监听器
				if ( connectionListener != null )
					connectionListener.onClosed();

				ctx.fireChannelInactive();
			}
			
			@Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	            throws Exception {
				//
				System.out.println("AAAAAAAAA");
				
	        ctx.fireExceptionCaught(cause);
	    }
		} );
	}

	// ============================================================================
	// setter/getter

	public void setCommMessageListener( CommunicationMessageListener commMessageListener ) {
		this.commMessageListener = commMessageListener;
	}

	public void setConnectionListener( ConnectionListener connectionListener ) {
		this.connectionListener = connectionListener;
	}
}
