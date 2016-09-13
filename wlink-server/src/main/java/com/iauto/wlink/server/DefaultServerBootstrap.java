package com.iauto.wlink.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.server.channel.DefaultChannelInitializer;

public class DefaultServerBootstrap {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 应用设置 */
	private final ApplicationSetting setting = ApplicationSetting.getInstance();

	/** Acceptor Reactor */
	private final EventLoopGroup bossGroup;

	/** Event Reactor */
	private final EventLoopGroup workerGroup;

	public DefaultServerBootstrap() {
		this.bossGroup = new NioEventLoopGroup();
		this.workerGroup = new NioEventLoopGroup(3);
	}

	public void start() throws Exception {
		// log
		logger.info( "Starting the wlink server......" );

		try {

			ChannelInitializer<SocketChannel> initializer = null;

			if ( setting.isSSLEnabled() ) {
				// 如果需要通信加密

				// log
				logger.info( "Setting the SSL context......" );

				// 加载证书和密匙文件
				URL crtFileUrl = this.getClass().getClassLoader().getResource( setting.getCrtFileName() );
				URL keyFileUrl = this.getClass().getClassLoader().getResource( setting.getPkFileName() );

				if ( crtFileUrl == null )
					// log
					logger.warn( "Failed to load certificate file." );

				if ( keyFileUrl == null )
					// log
					logger.warn( "Failed to load key file." );

				SslContext sslCtx = SslContextBuilder
					.forServer( new File( crtFileUrl.toURI() ), new File( keyFileUrl.toURI() ), setting.getKeyPassword() )
					.build();

				// log
				logger.info( "Succeed to set SSL context." );

				// 使用SSL协议进行通信加密
				initializer = new DefaultChannelInitializer( sslCtx );
			}
			else {
				initializer = new DefaultChannelInitializer();
			}

			ServerBootstrap b = new ServerBootstrap();
			b.group( bossGroup, workerGroup )
				.channel( NioServerSocketChannel.class )
				.handler( new LoggingHandler( LogLevel.INFO ) )
				.childHandler( initializer );

			// 绑定监听端口
			ChannelFuture future = b.bind( setting.getPort() ).sync();
			if ( future.isSuccess() ) {
				// log
				logger.info( "Succeed to start wlink server. listening in port: {}", setting.getPort() );
			}

			// 等待关闭
			future.channel()
				.closeFuture()
				.sync();
		} finally {
			// 关闭
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
