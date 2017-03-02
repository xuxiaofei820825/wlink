package com.iauto.wlink.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 服务启动入口
 * 
 * @author xiaofei.xu
 * 
 */
public class Launcher {

	/** logger */
	private final static Logger logger = LoggerFactory.getLogger( Launcher.class );

	public static void main( String[] args ) {

		ClassPathXmlApplicationContext appContext = null;

		try {

			// info log
			logger.info( "Loading application context......" );

			appContext = new ClassPathXmlApplicationContext( "applicationContext.xml" );

			// info log
			logger.info( "Succeed to load application context." );

			DefaultServerBootstrap bootstrap = (DefaultServerBootstrap) appContext.getBean( "bootstrap" );
			bootstrap.start();

		}
		catch ( Exception ex ) {
			// warn
			logger.warn( "Failed to start wlink service." );
		}
	}
}
