package com.iauto.wlink.core.message;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.message.handler.AbstractMessageHandler;
import com.iauto.wlink.core.message.handler.MessageHandler;
import com.iauto.wlink.core.session.Session;

public class MessageHandlerChain implements MessageHandler {

	/** logger */
	private static final Logger logger = LoggerFactory.getLogger( MessageHandlerChain.class );

	/** 处理责任链 */
	private MessageHandler chain = null;

	public MessageHandlerChain( List<AbstractMessageHandler> handlers ) {
		if ( handlers == null || handlers.size() == 0 )
			throw new IllegalArgumentException( "Message handler list is required." );

		handlers.add( new AbstractMessageHandler() {
			@Override
			public void handleMessage( Session session, CommunicationMessage message ) throws MessageProcessException {
				// warn log
				logger.warn( "No matched message handler for type: {}.", message.type() );
			}
		} );

		// 根据List中的顺序构建责任链
		for ( int cnt = 0; cnt < handlers.size(); cnt++ ) {
			if ( cnt == 0 ) {
				this.chain = handlers.get( 0 );
			} else {
				handlers.get( cnt - 1 ).setNextHandler( handlers.get( cnt ) );
			}
		}
	}

	@Override
	public void handleMessage( Session session, CommunicationMessage message ) throws MessageProcessException {
		chain.handleMessage( session, message );
	}

}
