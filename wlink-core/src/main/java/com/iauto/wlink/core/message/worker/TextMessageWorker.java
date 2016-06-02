package com.iauto.wlink.core.message.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.proto.TextMessageProto.TextMessage;

public class TextMessageWorker implements Runnable {

	// logger
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	private TextMessage txtMsg;

	public TextMessageWorker( final TextMessage txtMsg ) {
		this.txtMsg = txtMsg;
	}

	public void run() {
		// log
		logger.info( "Processing the text message......" );

		logger.debug( "Message: [from:{}, to:{}, text:{}]",
			this.txtMsg.getFrom(), txtMsg.getTo(), txtMsg.getText() );

		// 模拟耗时的网络请求
		try {
			Thread.sleep( 3000 );
		}
		catch ( InterruptedException e ) {
			// ignore
		}

		// log
		logger.info( "Finished to process the text message." );
	}
}
