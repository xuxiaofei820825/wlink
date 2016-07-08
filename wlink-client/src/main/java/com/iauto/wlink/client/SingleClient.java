package com.iauto.wlink.client;

import org.apache.commons.lang.StringUtils;

public class SingleClient {

	public static void main( String[] args ) throws Exception {

		String ticket = StringUtils.EMPTY;
		ticket = "T" + String.format( "%05d", 10002 );

		DefaultClient client = new DefaultClient( "localhost", 2391 );
		client.connect();
		client.auth( ticket );
		
		while ( true ) {
			Thread.sleep( 3000 );
			//client.sendMessage( "U10001", "U10002", "ahfahfahfjskadhfdkj" );
		}
	}
}
