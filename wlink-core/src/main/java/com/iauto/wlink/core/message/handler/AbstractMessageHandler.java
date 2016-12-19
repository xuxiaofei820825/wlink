package com.iauto.wlink.core.message.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.session.Session;

public abstract class AbstractMessageHandler {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 下一个处理者 */
	private AbstractMessageHandler nextHandler = null;

	public final void handle( Session session, CommunicationMessage message ) {
		if ( !this.handleMessage( session, message ) ) {
			if ( this.nextHandler != null ) {
				this.nextHandler.handle( session, message );
			} else {
				// info
				logger.info( "Message handler is not exist. type of message:{}", message.type() );
			}
		}
	}

	public void setNextHandler( AbstractMessageHandler handler ) {
		this.nextHandler = handler;
	}

	protected abstract boolean handleMessage( Session session, CommunicationMessage message );
}
