package com.iauto.wlink.server;

public class ServerStateStatistics {

	private final ThreadLocal<Integer> clientsOfCurrentThread = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return new Integer( 0 );
		}
	};

	public ThreadLocal<Integer> getClientsOfCurrentThread() {
		return clientsOfCurrentThread;
	}
}
