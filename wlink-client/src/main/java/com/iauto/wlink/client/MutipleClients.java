package com.iauto.wlink.client;

import com.iauto.wlink.core.tools.ReserveAccountTool;

public class MutipleClients {

	public static void main( String[] args ) throws Exception {

		for ( int idx = 1; idx <= 10; idx++ ) {

			Thread thread = new Thread( new ClientRunnable( idx ) );

			// start
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
			} catch ( Exception ex ) {

			} finally {
				try {
					client.disconnect();
				} catch ( Exception e ) {
					// ignore
				}
			}
		}
	}
}
