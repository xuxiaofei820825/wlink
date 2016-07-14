package com.iauto.wlink.core.auth;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

public class SessionContext {

	/** 签名算法 */
	private final static String HMAC_SHA256 = "HmacSHA256";

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

	/** 用户对应的Channel */
	private final Channel channel;

	public SessionContext( final String userId, final Channel channel ) {
		this.userId = userId;
		this.id = UUID.randomUUID().toString().replace( "-", "" );
		this.channel = channel;
	}

	public static String sign( final String key, final SessionContext context )
			throws Exception {

		// 初始化
		String result = StringUtils.EMPTY;

		// 生成用来进行签名的字符串
		final String timestamp = String.valueOf( System.currentTimeMillis() );
		String sessionContent = context.getId() + ";" + context.getUserId() + ";" + timestamp;

		// 生成会话信息的签名
		SecretKeySpec signingKey = new SecretKeySpec( Base64.decodeBase64( key ), HMAC_SHA256 );
		Mac mac = Mac.getInstance( HMAC_SHA256 );
		mac.init( signingKey );
		byte[] rawHmac = mac.doFinal( sessionContent.getBytes() );

		result = Base64.encodeBase64URLSafeString( rawHmac );

		return result;
	}

	// =====================================================
	// setter/getter

	public String getUserId() {
		return userId;
	}

	public String getId() {
		return id;
	}

	public static void addSession( SessionContext session ) {
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
