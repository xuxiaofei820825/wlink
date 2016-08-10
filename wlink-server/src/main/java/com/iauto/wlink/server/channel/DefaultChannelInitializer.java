package com.iauto.wlink.server.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.auth.AuthenticationProvider;
import com.iauto.wlink.core.auth.DefaultTicketAuthMessageCodec;
import com.iauto.wlink.core.auth.provider.ReserveAccountTicketAuthenticationProvider;
import com.iauto.wlink.core.comm.codec.CommunicationPackageCodec;
import com.iauto.wlink.core.message.MessageRouter;
import com.iauto.wlink.core.message.QpidConnectionHandler;
import com.iauto.wlink.core.message.QpidDisconnectHandler;
import com.iauto.wlink.core.message.QpidMessageRouter;
import com.iauto.wlink.core.message.codec.CommMessageCodec;
import com.iauto.wlink.core.message.codec.ErrorMessageCodec;
import com.iauto.wlink.core.message.codec.MessageAcknowledgeCodec;
import com.iauto.wlink.core.session.HMacSessionSignatureHandler;
import com.iauto.wlink.core.session.SessionIdGenerator;
import com.iauto.wlink.core.session.SessionSignatureHandler;
import com.iauto.wlink.core.session.codec.SessionContextCodec;
import com.iauto.wlink.server.ApplicationSetting;
import com.iauto.wlink.server.ServerStateStatistics;
import com.iauto.wlink.server.auth.handler.AuthenticationHandler;
import com.iauto.wlink.server.channel.handler.HeartbeatHandler;
import com.iauto.wlink.server.channel.handler.StateStatisticsHandler;
import com.iauto.wlink.server.message.SendCommMessageWorker;
import com.iauto.wlink.server.session.SessionRebuildWorker;
import com.iauto.wlink.server.session.handler.SessionContextHandler;

public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 应用配置项 */
	private static final ApplicationSetting setting = ApplicationSetting.getInstance();

	/** SSL Context */
	private SslContext sslCtx;

	/** 服务器状态统计 */
	private static final ServerStateStatistics statistics = new ServerStateStatistics();

	private static final MessageRouter messageRouter = new QpidMessageRouter();
	private static final AuthenticationProvider provider = new ReserveAccountTicketAuthenticationProvider(
		"UhZr6vyeBu0KmlX9", "UTbKkKQ335whZicI" );
	private static final SessionSignatureHandler signHandler = new HMacSessionSignatureHandler( setting.getHmacKey() );

	public DefaultChannelInitializer() {
		this( null );
	}

	public DefaultChannelInitializer( final SslContext sslCtx ) {
		this.sslCtx = sslCtx;
	}

	@Override
	protected void initChannel( SocketChannel channel ) throws Exception {
		// log
		logger.info( "Initialing the channel......" );

		ChannelPipeline pipeline = channel.pipeline();

		// SSL
		if ( setting.isSSLEnabled() ) {
			if ( this.sslCtx == null ) {
				throw new IllegalArgumentException( "SSL context is required." );
			}
			pipeline.addLast( sslCtx.newHandler( channel.alloc() ) );
		}

		pipeline.addLast( "logger", new LoggingHandler( LogLevel.DEBUG ) );

		// 设置通讯包编解码器(进、出)
		pipeline.addLast( "comm_codec", new CommunicationPackageCodec() );

		// ===========================================================
		// 1.以下设置编码器

		// 设置错误响应编码器(出)
		pipeline.addLast( "error_encoder", new ErrorMessageCodec() );

		// 设置消息确认响应编码器(出)
		pipeline.addLast( "msg_send_ack_encoder", new MessageAcknowledgeCodec() );

		// ===========================================================
		// 2.设置心跳检测处理器
		IdleStateHandler idleStateHandler = new IdleStateHandler(
			setting.getHeartbeatInterval(), 0, 0, TimeUnit.SECONDS );
		pipeline.addLast( "idle", idleStateHandler )
			.addLast( "heartbeat", new HeartbeatHandler() );

		// 创建QPID连接
		pipeline.addLast( "qpid_connect", new QpidConnectionHandler( setting.getMqUrl() ) );
		pipeline.addLast( "qpid_disconnect", new QpidDisconnectHandler() );

		// ===========================================================
		// 3.以下设置解码器

		// 设置会话上下文解码器(进、出)
		SessionContextCodec sessionCodec = new SessionContextCodec();
		sessionCodec.setWorker( new SessionRebuildWorker( signHandler ) );
		pipeline.addLast( "session_codec", sessionCodec );

		// 处理用户身份认证
		AuthenticationHandler authHandler = new AuthenticationHandler( provider, new SessionIdGenerator() {
			public String generate() {
				return UUID.randomUUID().toString().replace( "-", "" );
			}
		} );
		authHandler.setMessageCodec( new DefaultTicketAuthMessageCodec() );
		authHandler.setSignHandler( signHandler );

		pipeline.addLast( "auth", authHandler );

		// 设置消息编解码器(进、出)
		pipeline.addLast( "message_codec", new CommMessageCodec( new SendCommMessageWorker( messageRouter ) ) );

		// 会话处理(建立会话，保存会话上下文等等)
		pipeline.addLast( "session_handler", new SessionContextHandler( messageRouter ) );

		// ===========================================================
		// 4.设置服务器监控处理器
		pipeline.addLast( new StateStatisticsHandler( statistics ) );
	}
}
