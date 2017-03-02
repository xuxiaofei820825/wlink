package com.iauto.wlink.core.message.handler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.exception.ExpiredSessionException;
import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.SessionMessage;
import com.iauto.wlink.core.message.codec.ProtoSessionMessageCodec;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionSignHandler;

/**
 * 该消息处理器处理会话恢复
 * 
 * @author xiaofei.xu
 * 
 */
public class SessionMessageHandler extends AbstractMessageHandler {

	/** logger */
	private static final Logger logger = LoggerFactory.getLogger( SessionMessageHandler.class );

	/** 错误消息编解码器 */
	private MessageCodec<SessionMessage> sessionMessageCodec = new ProtoSessionMessageCodec();

	/** 会话签名处理器 */
	private SessionSignHandler sessionSignHandler;

	@Override
	public void handleMessage( Session session, CommunicationMessage message ) throws MessageProcessException {

		// 如果不是会话消息，则传递到链下一个处理器
		if ( !StringUtils.equals( message.type(), MessageType.Session ) && getNextHandler() != null ) {
			getNextHandler().handleMessage( session, message );
			return;
		}

		// debug log
		logger.debug( "starting to process a session message......" );

		// check
		if ( sessionSignHandler == null )
			throw new IllegalArgumentException( "Sign handler of session is required." );

		// 解码
		SessionMessage sessionMsg = sessionMessageCodec.decode( message.payload() );

		// debug log
		logger.debug( "information of decoded session message: id:{}, uid:{}, expiredTime:{}, signature:{}",
				sessionMsg.getId(), sessionMsg.getUid(), sessionMsg.getExpiredTime(), sessionMsg.getSignature() );

		// 验证会话的签名
		sessionSignHandler.validate( sessionMsg.getId(), sessionMsg.getUid(),
				sessionMsg.getExpiredTime(), sessionMsg.getSignature() );

		// 验证会话的有效期
		if ( System.currentTimeMillis() > sessionMsg.getExpiredTime() ) {

			// debug log
			logger.debug( "session is expired. id:{}, uid:{}", sessionMsg.getId(), sessionMsg.getUid() );

			// 过期异常
			throw new ExpiredSessionException();
		}

		// 恢复会话内容
		session.setId( sessionMsg.getId() );
		session.setUid( sessionMsg.getUid() );
		session.setExpiredTime( sessionMsg.getExpiredTime() );
	}

	// ================================================================================================
	// setter/getter

	public void setSessionMessageCodec( MessageCodec<SessionMessage> sessionMessageCodec ) {
		this.sessionMessageCodec = sessionMessageCodec;
	}

	public void setSessionSignHandler( SessionSignHandler sessionSignHandler ) {
		this.sessionSignHandler = sessionSignHandler;
	}
}
