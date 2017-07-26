package com.iauto.wlink.client;

import java.util.Random;

import com.iauto.wlink.core.tools.ReserveAccountTool;

public class MutipleClients {

	public static final String TEXT_MESSAGE = "It is a static message!!";

	/** 最小，最大用户ID */
	public static final long MIN_USER_ID = 1;
	public static final long MAX_USER_ID = 100;
	/** 用户重复登录数 */
	public static final long MAX_REPEAT = 1;

	/** 每个终端发送最大消息数 */
	public static long MAX_MESSAGES = -1;

	/** 发送消息的间隔 */
	public static final long MESSAGE_INTERVAL = 10;

	public static void main( String[] args ) throws Exception {

		for ( int repeat = 0; repeat < MAX_REPEAT; repeat++ ) {
			// 创建用户线程
			for ( int idx = 1; idx <= MAX_USER_ID; idx++ ) {
				Thread thread = new Thread( new ClientRunnable( idx ) );
				thread.start();
			}
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
				int cnt_msg = 0;

				if ( MAX_MESSAGES > 0 ) {
					while ( cnt_msg <= MAX_MESSAGES ) {
						sendMessage( client );
						cnt_msg++;
					}
				}
				else {
					while ( true ) {
						sendMessage( client );
					}
				}
			}
			catch ( Exception ex ) {
				throw new RuntimeException( ex );
			}
			finally {
				try {
					// client.disconnect();
				}
				catch ( Exception e ) {
					// ignore
				}
			}
		}

		private void sendMessage( WlinkClient client ) throws InterruptedException {
			Random random = new Random();
			long receiver = random.nextInt( (int) MAX_USER_ID ) % ( MAX_USER_ID - MIN_USER_ID + 1 ) + MIN_USER_ID;
			client.sendMessage( String.valueOf( receiver ), "text", TEXT_MESSAGE.getBytes() );
			Thread.sleep( MESSAGE_INTERVAL );
		}
	}
}
