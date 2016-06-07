package com.iauto.wlink.core.tools;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

public class HMACSHA256KeyGenerator {

	public static void main( String[] args ) {
		final String KEY_MAC = "HmacSHA256";
		SecretKey key;
		String str = "";
		try {
			KeyGenerator generator = KeyGenerator.getInstance( KEY_MAC );
			key = generator.generateKey();
			str = Base64.encodeBase64URLSafeString( key.getEncoded() );

			System.out.print( str );
		}
		catch ( NoSuchAlgorithmException e ) {
			e.printStackTrace();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
