package com.iauto.wlink.core.integration.rabbitmq;

import com.google.common.util.concurrent.ListenableFuture;
import com.iauto.wlink.core.exception.MessageRouteException;
import com.iauto.wlink.core.message.MessageReceivedHandler;
import com.iauto.wlink.core.message.TerminalMessageRouter;

public class RabbitMessageRouter implements TerminalMessageRouter {

	public void init() {

	}

	public ListenableFuture<?> subscribe( String uuid ) throws MessageRouteException {
		return null;
	}

	public void unsubscribe( String uuid ) {

	}

	public ListenableFuture<?> send( String type, String from, String to, byte[] message ) throws MessageRouteException {
		return null;
	}

	public ListenableFuture<?> broadcast( String type, String from, byte[] message ) throws MessageRouteException {
		return null;
	}

	public void setMessageReceivedHandler( MessageReceivedHandler messageReceivedHandler ) {

	}
}
