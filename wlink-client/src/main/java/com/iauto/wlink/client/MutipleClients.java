package com.iauto.wlink.client;

import java.util.Random;

import com.iauto.wlink.core.tools.ReserveAccountTool;

public class MutipleClients {

	private static final String TEXT_MESSAGE = "It is a static message!!";
	private static final long MIN_USER_ID = 1;
	private static final long MAX_USER_ID = 5;

	public static void main( String[] args ) throws Exception {

		// 创建用户现场
		for ( int idx = 1; idx <= MAX_USER_ID; idx++ ) {
			Thread thread = new Thread( new ClientRunnable( idx ) );
			thread.start();
		}
	}

	private static class ClientRunnable implements Runnable {

		private final long userId;

		public ClientRunnable( long userId ) {
			this.userId = userId;
		}

		public void run() {
			WlinkClient client = DefaultWlinkClient.newInstance( "localhost", 2391 );

			try {
				client.connect();
				client.auth( ReserveAccountTool.generate( userId ) );

				while ( true ) {
					Thread.sleep( 500 );

					Random random = new Random();
					long receiver = random.nextInt( (int) MAX_USER_ID ) % ( MAX_USER_ID - MIN_USER_ID + 1 ) + MIN_USER_ID;

					client.sendMessage( receiver, "text", TEXT_MESSAGE.getBytes() );
				}
			} catch ( Exception ex ) {

			} finally {
				try {
					// client.disconnect();
				} catch ( Exception e ) {
					// ignore
				}
			}
		}
	}
}
