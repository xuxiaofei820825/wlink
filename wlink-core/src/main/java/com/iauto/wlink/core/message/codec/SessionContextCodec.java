package com.iauto.wlink.core.message.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.comm.CommunicationPackage;
import com.iauto.wlink.core.message.CommMessage;
import com.iauto.wlink.core.message.Constant;
import com.iauto.wlink.core.message.Constant.SessionCodecEnv;
import com.iauto.wlink.core.message.proto.ErrorMessageProto.ErrorMessage;
import com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage;
import com.iauto.wlink.core.message.worker.AuthWorker;
import com.iauto.wlink.core.message.worker.MessageWorker;
import com.iauto.wlink.core.session.SessionContext;

public class SessionContextCodec extends MessageToMessageCodec<CommunicationPackage, SessionMessage> {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 连接用URL */
	private final static String url = "amqp://guest:guest@test/test?brokerlist='tcp://172.26.188.173:5672'";

	/** 与QPID服务端的会话(每个线程创建一个会话) */
	private static ThreadLocal<Session> sessions = new ThreadLocal<Session>();

	/** 当前会话上下文 */
	private SessionContext sessionContext;

	/** 消息处理器 */
	private final MessageWorker worker;

	private final SessionCodecEnv env;

	public SessionContextCodec( MessageWorker worker, SessionCodecEnv env ) {
		this.worker = worker;
		this.env = env;
	}

	/**
	 * 设置会话上下文
	 * 
	 * @param sessionContext
	 *          会话上下文
	 */
	public void setSessionContext( SessionContext sessionContext, ChannelHandlerContext ctx ) throws Exception {

		// 设置会话上下文
		this.sessionContext = sessionContext;

		// 开始监听发给该用户的消息
		createMessageListener( ctx, sessionContext.getUserId() );
	}

	public SessionContext getSessionContext() {
		return sessionContext;
	}

	private void createMessageListener( final ChannelHandlerContext ctx, final String userId ) throws Exception {
		// 获取当前线程的会话
		Session session = sessions.get();

		if ( session == null ) {
			// 如果当前线程的会话还没有创建，则为当前线程创建一个

			// debug
			logger.debug( "Session of current thread has not been created, create it first" );

			// 与broker创建一个连接
			Connection conn = new AMQConnection( url );

			// 建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

			// 设置本线程的Session
			sessions.set( session );

			// 创建连接
			conn.start();
		}

		// 在当前的会话上创建消费者，监听发送给用户的消息
		Destination dest = new AMQAnyDestination( "ADDR:" + "message.topic/" + userId );
		MessageConsumer consumer = session.createConsumer( dest );

		// 设置监听器
		consumer.setMessageListener( new CommMessageLinstener( ctx, userId ) );
	}

	// ==================================================================================
	// 编解码

	@Override
	protected void encode( ChannelHandlerContext ctx, SessionMessage msg, List<Object> out ) throws Exception {
		// 获取会话消息的ProtoBuffer编码
		byte[] sessionBytes = msg.toByteArray();

		CommunicationPackage comm = new CommunicationPackage();
		comm.setType( "session" );
		comm.setBody( sessionBytes );

		// 传递到下一个处理器
		out.add( comm );
	}

	@Override
	protected void decode( ChannelHandlerContext ctx, CommunicationPackage msg, List<Object> out ) throws Exception {

		if ( this.env == SessionCodecEnv.Server ) {
			// 服务端行为

			// 检查会话上下文
			if ( this.sessionContext != null ) {
				// debug
				logger.debug( "Session has been created, so send message to next handler." );

				// 消息流转到下一个处理器
				out.add( msg );
				return;
			}

			// 会话上下文还未建立
			// 如果是认证消息，则添加认证处理器来处理认证
			if ( StringUtils.equals( Constant.MessageType.Auth, msg.getType() ) ) {

				// 添加认证处理器
				ctx.pipeline().addAfter( Constant.MessageType.Session, Constant.MessageType.Auth,
					new AuthenticationMessageCodec( new AuthWorker(
						"9aROHg2eQXQ6X3XKrXGKWjXrLiRIO25CKTyz212ujvc" ) ) );

				// 流转到下一个处理器
				out.add( msg );

				// 返回
				return;
			}
		}

		// 如果是会话消息，则创建会话上下文
		if ( StringUtils.equals( "session", msg.getType() ) ) {
			// log
			logger.info( "Processing the session message......" );

			// 异步处理会话
			if ( this.worker != null )
				worker.process( ctx, msg.getHeader(), msg.getBody() );

			// 返回
			return;
		}

		if ( this.env == SessionCodecEnv.Server ) {
			// log
			logger.info( "Session context has not been created, ignore the message." );

			// 其余类型的消息将被忽略(不会继续流转到下一个处理器)，且返回未认证的错误消息
			ErrorMessage error = ErrorMessage.newBuilder()
				.setError( "UnAuthenticated" )
				.build();
			ctx.channel().writeAndFlush( error );
		}
	}
}

class CommMessageLinstener implements MessageListener {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 通道处理器上下文 */
	private final ChannelHandlerContext ctx;

	/** 监听消息的用户 */
	private final String userId;

	public CommMessageLinstener( ChannelHandlerContext ctx, String userId ) {
		this.ctx = ctx;
		this.userId = userId;
	}

	public void onMessage( Message message ) {

		try {

			String from = message.getStringProperty( "from" );
			String to = message.getStringProperty( "to" );

			if ( !StringUtils.equals( userId, to ) ) {
				// 消息接收者不一致
				return;
			}

			// log
			logger.info( "The user[ID: {}] receive a message. [from: {}]", to, from );

			BytesMessage bytes = (BytesMessage) message;
			byte[] body = new byte[] {};
			bytes.readBytes( body );

			CommMessage commMsg = new CommMessage();
			commMsg.setFrom( from );
			commMsg.setTo( to );
			commMsg.setType( "text" );
			commMsg.setBody( body );

			// 发送给接收者
			this.ctx.writeAndFlush( commMsg );

			// log
			logger.info( "Succeed to send the message to user[ID: {}]", to );
		} catch ( Exception ex ) {

			logger.info( "Failed to send the message", ex );
		}
	}
}
