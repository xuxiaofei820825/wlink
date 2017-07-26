package com.iauto.wlink.server.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iauto.wlink.core.comm.protocol.CommunicationMessageCodec;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.server.handler.StateStatisticsHandler;

@Component
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> implements InitializingBean {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( DefaultChannelInitializer.class );

	/** SSL Context */
	private SslContext sslCtx;

	/** SSL相关配置 */
	private boolean isSSLEnabled = false; // true:使用SSL，false:不使用
	private String crtFileName; // 证书文件
	private String pkFileName; // 秘钥对文件
	private String keyPassword; // 秘钥对文件加密密码

	/** 心跳保活间隔(默认30秒) */
	private int heartbeatInterval = 60;

	@Autowired
	private SimpleChannelInboundHandler<CommunicationMessage> commMessageHandler;

	public void afterPropertiesSet() throws Exception {
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

		// 3.设置通讯包编/解码器(进、出)
		pipeline.addLast( "comm_codec", new CommunicationMessageCodec() );

		// 1.心跳检测处理器
		IdleStateHandler idleStateHandler = new IdleStateHandler(
				heartbeatInterval, 0, 0, TimeUnit.SECONDS );
		pipeline.addLast( "idle", idleStateHandler );

		// 2.设置消息处理器
		pipeline.addLast( "comm_message", commMessageHandler );

		// 3.设置服务器监控处理器
		pipeline.addLast( "statistics", new StateStatisticsHandler() );
	}

	// ===========================================================================
	// setter/getter

	public void setHeartbeatInterval( int heartbeatInterval ) {
		this.heartbeatInterval = heartbeatInterval;
	}

	public void setSSLEnabled( boolean isSSLEnabled ) {
		this.isSSLEnabled = isSSLEnabled;
	}

	public void setCommMessageHandler( SimpleChannelInboundHandler<CommunicationMessage> commMessageHandler ) {
		this.commMessageHandler = commMessageHandler;
	}

}
