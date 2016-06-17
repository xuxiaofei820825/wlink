package com.iauto.wlink.client;

import org.apache.commons.lang.StringUtils;

public class SingleClient {

	public static void main( String[] args ) throws Exception {

		String ticket = StringUtils.EMPTY;
		ticket = "T" + String.format( "%05d", 2 );

		DefaultClient client = new DefaultClient( "localhost", 2391 );
		client.connect();
		client.auth( ticket );

		while ( true ) {
			Thread.sleep( 10000 );
			client.sendMessage( "U00002", "U00001", "ahfahfahfjskadhfdkj" );
		}
	}
}
