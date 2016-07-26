package com.iauto.wlink.core.auth;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.jms.MessageConsumer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionContext {

	/** 签名算法 */
	private final static String HMAC_SHA256 = "HmacSHA256";

	/** logger */
	private final static Logger logger = LoggerFactory.getLogger( SessionContext.class );

	/** 线程级会话存储 */
	private static ThreadLocal<Map<String, SessionContext>> sessions = new ThreadLocal<Map<String, SessionContext>>() {
		@Override
		protected Map<String, SessionContext> initialValue() {
			return new HashMap<String, SessionContext>();
		}
	};

	/** 会话 */
	private final Session session;

	/** 用户对应的Channel */
	private final Channel channel;

	/** MQ消息监听器 */
	private MessageConsumer consumer;

	public SessionContext( Session session, Channel channel ) {
		this.session = session;
		this.channel = channel;
	}

	// =================================================================================
	// setter/getter

	public Channel getChannel() {
		return channel;
	}

	public MessageConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer( MessageConsumer consumer ) {
		this.consumer = consumer;
	}

	public Session getSession() {
		return session;
	}

	// =================================================================================
	// static functions

	public static String sign( final String key, final Session session )
			throws Exception {

		// 初始化
		String result = StringUtils.EMPTY;

		// 生成用来进行签名的字符串
		String sessionContent = session.getId() + ";" + session.getUserId() + ";" + session.getTimestamp();

		// 生成会话信息的签名
		SecretKeySpec signingKey = new SecretKeySpec( Base64.decodeBase64( key ), HMAC_SHA256 );
		Mac mac = Mac.getInstance( HMAC_SHA256 );
		mac.init( signingKey );
		byte[] rawHmac = mac.doFinal( sessionContent.getBytes() );

		result = Base64.encodeBase64URLSafeString( rawHmac );

		return result;
	}

	public static boolean validate( String key, Session session, String signature ) {

		try {
			String tmp = sign( key, session );
			return StringUtils.equals( tmp, signature );
		} catch ( Exception ex ) {
			logger.error( "Exception occoured!", ex );
		}

		return false;
	}

	public static void add( SessionContext sessionCtx ) {
		if ( sessionCtx == null || sessionCtx.getSession() == null )
			return;

		sessions.get().put( sessionCtx.getSession().getId(), sessionCtx );
	}

	public static SessionContext getSessionContext( String id ) {
		if ( StringUtils.isBlank( id ) )
			return null;
		return sessions.get().get( id );
	}

	public static Map<String, SessionContext> getSessions() {
		return sessions.get();
	}

}
