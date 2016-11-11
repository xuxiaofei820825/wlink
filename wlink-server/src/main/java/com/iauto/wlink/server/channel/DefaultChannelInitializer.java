package com.iauto.wlink.server.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.iauto.wlink.server.ServerStateStatistics;
import com.iauto.wlink.server.channel.handler.AuthenticationHandler;
import com.iauto.wlink.server.channel.handler.SessionManagementHandler;
import com.iauto.wlink.server.channel.handler.HeartbeatHandler;
import com.iauto.wlink.server.channel.handler.StateStatisticsHandler;
import com.iauto.wlink.server.channel.handler.TerminalMessageHandler;
import com.iauto.wlink.server.codec.CommunicationPayloadCodec;

public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> implements InitializingBean {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** SSL Context */
	private SslContext sslCtx;

	/** 服务器状态统计 */
	private static final ServerStateStatistics statistics = new ServerStateStatistics();

	/** 认证处理器 */
	private AuthenticationHandler authHandler;

	private SessionManagementHandler sessionManagementHandler;

	private TerminalMessageHandler terminalMessageHandler;

	/** SSL相关配置 */
	private boolean isSSLEnabled = false;
	private String crtFileName;
	private String pkFileName;
	private String keyPassword;

	/** 心跳保活间隔(默认30秒) */
	private int heartbeatInterval = 30000;

	public void afterPropertiesSet() throws Exception {
		Assert.notNull( this.authHandler, "Terminal Authentication handler is required." );
		Assert.notNull( this.terminalMessageHandler, "Terminal message handler is required." );
		Assert.notNull( this.sessionManagementHandler, "Session management handler is required." );

		if ( isSSLEnabled ) {
			// 加载证书和密匙文件
			URL crtFileUrl = this.getClass().getClassLoader().getResource( crtFileName );
			URL keyFileUrl = this.getClass().getClassLoader().getResource( pkFileName );

			if ( crtFileUrl == null )
				// log
				logger.warn( "Failed to load certificate file." );

			if ( keyFileUrl == null )
				// log
				logger.warn( "Failed to load key file." );

			sslCtx = SslContextBuilder.forServer(
				new File( crtFileUrl.toURI() ), new File( keyFileUrl.toURI() ), keyPassword ).build();
		}
	}

	public DefaultChannelInitializer() {
		sessionManagementHandler = new SessionManagementHandler();
	}

	@Override
	protected void initChannel( SocketChannel channel ) throws Exception {
		// log
		logger.info( "Initialing the channel......" );

		ChannelPipeline pipeline = channel.pipeline();

		// SSL
		if ( isSSLEnabled ) {
			if ( this.sslCtx == null ) {
				throw new IllegalArgumentException( "SSL context is required." );
			}
			pipeline.addLast( sslCtx.newHandler( channel.alloc() ) );
		}

		// 设置日志级别
		pipeline.addLast( "logger", new LoggingHandler( LogLevel.DEBUG ) );

		// 设置通讯包编/解码器(进、出)
		pipeline.addLast( "comm_codec", new CommunicationPayloadCodec() );

		// ===========================================================
		// 2.设置心跳检测处理器
		IdleStateHandler idleStateHandler = new IdleStateHandler(
			heartbeatInterval, 0, 0, TimeUnit.SECONDS );
		pipeline.addLast( "idle", idleStateHandler )
			.addLast( "heartbeat", new HeartbeatHandler() );

		// ===========================================================
		// 1.以下设置编码器

		// 处理终端身份认证
		pipeline.addLast( "auth", authHandler );

		// 通道对应表处理器
		pipeline.addLast( "channel_table_management", sessionManagementHandler );

		// 设置消息编解码器(进、出)
		// pipeline.addLast( "message_codec", new CommMessageCodec( new SendCommMessageWorker( messageRouter ) ) );

		// 会话处理(建立会话，保存会话上下文等等)
		// pipeline.addLast( "session_handler", new SessionContextHandler( messageRouter ) );

		// 终端消息处理
		pipeline.addLast( "terminal_message_handler", terminalMessageHandler );

		// ===========================================================
		// 4.设置服务器监控处理器
		pipeline.addLast( new StateStatisticsHandler( statistics ) );
	}

	// ===========================================================================
	// setter/getter

	public void setAuthHandler( AuthenticationHandler authHandler ) {
		this.authHandler = authHandler;
	}

	public void setSessionManagementHandler( SessionManagementHandler sessionManagementHandler ) {
		this.sessionManagementHandler = sessionManagementHandler;
	}

	public void setHeartbeatInterval( int heartbeatInterval ) {
		this.heartbeatInterval = heartbeatInterval;
	}

	public void setSSLEnabled( boolean isSSLEnabled ) {
		this.isSSLEnabled = isSSLEnabled;
	}

	public void setTerminalMessageHandler( TerminalMessageHandler terminalMessageHandler ) {
		this.terminalMessageHandler = terminalMessageHandler;
	}
}
