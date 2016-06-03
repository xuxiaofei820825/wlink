package com.iauto.wlink.core.message.router;

import com.iauto.wlink.core.message.worker.QpidClient;

public class QpidMessageRouter implements MessageRouter {

	private static final QpidClient qpidClient = new QpidClient(
		"amqp://guest:guest@test/test?brokerlist='tcp://172.26.188.173:5672'", "amq.topic" );

	public void send( String sender, String receiver, byte[] message ) {
		qpidClient.send( "safdsa" );
	}

	public void receive( String receiver ) {

	}

}
