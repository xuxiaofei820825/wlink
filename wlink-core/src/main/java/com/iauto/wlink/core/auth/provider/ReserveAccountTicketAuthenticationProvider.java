package com.iauto.wlink.core.auth.provider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Charsets;
import com.iauto.wlink.core.auth.TicketAuthentication;
import com.iauto.wlink.core.exception.AuthenticationException;

public class ReserveAccountTicketAuthenticationProvider extends TicketAuthenticationProvider {

	/** 加密密匙 */
	private final String key;

	/** 向量 */
	private final String iv;

	public ReserveAccountTicketAuthenticationProvider( String key, String iv ) {
		this.key = key;
		this.iv = iv;
	}

	public TicketAuthentication authenticate( String ticket ) throws AuthenticationException {

		try {

			// 对票据进行AES128解密
			IvParameterSpec iv = new IvParameterSpec( this.iv.getBytes( Charsets.UTF_8 ) );
			SecretKeySpec skeySpec = new SecretKeySpec( this.key.getBytes( Charsets.UTF_8 ), "AES" );

			Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5PADDING" );
			cipher.init( Cipher.DECRYPT_MODE, skeySpec, iv );

			byte[] original = cipher.doFinal( Base64.decodeBase64( ticket ) );

			String info = new String( original );

			// 截取用户编号
			long userId = 0L;
			userId = Long.valueOf( info.split( ";" )[0] );

			TicketAuthentication authentication = new TicketAuthentication( ticket );
			authentication.setPrincipal( userId );

			return authentication;
		} catch ( Exception ex ) {
			throw new AuthenticationException();
		}
	}
}
