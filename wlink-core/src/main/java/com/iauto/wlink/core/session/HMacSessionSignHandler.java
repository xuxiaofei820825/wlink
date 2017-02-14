package com.iauto.wlink.core.session;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.iauto.wlink.core.exception.InvalidSignatureSessionException;

/**
 * 该类使用HMACSHA256算法对会话进行签名
 * 
 * @author xiaofei.xu
 * 
 */
public class HMacSessionSignHandler implements SessionSignHandler {

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

		byte[] rawHmac = HmacUtils.hmacSha256(
			Base64.decodeBase64( key ), content.getBytes( Charsets.UTF_8 ) );

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
	public void validate( String id, String tuid, long expireTime, final String signature )
			throws InvalidSignatureSessionException {
		if ( !StringUtils.equals( sign( id, tuid, expireTime ), signature ) )
			throw new InvalidSignatureSessionException();
	}
}
