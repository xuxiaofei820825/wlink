package com.iauto.wlink.core.message;

import java.util.List;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.message.codec.ProtoErrorMessageCodec;
import com.iauto.wlink.core.message.handler.AbstractMessageHandler;
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

	/** 处理责任链 */
	private MessageHandler chain = null;

	/** 错误消息编解码器 */
	private MessageCodec<ErrorMessage> errorMessageCodec = new ProtoErrorMessageCodec();

	public MessageEventHandler( List<AbstractMessageHandler> handlers ) {

		if ( handlers == null || handlers.size() == 0 )
			throw new IllegalArgumentException( "Message handler list is required." );

		// 根据List中的顺序构建责任链
		for ( int cnt = 0; cnt < handlers.size(); cnt++ ) {
			if ( cnt == 0 ) {
				this.chain = handlers.get( 0 );
			} else {
				handlers.get( cnt - 1 ).setNextHandler( handlers.get( cnt ) );
			}
		}
	}

	/**
	 * 处理接收到终端消息的事件
	 */
	public void onEvent( MessageEvent event, long sequence, boolean endOfBatch ) throws Exception {

		Session session = event.getSession();

		try {
			this.chain.handleMessage( session, event.getMessage() );
		} catch ( MessageProcessException ex ) {

			session.send( new CommunicationMessage(
				MessageType.Error,
				errorMessageCodec.encode( new ErrorMessage( ex.getErrorCode() ) ) ) );
		}
	}
}
