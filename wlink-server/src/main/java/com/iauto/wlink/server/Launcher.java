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

			// 加载应用配置项
			ApplicationSetting.getInstance().load();

			// info
			logger.info( "Succeed to load application configuration!!!" );
		} catch ( Exception ex ) {
			// warn
			logger.warn( "Failed to load application configuration, use the default configuration." );

		}

		DefaultServerBootstrap server = new DefaultServerBootstrap();

		try {
			server.start();
		} catch ( Exception e ) {
			logger.error( "Failed to start the service.", e );
		}
	}
}
