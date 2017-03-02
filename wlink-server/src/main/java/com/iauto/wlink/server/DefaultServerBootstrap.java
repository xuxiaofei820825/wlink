package com.iauto.wlink.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class DefaultServerBootstrap implements InitializingBean {

	/** logger */
	private static final Logger logger = LoggerFactory.getLogger( DefaultServerBootstrap.class );

	/** Acceptor Reactor */
	private final EventLoopGroup bossGroup;

	/** Event Reactor */
	private final EventLoopGroup workerGroup;

	/** 服务监听端口 */
	private int port = 2391;

	/** 通道初始化器 */
	private ChannelInitializer<SocketChannel> channelInitializer;

	public void afterPropertiesSet() throws Exception {
		Assert.notNull( channelInitializer );
	}

	public DefaultServerBootstrap() {
		this.bossGroup = new NioEventLoopGroup();
		this.workerGroup = new NioEventLoopGroup();
	}

	public DefaultServerBootstrap( int bossthread, int workerthread ) {
		this.bossGroup = new NioEventLoopGroup( bossthread );
		this.workerGroup = new NioEventLoopGroup( workerthread );
	}

	public void start() throws Exception {
		// log
		logger.info( "Starting the wlink server......" );

		try {

			ServerBootstrap b = new ServerBootstrap();
			b.group( bossGroup, workerGroup )
					.channel( NioServerSocketChannel.class )
					.handler( new LoggingHandler( LogLevel.INFO ) )
					.childHandler( channelInitializer );

			// 绑定监听端口
			ChannelFuture future = b.bind( port ).sync();
			future.addListener( new ChannelFutureListener() {
				@Override
				public void operationComplete( ChannelFuture future ) throws Exception {
					if ( future.isSuccess() ) {
						// log
						logger.info( "Succeed to start wlink server. listening in port: {}", port );
					}
				}
			} );

			// 等待关闭
			future.channel()
					.closeFuture()
					.sync();
		}
		finally {
			// 关闭
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	// ====================================================================
	// setter/getter

	public void setPort( int port ) {
		this.port = port;
	}

	public void setChannelInitializer( ChannelInitializer<SocketChannel> channelInitializer ) {
		this.channelInitializer = channelInitializer;
	}
}
