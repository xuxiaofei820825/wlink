package com.iauto.wlink.client;

/**
 * Hello world!
 * 
 */
public class App
{
	public static void main( String[] args ) throws Exception
	{

		DefaultClient client = new DefaultClient( "localhost", 2391 );
		client.connect();

		client.auth( "ticket_AAAA" );

		
		while (true) {

		Thread.sleep( 20000 );

		client.sendText( "woaijuanjuan2" );
		}
	}
}
