package com.iauto.wlink.core.message.handler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.auth.Authentication;
import com.iauto.wlink.core.auth.AuthenticationProvider;
import com.iauto.wlink.core.auth.TicketAuthentication;
import com.iauto.wlink.core.exception.AuthenticationException;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.message.ErrorMessage;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.SessionMessage;
import com.iauto.wlink.core.message.TicketAuthMessage;
import com.iauto.wlink.core.message.codec.ProtoErrorMessageCodec;
import com.iauto.wlink.core.message.codec.ProtoSessionMessageCodec;
import com.iauto.wlink.core.message.codec.ProtoTicketAuthMessageCodec;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionSignHandler;

/**
 * 该消息处理器处理认证消息
 * 
 * @author xiaofei.xu
 * 
 */
public class AuthMessageHandler extends AbstractMessageHandler {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 认证处理器 */
	private AuthenticationProvider authProvider;

	/** 认证消息编解码器 */
	private MessageCodec<? extends TicketAuthMessage> authMessageCodec = new ProtoTicketAuthMessageCodec();

	/** 错误消息编解码器 */
	private MessageCodec<ErrorMessage> errorMessageCodec = new ProtoErrorMessageCodec();

	/** 会话消息编解码器 */
	private MessageCodec<SessionMessage> sessionMessageCodec = new ProtoSessionMessageCodec();

	/** 会话签名处理器 */
	private SessionSignHandler sessionSignHandler;

	@Override
	protected boolean handleMessage( Session session, CommunicationMessage message ) {
		// 判定是否为认证消息
		if ( StringUtils.equals( message.type(), MessageType.Auth ) ) {
			// log
			logger.info( "Starting to process a authentication message." );

			// check
			if ( this.authMessageCodec == null ) {
				throw new IllegalArgumentException( "AuthMessage codec is required." );
			}
			if ( this.authProvider == null ) {
				throw new IllegalArgumentException( "Authentication provider is required." );
			}

			// 解码通信包的有效荷载
			TicketAuthMessage ticketMsg = this.authMessageCodec.decode( message.payload() );
			TicketAuthentication authentication = new TicketAuthentication( ticketMsg.getTicket() );

			try {

				// 进行认证
				Authentication result = this.authProvider.authenticate( authentication );

				// 设置终端唯一识别号
				session.setTUId( String.valueOf( result.principal() ) );

				// check
				if ( sessionSignHandler == null )
					throw new IllegalArgumentException( "Sign handler of session is required." );

				// 对会话进行签名
				String signature = sessionSignHandler.sign( session );

				SessionMessage sessionMsg = new SessionMessage();
				sessionMsg.setId( session.getId() );
				sessionMsg.setSignature( signature ); // 重建会话时要验证签名
				sessionMsg.setExpireTime( session.getExpireTime() );
				sessionMsg.setTuid( String.valueOf( result.principal() ) );

				// 把会话返回给终端，用于重新建立会话
				session.send( new CommunicationMessage(
					MessageType.Session,
					sessionMessageCodec.encode( sessionMsg ) ) );
			} catch ( AuthenticationException e ) {
				// 处理认证失败

				session.send( new CommunicationMessage(
					MessageType.Error,
					errorMessageCodec.encode( new ErrorMessage( "AUTHENTICATE_FAILURE" ) ) ) );
			}

			return true;
		} else {
			// 未认证
			if ( !session.isAuthenticated() ) {
				// 返回错误，不再将消息传递

				session.send( new CommunicationMessage(
					MessageType.Error,
					errorMessageCodec.encode( new ErrorMessage( "UN_AUTHENTICATED" ) ) ) );

				return true;
			}
		}

		// 传递给下一个处理器处理
		return false;
	}

	// ==========================================================================
	// setter/getter

	public void setAuthProvider( AuthenticationProvider authProvider ) {
		this.authProvider = authProvider;
	}

	public void setAuthMessageCodec( MessageCodec<? extends TicketAuthMessage> authMessageCodec ) {
		this.authMessageCodec = authMessageCodec;
	}

	public void setErrorMessageCodec( MessageCodec<ErrorMessage> errorMessageCodec ) {
		this.errorMessageCodec = errorMessageCodec;
	}

	public void setSessionSignHandler( SessionSignHandler sessionSignHandler ) {
		this.sessionSignHandler = sessionSignHandler;
	}
}
