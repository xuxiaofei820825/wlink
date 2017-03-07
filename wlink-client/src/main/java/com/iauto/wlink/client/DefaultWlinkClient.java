package com.iauto.wlink.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.client.channel.DefaultChannelInitializer;
import com.iauto.wlink.client.exception.AuthenticationException;
import com.iauto.wlink.core.Constant;
import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.message.DefaultTerminalMessage;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.SessionMessage;
import com.iauto.wlink.core.message.TerminalMessage;
import com.iauto.wlink.core.message.TicketAuthMessage;
import com.iauto.wlink.core.message.codec.ProtoSessionMessageCodec;
import com.iauto.wlink.core.message.codec.ProtoTerminalMessageCodec;
import com.iauto.wlink.core.message.codec.ProtoTicketAuthMessageCodec;

/**
 * WlinkClient基于NETTY的默认实现。
 * 
 * @author xiaofei.xu
 * 
 */
public class DefaultWlinkClient implements WlinkClient, CommunicationMessageListener,
		ConnectionListener {

	/** logger */
	private final static Logger logger = LoggerFactory.getLogger( DefaultWlinkClient.class );

	/** 重连时间间隔 */
	private final static long Reconnect_interval_seconds = 10 * 1000;

	/** 是否已建立连接 */
	private final AtomicBoolean isConnected = new AtomicBoolean( false );

	/** 服务端地址 */
	private final String host;

	/** 服务端端口号 */
	private final int port;

	/** IO线程组 */
	private final Bootstrap boss;
	private final EventLoopGroup group;

	/** 会话键值 */
	public static final AttributeKey<Session> SessionKey =
			AttributeKey.newInstance( "session" );

	/** 保存认证后获取的Session值，可用于重新连接登录 */
	private Session session;

	/** 当前用户终端连接通道 */
	private Channel channel;

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

		// 建立主线程与IO线程
		group = new NioEventLoopGroup( 1 );
		boss = new Bootstrap();
	}

	/**
	 * 创建一个新的{@link DefaultWlinkClient}实例
	 * 
	 * @param host
	 *          服务端地址
	 * @param port
	 *          端口号
	 */
	public synchronized static DefaultWlinkClient newInstance( final String host, final int port ) {
		final DefaultWlinkClient instance = new DefaultWlinkClient( host, port );
		instance.init();
		return instance;
	}

	public void init() {

		// 设置通道初始化器
		DefaultChannelInitializer channelInitializer = new DefaultChannelInitializer();
		channelInitializer.setCommMessageListener( this );
		channelInitializer.setConnectionListener( this );

		boss.group( group )
				.channel( NioSocketChannel.class )
				.option( ChannelOption.SO_KEEPALIVE, true )
				.remoteAddress( this.host, this.port )
				.handler( channelInitializer );
	}

	/**
	 * 建立与服务端的连接
	 * 
	 */
	public void connect() {

		while ( !isConnected.get() ) {

			// 连接到服务器(异步请求)
			ChannelFuture future = boss.connect( this.host, this.port );

			// info
			logger.info( "connecting to wlink server. host:{}, port:{}", this.host, this.port );

			// 设置监听器
			future.addListener( new ChannelFutureListener() {

				@Override
				public void operationComplete( ChannelFuture future ) throws Exception {

					// 连接被取消
					if ( future.isCancelled() ) {
						throw new RuntimeException( "connection operation is cancelled." );
					}

					// 连接未成功
					if ( !future.isSuccess() ) {
						// error log
						if ( logger.isErrorEnabled() )
							logger.error( "Failed to connect wlink server. " + future.cause().getMessage(), future.cause() );
					}
					else {

						// log
						logger.info( "AAAAAAAAAAAAAAAAAAAAAAAAAAA" );

						// 设置状态为已连接
						isConnected.compareAndSet( false, true );

						// log
						logger.info( "Succeed to connect wlink server. host:{}, port:{}", host, port );

						// 缓存已建立的Channel
						channel = future.channel();
					}

					// 唤醒主线程
					synchronized ( lock ) {
						lock.notifyAll();
					}
				}
			} );

			// 主线程等待结果
			synchronized ( lock ) {

				try {
					// info log
					logger.info( "waiting for connect to wlink server......" );

					lock.wait();

					// info log
					logger.info( "BBBBBBBBBBBBBB" );

					if ( !isConnected.get() ) {
						// info log
						logger.info( "{} seconds later, try to reconnect......", Reconnect_interval_seconds / 1000 );

						Thread.sleep( Reconnect_interval_seconds );
					}
				}
				catch ( Exception e ) {
					// ignore

					// error
					logger.error( "Error occured.", e );
				}
			}
		}
	}

	/**
	 * 使用认证票据进行用户身份认证
	 * 
	 * @param ticket
	 *          认证票据
	 */
	public void auth( String ticket ) throws AuthenticationException {

		// 检查是否已建立连接
		if ( !isConnected.get() )
			return;

		// info
		logger.info( "Authenticate user with authentication ticket. ticket:{}", ticket );

		try {

			TicketAuthMessage ticketAuthMessage = new TicketAuthMessage( ticket );
			ProtoTicketAuthMessageCodec authCodec = new ProtoTicketAuthMessageCodec();

			// 封装通讯包
			CommunicationMessage comm = new CommunicationMessage();
			comm.setPayload( authCodec.encode( ticketAuthMessage ) );
			comm.setType( Constant.MessageType.Auth );

			// 发送认证消息
			channel.writeAndFlush( comm );

			// info
			logger.info( "waiting for authentication response......" );

			// 锁定当前线程，等待认证响应
			synchronized ( lock ) {
				lock.wait();
			}

			// info
			logger.info( "authentication response message received." );

			Session sctx = channel.attr( SessionKey ).get();

			// info
			logger.info( "succeed to process authentication. session:[id:{}, uid:{}, expiredTime:{}, signature:{}]",
					sctx.getId(), sctx.gettUId(), sctx.getExpireTime(), sctx.getSignature() );

		}
		catch ( Exception ex ) {
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

		// 检查是否已建立连接
		if ( !isConnected.get() )
			return;

		try {

			SessionMessage session = new SessionMessage();
			session.setId( id );
			session.setUid( userId );
			session.setSignature( signature );
			session.setExpiredTime( expireTime );

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
			logger.info( "waiting for authentication response......" );
		}
		catch ( Exception ex ) {
			throw new AuthenticationException( ex );
		}
	}

	public void sendMessage( String receiver, String type, byte[] body ) {

		// 检查是否已建立连接
		if ( !isConnected.get() )
			return;

		// info log
		logger.info( "send a message. receiver:{}, type:{}, payload:{} bytes", receiver, type, body.length );

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

	// ===================================================================================
	// implement of CommunicationMessageListener

	@Override
	public void onMessage( CommunicationMessage commMessage ) {

		// 解码
		boolean isSession = StringUtils.equals( commMessage.type(), MessageType.Session );

		if ( isSession ) {
			ProtoSessionMessageCodec codec = new ProtoSessionMessageCodec();
			SessionMessage sessionMsg = codec.decode( commMessage.payload() );

			this.session = new Session();
			this.session.setId( sessionMsg.getId() );
			this.session.settUId( sessionMsg.getUid() );
			this.session.setExpireTime( sessionMsg.getExpiredTime() );
			this.session.setSignature( sessionMsg.getSignature() );

			channel.attr( SessionKey ).set( session );

			// 通知已收到认证响应
			synchronized ( lock ) {
				lock.notifyAll();
			}
		}

		boolean isTerminal = StringUtils.equals( commMessage.type(), MessageType.Terminal );

		if ( isTerminal ) {
			MessageCodec<TerminalMessage> codec = new ProtoTerminalMessageCodec();
			TerminalMessage tmMessage = codec.decode( commMessage.payload() );

			// info
			logger.info( "a terminal message received. type:{}, from:{}", tmMessage.type(), tmMessage.from() );
		}

		boolean isError = StringUtils.equals( commMessage.type(), MessageType.Error );

		if ( isError ) {
			// info
			logger.info( "a error message received." );
		}
	}

	// ===================================================================================
	// implement of ConnectionListener

	@Override
	public void onOpened() {

	}

	@Override
	public void onClosed() {
		// info log
		logger.info( "Connection to wlink server is closed, try to reconnect." );

		// 设置状态为已连接
		isConnected.compareAndSet( true, false );

		// 关闭
		this.group.shutdownGracefully();

		// this.connect();
	}
}
