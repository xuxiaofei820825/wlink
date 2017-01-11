package com.iauto.wlink.client;

import com.iauto.wlink.core.tools.ReserveAccountTool;

public class MutipleClients {

	public static final String TEXT_MESSAGE = "It is a static message!!";
	public static final long MIN_USER_ID = 1;
	public static final long MAX_USER_ID = 100;

	public static void main( String[] args ) throws Exception {

		// 创建用户线程
		for ( int idx = 1; idx <= MAX_USER_ID; idx++ ) {
			Thread thread = new Thread( new ClientRunnable( idx ) );
			thread.start();

			//Thread.sleep( 500 );
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

				/*
				while ( true ) {
					Random random = new Random();
					long receiver = random.nextInt( (int) MAX_USER_ID ) % ( MAX_USER_ID - MIN_USER_ID + 1 ) + MIN_USER_ID;

					client.sendMessage( String.valueOf( receiver ), "text", TEXT_MESSAGE.getBytes() );
					Thread.sleep( 500 );
				}*/
			} catch ( Exception ex ) {
				throw new RuntimeException( ex );
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
