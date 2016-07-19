package com.iauto.wlink.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.client.channel.DefaultChannelInitializer;
import com.iauto.wlink.core.message.CommMessage;
import com.iauto.wlink.core.message.proto.AuthMessageProto.AuthMessage;
import com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage;

public class DefaultClient {

	// logger
	private final static Logger logger = LoggerFactory.getLogger( DefaultClient.class );

	/** 服务端地址 */
	private String host;

	/** 服务端端口号 */
	private int port;

	/** 当前连接通道 */
	private Channel channel;

	public DefaultClient( final String host, final int port ) {
		this.host = host;
		this.port = port;
	}

	public void connect() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();

		b.group( group )
			.channel( NioSocketChannel.class )
			.option( ChannelOption.SO_KEEPALIVE, true )
			.remoteAddress( this.host, this.port )
			.handler( new DefaultChannelInitializer() );

		// info
		logger.info( "Connecting to wlink server......" );

		ChannelFuture future = b.connect( host, port ).sync();
		if ( future.isSuccess() ) {
			// log
			logger.info( "Succeed to connect wlink server." );

			this.channel = future.channel();
		}
	}

	public void auth( String ticket ) {

		AuthMessage authMsg = AuthMessage.newBuilder()
			.setTicket( ticket )
			.build();

		// 发送认证消息
		channel.writeAndFlush( authMsg );
	}

	public void auth( String id, String userId, long timestamp, String signature ) {

		SessionMessage sessionMsg = SessionMessage.newBuilder()
			.setId( id )
			.setUserId( userId )
			.setTimestamp( timestamp )
			.setSignature( signature )
			.build();

		// 发送认证消息
		channel.writeAndFlush( sessionMsg );
	}

	public void sendMessage( final String sender, final String receiver, final String message ) {
		CommMessage commMsg = new CommMessage();
		commMsg.setFrom( sender );
		commMsg.setTo( receiver );
		commMsg.setType( "text" );
		commMsg.setBody( message.getBytes() );

		// 发送文本消息
		channel.writeAndFlush( commMsg );
	}
}
