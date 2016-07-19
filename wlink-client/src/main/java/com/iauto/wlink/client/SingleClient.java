package com.iauto.wlink.client;

import org.apache.commons.lang.StringUtils;

import com.iauto.wlink.core.tools.ReserveAccountTool;

public class SingleClient {

	public static void main( String[] args ) throws Exception {

		String ticket = StringUtils.EMPTY;
		ticket = "U" + String.format( "%06d", 1 );

		DefaultClient client = new DefaultClient( "localhost", 2391 );
		client.connect();
		//client.auth( ReserveAccountTool.generate( ticket ) );
		
		String sessionId = "c9d2eb76d7834e8f8f553b9e491c4aac";
		String userId = "U000002";
		long timestamp = 1468816091754L;
		String signature = "6a0tc-z1mOsQt-Zz6OM4R0DiGDCKOnlVPkYOXILx1JM";
		client.auth( sessionId, userId, timestamp, signature );

		while ( true ) {
			Thread.sleep( 3000 );
			// client.sendMessage( "U10001", "U10002", "ahfahfahfjskadhfdkj" );
		}
	}
}
