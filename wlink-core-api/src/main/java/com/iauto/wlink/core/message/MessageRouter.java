package com.iauto.wlink.core.message;

import com.iauto.wlink.core.exception.MessageRouteException;

public interface MessageRouter {
	void send( long sender, long receiver, CommMessage message ) throws MessageRouteException;

	void listen( long userId ) throws MessageRouteException;
}
