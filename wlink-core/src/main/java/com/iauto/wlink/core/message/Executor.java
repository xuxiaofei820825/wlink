package com.iauto.wlink.core.message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Executor {

	/** 业务线程池 */
	private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
		50, 50, 30L, TimeUnit.SECONDS,
		new ArrayBlockingQueue<Runnable>( 5000 ),
		new TaskRejectedHandler() );

	public static void execute( Runnable command ) {
		// 执行Command
		executor.execute( command );
	}
}

class TaskRejectedHandler implements RejectedExecutionHandler {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	public void rejectedExecution( Runnable r, ThreadPoolExecutor executor ) {
		System.err.println( String.format( "Task %d rejected.", r.hashCode() ) );

		logger.info( "Task {} rejected.", r.hashCode() );
	}
}
