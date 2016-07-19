package com.iauto.wlink.core.auth.service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.iauto.wlink.core.exception.AuthenticationException;

public class ReserveAccountAuthenticationProvider implements AuthenticationProvider {

	/** 加密密匙 */
	private final String key;

	/** 向量 */
	private final String iv;

	/** char-set */
	private final static String CHAR_SET = "UTF-8";

	public ReserveAccountAuthenticationProvider( String key, String iv ) {
		this.key = key;
		this.iv = iv;
	}

	public String authenticate( final String ticket ) throws AuthenticationException {
		// 设置默认值
		String userId = StringUtils.EMPTY;

		try {

			// 对票据进行AES128解密
			IvParameterSpec iv = new IvParameterSpec( this.iv.getBytes( CHAR_SET ) );
			SecretKeySpec skeySpec = new SecretKeySpec( this.key.getBytes( CHAR_SET ), "AES" );

			Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5PADDING" );
			cipher.init( Cipher.DECRYPT_MODE, skeySpec, iv );

			byte[] original = cipher.doFinal( Base64.decodeBase64( ticket ) );

			String info = new String( original );

			// 截取用户编号
			userId = info.split( ";" )[0];
		} catch ( Exception ex ) {
			throw new AuthenticationException();
		}

		return userId;
	}
}
