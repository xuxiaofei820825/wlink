package com.iauto.wlink.core.message;

import com.iauto.wlink.core.exception.MessageRouteException;
import com.iauto.wlink.core.session.SessionContext;

public interface MessageRouter {
	void send( AbstractCommMessage<byte[]> message ) throws MessageRouteException;

	void register( SessionContext ctx ) throws MessageRouteException;

	void unregister( SessionContext ctx ) throws MessageRouteException;
}
