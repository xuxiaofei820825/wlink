package com.iauto.wlink.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.client.channel.DefaultChannelInitializer;
import com.iauto.wlink.client.exception.AuthenticationException;
import com.iauto.wlink.core.Constant;
import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.message.DefaultTerminalMessage;
import com.iauto.wlink.core.message.SessionMessage;
import com.iauto.wlink.core.message.TicketAuthMessage;
import com.iauto.wlink.core.message.codec.ProtoSessionMessageCodec;
import com.iauto.wlink.core.message.codec.ProtoTerminalMessageCodec;
import com.iauto.wlink.core.message.codec.ProtoTicketAuthMessageCodec;

public class DefaultWlinkClient implements WlinkClient {

	/** logger */
	private final static Logger logger = LoggerFactory.getLogger( DefaultWlinkClient.class );

	/** 服务端地址 */
	private String host;

	/** 服务端端口号 */
	private int port;

	/** 当前用户终端连接通道 */
	private Channel channel;

	/** IO线程组 */
	private EventLoopGroup group;

	/** 会话键值 */
	public static final AttributeKey<Session> SessionKey =
			AttributeKey.newInstance( "session" );

	/** 线程锁 */
	private final Object lock = new Object();

	/**
	 * 私有构造函数
	 * 
	 * @param host
	 *          服务端地址
	 * @param port
	 *          端口号
	 */
	private DefaultWlinkClient( final String host, final int port ) {
		this.host = host;
		this.port = port;
	}

	/**
	 * 创建一个新的{@link DefaultWlinkClient}实例
	 * 
	 * @param host
	 *          服务端地址
	 * @param port
	 *          端口号
	 */
	public static DefaultWlinkClient newInstance( final String host, final int port ) {
		return new DefaultWlinkClient( host, port );
	}

	/**
	 * 建立与服务端的连接
	 * 
	 * @throws Exception
	 *           发生连接异常
	 */
	public void connect() throws Exception {

		// 建立主线程与IO线程
		group = new NioEventLoopGroup();
		Bootstrap boss = new Bootstrap();

		boss.group( group )
			.channel( NioSocketChannel.class )
			.option( ChannelOption.SO_KEEPALIVE, true )
			.remoteAddress( this.host, this.port )
			.handler( new DefaultChannelInitializer() );

		// info
		logger.info( "Connecting to wlink server. host:{}, port:{}", this.host, this.port );

		// 连接服务端(等待直到连接成功)
		ChannelFuture future = boss.connect( this.host, this.port )
			.sync();

		if ( future.isDone() ) {

			// 连接被取消
			if ( future.isCancelled() ) {
				throw new RuntimeException( "Connection operation is cancelled!!!" );
			}

			// 连接未成功
			if ( !future.isSuccess() ) {
				throw new RuntimeException( future.cause() );
			}

			// log
			logger.info( "Succeed to connect wlink server!!!" );

			// 缓存已建立的Channel
			this.channel = future.channel();
		} else {
			// 连接未完成
			throw new RuntimeException( "Connection operation is uncompleted!!!" );
		}
	}

	/**
	 * 使用认证票据进行用户身份认证
	 * 
	 * @param ticket
	 *          认证票据
	 */
	public void auth( String ticket ) throws AuthenticationException {

		// log
		logger.info( "Authenticate user with authentication ticket. ticket:{}", ticket );

		try {

			channel.pipeline().addLast( new SimpleChannelInboundHandler<CommunicationMessage>() {
				@Override
				protected void channelRead0( ChannelHandlerContext ctx, CommunicationMessage msg ) throws Exception {

					// 解码
					boolean isSession = StringUtils.equals( msg.type(), MessageType.Session );

					if ( isSession ) {
						ProtoSessionMessageCodec codec = new ProtoSessionMessageCodec();
						SessionMessage sessionMsg = codec.decode( msg.payload() );

						Session session = new Session();
						session.setId( sessionMsg.getId() );
						session.settUId( sessionMsg.getTuid() );
						session.setExpireTime( sessionMsg.getExpireTime() );
						session.setSignature( sessionMsg.getSignature() );

						ctx.channel().attr( SessionKey ).set( session );

						// 通知已收到认证响应
						synchronized ( lock ) {
							lock.notifyAll();
						}

						channel.pipeline().removeLast();
					}
				}
			} );

			TicketAuthMessage ticketAuthMessage = new TicketAuthMessage( ticket );
			ProtoTicketAuthMessageCodec authCodec = new ProtoTicketAuthMessageCodec();

			// 封装通讯包
			CommunicationMessage comm = new CommunicationMessage();
			comm.setPayload( authCodec.encode( ticketAuthMessage ) );
			comm.setType( Constant.MessageType.Auth );

			// 发送认证消息
			channel.writeAndFlush( comm );

			// info
			logger.info( "Waiting for authentication response......" );

			// 锁定当前线程，等待认证响应
			synchronized ( lock ) {
				lock.wait();
			}

			// info
			logger.info( "Receive authentication response message." );

			Session sctx = channel.attr( SessionKey ).get();

			// info
			logger.info( "Succeed to process authentication. [session:{}, userId:{}, signature:{}]",
				sctx.getId(), sctx.gettUId(), sctx.getSignature() );

		} catch ( Exception ex ) {
			throw new AuthenticationException( ex );
		}
	}

	/**
	 * 使用服务端返回的会话信息恢复会话
	 * 
	 * @param id
	 *          会话编号
	 * @param userId
	 *          用户编号
	 * @param timestamp
	 *          创建会话时的时间戳
	 * @param signature
	 *          服务端返回的签名
	 */
	public void auth( String id, String userId, long expireTime, String signature ) throws AuthenticationException {

		try {

			SessionMessage session = new SessionMessage();
			session.setId( id );
			session.setTuid( userId );
			session.setSignature( signature );
			session.setExpireTime( expireTime );

			ProtoSessionMessageCodec codec = new ProtoSessionMessageCodec();

			CommunicationMessage comm = new CommunicationMessage();
			comm.setPayload( codec.encode( session ) );
			comm.setType( Constant.MessageType.Session );

			// 发送认证消息
			channel.writeAndFlush( comm );

			// 等待认证响应
			synchronized ( lock ) {
				lock.wait();
			}

			// info
			logger.info( "Waiting for authentication response......" );
		} catch ( Exception ex ) {
			throw new AuthenticationException( ex );
		}
	}

	public void sendMessage( String receiver, String type, byte[] body ) {
		Session sctx = channel.attr( SessionKey ).get();
		String userId = sctx.gettUId();
		DefaultTerminalMessage terminalMsg = new DefaultTerminalMessage( type, userId, receiver, body );

		ProtoTerminalMessageCodec codec = new ProtoTerminalMessageCodec();

		CommunicationMessage comm = new CommunicationMessage();
		comm.setPayload( codec.encode( terminalMsg ) );
		comm.setType( Constant.MessageType.Terminal );

		// 发送消息
		channel.writeAndFlush( comm );
	}

	public void disconnect() {
		channel.disconnect();
		group.shutdownGracefully();
	}
}
