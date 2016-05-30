package com.iauto.wlink.server;

public class ServerStateStatistics {

	private final ThreadLocal<Integer> clientsOfCurrentThread = new ThreadLocal<Integer>();

	public ThreadLocal<Integer> getClientsOfCurrentThread() {
		return clientsOfCurrentThread;
	}

}
