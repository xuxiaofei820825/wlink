package com.iauto.wlink.core.session;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.iauto.wlink.core.exception.InvalidSignatureSessionException;
import com.iauto.wlink.core.exception.UnexpectedSystemException;

/**
 * 该类使用HMACSHA256算法对会话进行签名
 * 
 * @author xiaofei.xu
 * 
 */
public class HMacSessionSignHandler implements SessionSignHandler {

	/** 签名算法 */
	private final static String HMAC_SHA256 = "HmacSHA256";

	/** 签名密匙 */
	private final String key;

	public HMacSessionSignHandler( String key ) {
		this.key = key;
	}

	/**
	 * 签名会话
	 * 
	 * @param session
	 *          会话
	 */
	public String sign( String id, String tuid, long expireTime ) {
		// 初始化
		String result = StringUtils.EMPTY;

		// 生成用来进行签名的字符串
		Joiner joiner = Joiner.on( ";" ).skipNulls();
		String content = joiner.join( id, tuid, expireTime );

		try {

			// 生成会话信息的签名
			SecretKeySpec signingKey = new SecretKeySpec( Base64.decodeBase64( key ), HMAC_SHA256 );
			Mac mac = Mac.getInstance( HMAC_SHA256 );
			mac.init( signingKey );
			byte[] rawHmac = mac.doFinal( content.getBytes() );

			// 进行Base64编码
			result = Base64.encodeBase64URLSafeString( rawHmac );

			return result;
		} catch ( Exception ex ) {
			throw new UnexpectedSystemException();
		}
	}

	/**
	 * 验证会话的签名
	 * 
	 * @param session
	 *          会话
	 * @param signature
	 *          签名
	 */
	public void validate( String id, String tuid, long expireTime, final String signature )
			throws InvalidSignatureSessionException {
		if ( !StringUtils.equals( sign( id, tuid, expireTime ), signature ) )
			throw new InvalidSignatureSessionException();
	}
}
