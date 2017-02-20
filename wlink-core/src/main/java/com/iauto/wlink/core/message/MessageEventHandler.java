package com.iauto.wlink.core.message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.DefaultThreadFactory;
import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.message.codec.ProtoErrorMessageCodec;
import com.iauto.wlink.core.message.handler.MessageHandler;
import com.iauto.wlink.core.session.Session;
import com.lmax.disruptor.EventHandler;

/**
 * 实现一个事件处理器，处理接收到的终端消息。 <br/>
 * 该类创建一个责任链，在责任链中依次增加消息处理器。
 * 
 * @author xiaofei.xu
 * 
 */
public class MessageEventHandler implements EventHandler<MessageEvent> {

	/** logger */
	private final static Logger logger = LoggerFactory.getLogger( MessageEventHandler.class );

	private final ExecutorService exectors = Executors.newFixedThreadPool( 10,
		new DefaultThreadFactory( "comm-message-handler" ) );

	/** 处理责任链 */
	private MessageHandler chain = null;

	/** 错误消息编解码器 */
	private MessageCodec<ErrorMessage> errorMessageCodec = new ProtoErrorMessageCodec();

	public MessageEventHandler( final MessageHandler chain ) {
		this.chain = chain;
	}

	/**
	 * 处理接收到终端消息的事件
	 */
	public void onEvent( final MessageEvent event, long sequence, boolean endOfBatch ) throws Exception {

		final Session session = event.getSession();
		final CommunicationMessage message = event.getMessage();

		// info log
		logger.info( "Processing a communication message. type: {}, endOfBatch: {}", message.type(), endOfBatch );

		// 提交给线程池处理。
		// 否则消息处理器的处理时间会阻塞消费者线程

		exectors.execute( new Runnable() {
			@Override
			public void run() {
				try {
					chain.handleMessage( session, message );
				} catch ( MessageProcessException ex ) {
					// info log
					logger.info( "A message process error occured. Code: {}", ex.getErrorCode() );

					ErrorMessage errorMsg = new ErrorMessage( ex.getErrorCode() );
					CommunicationMessage commMessage = new CommunicationMessage(
						MessageType.Error, errorMessageCodec.encode( errorMsg ) );

					session.send( commMessage );
				}
			}
		} );
	}

	// ===================================================================
	// setter/getter

	public void setErrorMessageCodec( MessageCodec<ErrorMessage> errorMessageCodec ) {
		this.errorMessageCodec = errorMessageCodec;
	}

}
