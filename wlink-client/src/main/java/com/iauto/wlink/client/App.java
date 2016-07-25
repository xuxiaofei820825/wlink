package com.iauto.wlink.client;

import java.util.Random;

import org.apache.commons.lang.StringUtils;

/**
 * Hello world!
 * 
 */
public class App
{

	public static int clients_max = 2;
	public static int clients_create_interval = 2;
	public static int message_interval = 3;

	public static void main( String[] args ) throws Exception
	{

		for ( int idx = 1; idx <= clients_max; idx++ ) {

			String ticket = StringUtils.EMPTY;
			String userId = StringUtils.EMPTY;

			ticket = "T" + String.format( "%05d", idx );
			userId = "U" + String.format( "%05d", idx );

			Thread thread = new Thread( new ClientRunnable( ticket, userId ) );

			// start
			thread.start();

			Thread.sleep( clients_create_interval * 1000 );
		}
	}

	private static class ClientRunnable implements Runnable {

		private final String ticket;
		private final String userId;

		public ClientRunnable( String ticket, String userId ) {
			this.ticket = ticket;
			this.userId = userId;
		}

		public void run() {
			try {

				WlinkClient client = DefaultWlinkClient.newInstance( "localhost", 2391 );
				client.connect();
				client.auth( ticket );

				while ( true ) {
					Thread.sleep( message_interval * 1000 );

					// 随机生成接收者
					Random random = new Random();
					int rd_id = random.nextInt( App.clients_max - 1 ) + 1;
					String receiver = "U" + String.format( "%05d", rd_id );

					// 向接收者发送消息
					//client.sendMessage( userId, receiver, "HI, I am " + userId );
				}
			} catch ( Exception ex ) {
				// ignore
			}
		}
	}
}
