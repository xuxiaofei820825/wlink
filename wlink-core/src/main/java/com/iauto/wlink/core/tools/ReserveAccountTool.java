package com.iauto.wlink.core.tools;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class ReserveAccountTool {

	public static void main( String[] args ) {

		String key = "UhZr6vyeBu0KmlX9"; // 128 bit key
		String initVector = "UTbKkKQ335whZicI"; // 16 bytes IV

		System.out.println( decrypt( key, initVector,
			encrypt( key, initVector, "U000001;" + System.currentTimeMillis() ) ) );
	}

	public static String decrypt( String key, String initVector, String encrypted ) {
		try {
			IvParameterSpec iv = new IvParameterSpec( initVector.getBytes( "UTF-8" ) );
			SecretKeySpec skeySpec = new SecretKeySpec( key.getBytes( "UTF-8" ), "AES" );

			Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5PADDING" );
			cipher.init( Cipher.DECRYPT_MODE, skeySpec, iv );

			byte[] original = cipher.doFinal( Base64.decodeBase64( encrypted ) );

			return new String( original );
		} catch ( Exception ex ) {
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
			System.out.println( "encrypted string: " + Base64.encodeBase64URLSafeString( encrypted ) );

			return Base64.encodeBase64URLSafeString( encrypted );
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}

		return null;
	}
}
