package com.iauto.wlink.core.integration.qpid;

import org.apache.qpid.client.AMQConnection;

public class QpidConnectionManager {

	/** 与QPID服务器的连接(每个I/O线程创建一个连接) */
	private final static ThreadLocal<AMQConnection> connections = new ThreadLocal<AMQConnection>();

	// ========================================================
	// static functions

	public static void add( AMQConnection conn ) {
		connections.set( conn );
	}

	public static AMQConnection get() {
		return connections.get();
	}

	public static void remove() {
		connections.remove();
	}
}
