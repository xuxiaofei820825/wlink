package com.iauto.wlink.server;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

	// logger
	private final static Logger logger = LoggerFactory.getLogger( Launcher.class );

	public static void main( String[] args ) {

		try {
			// info
			logger.info( "Loading application configuration......" );

			// 应用配置
			Properties properties = new Properties();

			Launcher launcher = new Launcher();
			properties.load( launcher.getClass().getClassLoader().getResourceAsStream( "application.properties" ) );

// config = AppConfig.Builder.newBuilder()
// .port( Integer.valueOf( properties.getProperty( PRO_PORT ) ) )
// .useSSL( false )
// .heartbeatInterval( Integer.valueOf( properties.getProperty( PRO_HEARTBEAT_INTERVAL ) ) )
// .build();

			// 加载应用配置项
			ApplicationSetting.getInstance().load();

			// info
			logger.info( "Succeed to load application configuration!!!" );
		} catch ( Exception ex ) {
			// warn
			logger.warn( "Failed to load application configuration, use the default configuration." );

// config = AppConfig.Builder.newBuilder()
// .port( 2391 )
// .useSSL( false )
// .heartbeatInterval( 60 )
// .cerFile( "ntc-server.crt" )
// .keyFile( "ntc-server.key" )
// .keyPassword( "suntec" )
// .build();
		}

		DefaultServerBootstrap server = new DefaultServerBootstrap();

		try {
			server.start();
		} catch ( Exception e ) {
			logger.error( "Failed to start the service.", e );
		}
	}
}
