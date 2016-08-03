package com.iauto.wlink.core.session;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * 该类使用HMACSHA256算法对会话进行签名
 * 
 * @author xiaofei.xu
 * 
 */
public class HMacSessionSignatureHandler implements SessionSignatureHandler {

	/** 签名算法 */
	private final static String HMAC_SHA256 = "HmacSHA256";

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 签名密匙 */
	private final String key;

	public HMacSessionSignatureHandler( String key ) {
		this.key = key;
	}

	/**
	 * 签名会话
	 * 
	 * @param session
	 *          会话
	 */
	public String sign( Session session ) throws Exception {
		// 初始化
		String result = StringUtils.EMPTY;

		// 生成用来进行签名的字符串
		Joiner joiner = Joiner.on( ";" ).skipNulls();
		joiner.join( session.getId(), session.getUserId(), session.getTimestamp() );
		String content = joiner.toString();

		// 生成会话信息的签名
		SecretKeySpec signingKey = new SecretKeySpec( Base64.decodeBase64( key ), HMAC_SHA256 );
		Mac mac = Mac.getInstance( HMAC_SHA256 );
		mac.init( signingKey );
		byte[] rawHmac = mac.doFinal( content.getBytes() );

		// 进行Base64编码
		result = Base64.encodeBase64URLSafeString( rawHmac );

		return result;
	}

	/**
	 * 验证会话的签名
	 * 
	 * @param session
	 *          会话
	 * @param signature
	 *          签名
	 */
	public boolean validate( final Session session, final String signature ) throws Exception {
		try {
			String tmp = sign( session );
			return StringUtils.equals( tmp, signature );
		} catch ( Exception ex ) {
			logger.error( "Exception occoured!", ex );
		}
		return false;
	}
}
