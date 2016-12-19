package com.iauto.wlink.core.message;

import java.util.List;

import com.iauto.wlink.core.message.handler.AbstractMessageHandler;
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
	private AbstractMessageHandler chain = null;

	public MessageEventHandler( List<AbstractMessageHandler> handlers ) {

		if ( handlers == null || handlers.size() == 0 )
			throw new IllegalArgumentException( "Message handler list is required." );

		// 构建责任链
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
		this.chain.handle( event.getSession(), event.getMessage() );
	}
}
