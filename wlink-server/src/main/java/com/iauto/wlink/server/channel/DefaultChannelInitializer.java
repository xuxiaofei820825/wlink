package com.iauto.wlink.server.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.codec.CommunicationPackageCodec;
import com.iauto.wlink.core.message.codec.CommMessageCodec;
import com.iauto.wlink.core.message.codec.ErrorMessageCodec;
import com.iauto.wlink.core.message.codec.MessageAcknowledgeCodec;
import com.iauto.wlink.core.message.codec.SessionContextCodec;
import com.iauto.wlink.core.message.handler.AuthenticationHandler;
import com.iauto.wlink.core.message.handler.MQConnectionCreatedHandler;
import com.iauto.wlink.core.message.handler.MQMessageConsumerCreatedHandler;
import com.iauto.wlink.core.message.handler.MQReconnectedHandler;
import com.iauto.wlink.core.message.handler.SessionContextHandler;
import com.iauto.wlink.core.message.router.MessageReceiver;
import com.iauto.wlink.core.message.router.MessageSender;
import com.iauto.wlink.core.message.router.QpidMessageReceiver;
import com.iauto.wlink.core.message.router.QpidMessageSender;
import com.iauto.wlink.core.message.worker.SendCommMessageWorker;
import com.iauto.wlink.server.ApplicationSetting;
import com.iauto.wlink.server.ServerStateStatistics;
import com.iauto.wlink.server.channel.handler.HeartbeatHandler;
import com.iauto.wlink.server.channel.handler.StateStatisticsHandler;

public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 应用配置项 */
	private static final ApplicationSetting setting = ApplicationSetting.getInstance();

	/** SSL Context */
	private SslContext sslCtx;

	/** 服务器状态统计 */
	private static final ServerStateStatistics statistics = new ServerStateStatistics();

	/** 消息队列组件 */
	private static final MessageSender msgSender = new QpidMessageSender( setting.getMqUrl() );
	private static final MessageReceiver msgReceiver = new QpidMessageReceiver( setting.getMqUrl() );

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

		// ===========================================================
		// 3.以下设置解码器

		// 设置会话上下文解码器(进、出)
		pipeline.addLast( "session_codec", new SessionContextCodec( null ) );

		// 处理用户身份认证
		pipeline.addLast( "auth", new AuthenticationHandler( "9aROHg2eQXQ6X3XKrXGKWjXrLiRIO25CKTyz212ujvc" ) );

		// 设置消息编解码器(进、出)
		pipeline.addLast( "message_codec", new CommMessageCodec( new SendCommMessageWorker( msgSender ) ) );

		// 会话处理(建立会话，保存会话上下文等等)
		pipeline.addLast( "session_handler", new SessionContextHandler( msgReceiver ) );
		pipeline.addLast( "mq_connection_created_handler", new MQConnectionCreatedHandler( msgReceiver ) );
		pipeline.addLast( "mq_reconnected_handler", new MQReconnectedHandler( msgReceiver ) );
		pipeline.addLast( "mq_consumer_created_handler", new MQMessageConsumerCreatedHandler() );

		// ===========================================================
		// 4.设置服务器监控处理器
		pipeline.addLast( new StateStatisticsHandler( statistics ) );
	}
}
