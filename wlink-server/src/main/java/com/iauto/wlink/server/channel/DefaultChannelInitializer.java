package com.iauto.wlink.server.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.codec.CommunicationDecoder;
import com.iauto.wlink.server.AppConfig;
import com.iauto.wlink.server.ServerStateStatistics;
import com.iauto.wlink.server.channel.handler.HeartbeatHandler;
import com.iauto.wlink.server.channel.handler.StateStatisticsHandler;

public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 应用配置 */
	private final AppConfig config;

	/** SSL Context */
	private SslContext sslCtx;

	/** 服务器状态统计 */
	private ServerStateStatistics statistics;

	public DefaultChannelInitializer( SslContext sslCtx, AppConfig config ) {
		this.config = config;
		this.sslCtx = sslCtx;
	}

	public DefaultChannelInitializer( AppConfig config ) {
		this.config = config;
	}

	@Override
	protected void initChannel( SocketChannel channel ) throws Exception {
		// log
		logger.info( "Initialing the channel......" );

		ChannelPipeline pipeline = channel.pipeline();

		// SSL
		if ( config.isSSLUsed() ) {
			if ( this.sslCtx == null ) {
				throw new IllegalArgumentException( "SSL context is required." );
			}
			pipeline.addLast( sslCtx.newHandler( channel.alloc() ) );
		}

		// 设置心跳检测
		IdleStateHandler idleStateHandler = new IdleStateHandler(
			config.getHeartbeatInterval(), 0, 0, TimeUnit.SECONDS );
		pipeline.addLast( "idle", idleStateHandler )
			.addLast( "heartbeat", new HeartbeatHandler() );

		// 设置通讯包解码器
		pipeline.addLast( "comm", new CommunicationDecoder() );

		// 设置服务器监控
		pipeline.addLast( new StateStatisticsHandler( statistics ) );
	}
}
