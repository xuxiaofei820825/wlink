package com.iauto.wlink.client;

import java.util.UUID;

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

		client.auth( UUID.randomUUID().toString() );

		while ( true ) {
			client.sendText( "woaijuanjuan2" );
			Thread.sleep( 20000 );
		}
	}
}
