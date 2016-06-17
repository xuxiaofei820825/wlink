package com.iauto.wlink.client;

import org.apache.commons.lang.StringUtils;

/**
 * Hello world!
 * 
 */
public class App
{
	public static void main( String[] args ) throws Exception
	{

		final int client_count = 1;

		for ( int idx = 1; idx <= client_count; idx++ ) {

			String ticket = StringUtils.EMPTY;
			String userId = StringUtils.EMPTY;
			String receiver = StringUtils.EMPTY;

			ticket = "T" + String.format( "%05d", idx );
			userId = "U" + String.format( "%05d", idx );
			receiver = "U" + String.format( "%05d", idx + 1 );

			Thread thread = new Thread( new ClientRunnable( ticket, userId, receiver ) );

			// start
			thread.start();
			
			Thread.sleep( 5000 );
		}
	}
}

class ClientRunnable implements Runnable {

	private final String ticket;
	private final String userId;
	private final String receiver;

	public ClientRunnable( String ticket, String userId, String receiver ) {
		this.ticket = ticket;
		this.userId = userId;
		this.receiver = receiver;
	}

	public void run() {
		try {

			DefaultClient client = new DefaultClient( "localhost", 2391 );
			client.connect();
			client.auth( ticket );

			while ( true ) {
				Thread.sleep( 10000 );
				//client.sendMessage( userId, receiver, "HI, I am " + userId );
			}
		} catch ( Exception ex ) {
			// ignore
		}
	}
}
