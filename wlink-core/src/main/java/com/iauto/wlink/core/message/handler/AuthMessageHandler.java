package com.iauto.wlink.core.message.handler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.auth.Authentication;
import com.iauto.wlink.core.auth.AuthenticationProvider;
import com.iauto.wlink.core.auth.TicketAuthentication;
import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.exception.UnAuthenticatedException;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.SessionMessage;
import com.iauto.wlink.core.message.TicketAuthMessage;
import com.iauto.wlink.core.message.codec.ProtoSessionMessageCodec;
import com.iauto.wlink.core.message.codec.ProtoTicketAuthMessageCodec;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionManager;
import com.iauto.wlink.core.session.SessionSignHandler;

/**
 * 该消息处理器处理认证消息
 * 
 * @author xiaofei.xu
 * 
 */
public class AuthMessageHandler extends AbstractMessageHandler {

	/** logger */
	private static final Logger logger = LoggerFactory.getLogger( AuthMessageHandler.class );

	/** 认证处理器 */
	private AuthenticationProvider authProvider;

	/** 认证消息编解码器 */
	private MessageCodec<? extends TicketAuthMessage> authMessageCodec = new ProtoTicketAuthMessageCodec();

	/** 会话消息编解码器 */
	private MessageCodec<SessionMessage> sessionMessageCodec = new ProtoSessionMessageCodec();

	/** 会话签名处理器 */
	private SessionSignHandler sessionSignHandler;

	/** 会话管理器 */
	private SessionManager sessionManager;

	@Override
	public void handleMessage( Session session, CommunicationMessage message )
			throws MessageProcessException {

		if ( !StringUtils.equals( message.type(), MessageType.Auth ) ) {

			// 未认证
			if ( !session.isAuthenticated() ) {
				// 抛出未认证的异常
				throw new UnAuthenticatedException();
			}

			// 传递给下一个处理器处理
			if ( getNextHandler() != null ) {
				getNextHandler().handleMessage( session, message );
			}

			return;
		}

		// 判定是否为认证消息
		// debug log
		logger.debug( "starting to process an authentication message." );

		// check
		if ( this.authProvider == null ) {
			throw new IllegalArgumentException( "Authentication provider is required." );
		}

		// 解码通信包的有效荷载
		TicketAuthMessage ticketMsg = this.authMessageCodec.decode( message.payload() );
		TicketAuthentication authentication = new TicketAuthentication( ticketMsg.getTicket() );

		// 进行认证
		Authentication result = this.authProvider.authenticate( authentication );

		final String uid = String.valueOf( result.principal() );

		// debug log
		logger.debug( "Succeed to authenticate. uid:{}", uid );

		// 设置终端唯一识别号
		session.setUid( uid );

		// 添加到Session管理器
		sessionManager.add( session );

		// check
		if ( sessionSignHandler == null )
			throw new IllegalArgumentException( "Sign handler of session is required." );

		// debug log
		logger.debug( "signing session......" );

		// 对会话进行签名
		String signature = sessionSignHandler.sign( session.getId(), session.getUid(), session.getExpiredTime() );

		// debug log
		logger.debug( "id:{}, uid:{}, expiredTime:{}, signature: {}", session.getId(), session.getUid(),
				session.getExpiredTime(), signature );

		SessionMessage sessionMsg = new SessionMessage();
		sessionMsg.setId( session.getId() );
		sessionMsg.setSignature( signature ); // 重建会话时要验证签名
		sessionMsg.setExpiredTime( session.getExpiredTime() );
		sessionMsg.setUid( String.valueOf( result.principal() ) );

		CommunicationMessage comm = new CommunicationMessage();
		comm.setType( MessageType.Session );
		comm.setPayload( sessionMessageCodec.encode( sessionMsg ) );

		// debug log
		logger.debug( "return session content to terminal. id:{}, uid:{}",
				session.getId(), session.getUid() );

		// 把会话返回给终端，用于重新建立会话
		session.send( comm );
	}

	// ==========================================================================
	// setter/getter

	public void setAuthProvider( AuthenticationProvider authProvider ) {
		this.authProvider = authProvider;
	}

	public void setAuthMessageCodec( MessageCodec<? extends TicketAuthMessage> authMessageCodec ) {
		this.authMessageCodec = authMessageCodec;
	}

	public void setSessionSignHandler( SessionSignHandler sessionSignHandler ) {
		this.sessionSignHandler = sessionSignHandler;
	}

	public void setSessionMessageCodec( MessageCodec<SessionMessage> sessionMessageCodec ) {
		this.sessionMessageCodec = sessionMessageCodec;
	}

	public void setSessionManager( SessionManager sessionManager ) {
		this.sessionManager = sessionManager;
	}
}
