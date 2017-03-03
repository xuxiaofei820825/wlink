package com.iauto.wlink.core.tools;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReserveAccountTool {

	/** logger */
	private static final Logger logger = LoggerFactory.getLogger( ReserveAccountTool.class );

	private static final String key = "UhZr6vyeBu0KmlX9"; // 128 bit key
	private static final String initVector = "UTbKkKQ335whZicI"; // 16 bytes IV

	public static void main( String[] args ) {

		System.out.println( decrypt( key, initVector,
				encrypt( key, initVector, "U000001;" + System.currentTimeMillis() ) ) );
	}

	public static String generate( long userId ) {
		String result = StringUtils.EMPTY;
		result = encrypt( key, initVector, userId + ";" + System.currentTimeMillis() );
		return result;
	}

	public static String decrypt( String key, String initVector, String encrypted ) {
		try {
			IvParameterSpec iv = new IvParameterSpec( initVector.getBytes( "UTF-8" ) );
			SecretKeySpec skeySpec = new SecretKeySpec( key.getBytes( "UTF-8" ), "AES" );

			Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5PADDING" );
			cipher.init( Cipher.DECRYPT_MODE, skeySpec, iv );

			byte[] original = cipher.doFinal( Base64.decodeBase64( encrypted ) );

			return new String( original );
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}

		return null;
	}

	public static String encrypt( String key, String initVector, String value ) {
		try {

			IvParameterSpec iv = new IvParameterSpec( initVector.getBytes( "UTF-8" ) );
			SecretKeySpec skeySpec = new SecretKeySpec( key.getBytes( "UTF-8" ), "AES" );

			Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5PADDING" );
			cipher.init( Cipher.ENCRYPT_MODE, skeySpec, iv );

			byte[] encrypted = cipher.doFinal( value.getBytes() );
			logger.debug( "encrypted string: {}", Base64.encodeBase64URLSafeString( encrypted ) );

			return Base64.encodeBase64URLSafeString( encrypted );
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}

		return null;
	}
}
