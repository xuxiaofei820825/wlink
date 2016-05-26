package com.iauto.wlink.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

	// logger
	private final static Logger logger = LoggerFactory.getLogger( Launcher.class );

	public static void main( String[] args ) {

		AppConfig config = AppConfig.Builder.newBuilder()
			.port( 2391 )
			.useSSL( false )
			.heartbeatInterval( 10 )
			.cerFile( "ntc-server.crt" )
			.keyFile( "ntc-server.key" )
			.keyPassword( "suntec" )
			.build();

		DefaultServerBootstrap server = new DefaultServerBootstrap( config );

		try {
			server.start();
		}
		catch ( Exception e ) {
			logger.error( "Failed to start the service.", e );
		}
	}
}
