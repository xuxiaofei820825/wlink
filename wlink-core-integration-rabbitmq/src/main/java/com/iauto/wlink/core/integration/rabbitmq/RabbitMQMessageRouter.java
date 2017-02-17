package com.iauto.wlink.core.integration.rabbitmq;

import com.google.common.util.concurrent.ListenableFuture;
import com.iauto.wlink.core.exception.MessageRouteException;
import com.iauto.wlink.core.message.MessageReceivedHandler;
import com.iauto.wlink.core.message.TerminalMessageRouter;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionListener;

public class RabbitMQMessageRouter implements TerminalMessageRouter, SessionListener {

	@Override
	public void init() {

	}

	@Override
	public ListenableFuture<?> subscribe( String uuid ) throws MessageRouteException {
		return null;
	}

	@Override
	public void unsubscribe( String uuid ) {

	}

	@Override
	public ListenableFuture<?> send( String type, String from, String to, byte[] message ) throws MessageRouteException {
		return null;
	}

	@Override
	public ListenableFuture<?> broadcast( String type, String from, byte[] message ) throws MessageRouteException {
		return null;
	}

	@Override
	public void setMessageReceivedHandler( MessageReceivedHandler messageReceivedHandler ) {

	}

	@Override
	public void onCreated( Session session ) {

	}

	@Override
	public void onRemoved( Session session ) {

	}
}
