package com.iauto.wlink.core.auth;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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

	/** 会话编号 */
	private final String id;

	/** 用户编号 */
	private final String userId;

	/** 时间戳 */
	private long timestamp;

	/** 用户对应的Channel */
	private Channel channel;

	public SessionContext( final String id, final String userId ) {
		this.id = id;
		this.userId = userId;
	}

	public SessionContext( final String userId, final Channel channel ) {
		this.userId = userId;
		this.id = UUID.randomUUID().toString().replace( "-", "" );
		this.channel = channel;
		this.timestamp = System.currentTimeMillis();
	}

	public SessionContext( final String id, final String userId, final Channel channel ) {
		this.userId = userId;
		this.id = id;
		this.channel = channel;
	}

	public static String sign( final String key, final SessionContext context )
			throws Exception {

		// 初始化
		String result = StringUtils.EMPTY;

		// 生成用来进行签名的字符串
		String sessionContent = context.getId() + ";" + context.getUserId() + ";" + context.getTimestamp();

		// 生成会话信息的签名
		SecretKeySpec signingKey = new SecretKeySpec( Base64.decodeBase64( key ), HMAC_SHA256 );
		Mac mac = Mac.getInstance( HMAC_SHA256 );
		mac.init( signingKey );
		byte[] rawHmac = mac.doFinal( sessionContent.getBytes() );

		result = Base64.encodeBase64URLSafeString( rawHmac );

		return result;
	}

	public static boolean validate( String key, SessionContext session, String signature ) {

		try {
			String tmp = sign( key, session );
			return StringUtils.equals( tmp, signature );
		} catch ( Exception ex ) {
			logger.error( "Exception occoured!", ex );
		}

		return false;
	}

	// =====================================================
	// setter/getter

	public String getUserId() {
		return userId;
	}

	public String getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp( long timestamp ) {
		this.timestamp = timestamp;
	}

	public static void add( SessionContext session ) {
		sessions.get().put( session.getId(), session );
	}

	public static SessionContext getSession( String id ) {
		if ( StringUtils.isBlank( id ) )
			return null;
		return sessions.get().get( id );
	}

	public static Map<String, SessionContext> getSessions() {
		return sessions.get();
	}

	public Channel getChannel() {
		return channel;
	}
}
