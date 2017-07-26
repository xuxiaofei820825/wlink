package com.iauto.wlink.core.tools;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class SessionSignatureTool {

	/** 签名算法 */
	private final static String HMAC_SHA256 = "HmacSHA256";
	/** 签名密匙 */
	private static final String key = "9aROHg2eQXQ6X3XKrXGKWjXrLiRIO25CKTyz212ujvc";

	private static final String id = UUID.randomUUID().toString();
	private static final String userId = "U000002";

	public static void main( String[] args ) throws InvalidKeyException, NoSuchAlgorithmException {

		String sessionContent = id.replace( "-", "" ) + ";" + userId + ";" + System.currentTimeMillis();
		System.out.println( "Content: " + sessionContent );

		// 生成会话信息的签名
		SecretKeySpec signingKey = new SecretKeySpec( Base64.decodeBase64( key ), HMAC_SHA256 );
		Mac mac = Mac.getInstance( HMAC_SHA256 );
		mac.init( signingKey );
		byte[] rawHmac = mac.doFinal( sessionContent.getBytes() );
		
		System.out.println( "Base64 of content: " + Base64.encodeBase64String( "a85911cf3bef4c8f96eff2d77d0c9091,1490169154466".getBytes() ));

		System.out.println( "Signature: " + Base64.encodeBase64URLSafeString( rawHmac ) );
		System.out.println( "Signature: " + Base64.encodeBase64String( rawHmac ) );
	}
}
