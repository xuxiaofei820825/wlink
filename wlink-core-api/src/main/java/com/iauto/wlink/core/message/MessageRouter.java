package com.iauto.wlink.core.message;

import com.google.common.util.concurrent.ListenableFuture;
import com.iauto.wlink.core.exception.MessageRouteException;
import com.iauto.wlink.core.session.SessionContext;

public interface MessageRouter {
	ListenableFuture<Object> send( CommMessage<byte[]> message ) throws MessageRouteException;

	void register( SessionContext ctx ) throws MessageRouteException;

	void unregister( SessionContext ctx ) throws MessageRouteException;
}
