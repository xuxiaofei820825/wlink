package com.iauto.wlink.client;

import com.iauto.wlink.core.tools.ReserveAccountTool;

public class SingleClient {

	private static final String TEXT_MESSAGE = "";

	public static void main( String[] args ) throws Exception {

		WlinkClient client = DefaultWlinkClient.newInstance( "localhost", 2391 );

		client.connect();
		client.auth( ReserveAccountTool.generate( 2391L ) );

		for ( int idx = 1; idx <= 100; idx++ ) {
			Thread.sleep( 1000 );
			client.sendMessage( 2390L, "text", TEXT_MESSAGE.getBytes() );
		}

// String sessionId = "c9d2eb76d7834e8f8f553b9e491c4aac";
// String userId = "U000002";
// long timestamp = 1468816091754L;
// String signature = "6a0tc-z1mOsQt-Zz6OM4R0DiGDCKOnlVPkYOXILx1JM";
// client.auth( sessionId, userId, timestamp, signature );

		client.disconnect();

		// while ( true ) {
		// Thread.sleep( 3000 );
		// client.sendMessage( "U10001", "U10002", "ahfahfahfjskadhfdkj" );
		// }
	}
}
