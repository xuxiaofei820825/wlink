package com.iauto.wlink.core.message.handler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.session.Session;

public class TerminalMessageHandler extends AbstractMessageHandler {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( TerminalMessageHandler.class );

	@Override
	public void handleMessage( Session session, CommunicationMessage message ) {

		if ( !StringUtils.equals( message.type(), MessageType.Terminal ) ) {

			// 传递给下一个处理器处理
			if ( getNextHandler() != null ) {
				getNextHandler().handleMessage( session, message );
				return;
			}
		}

		// log
		logger.info( "Starting to process a terminal message." );

// try {
// Thread.sleep( 3000 );
// } catch ( InterruptedException e ) {
// e.printStackTrace();
// }
		long cnt = 0;
		for ( int idx = 0; idx < 100000000; idx++ ) {
			cnt = cnt + 1;
		}

		logger.info( "Count: {}", cnt );

	}
}
