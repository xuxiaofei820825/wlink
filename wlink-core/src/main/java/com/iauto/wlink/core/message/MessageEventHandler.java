package com.iauto.wlink.core.message;

import com.iauto.wlink.core.message.handler.AbstractMessageHandler;
import com.iauto.wlink.core.message.handler.AuthMessageHandler;
import com.iauto.wlink.core.message.handler.TerminalMessageHandler;
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

	public MessageEventHandler() {
		// 构建责任链
		this.chain = new AuthMessageHandler();
		AbstractMessageHandler terminalMessageHandler = new TerminalMessageHandler();
		this.chain.setNextHandler( terminalMessageHandler );
	}

	/**
	 * 处理接收到终端消息的事件
	 */
	public void onEvent( MessageEvent event, long sequence, boolean endOfBatch ) throws Exception {
		this.chain.handle( event.getSession(), event.getMessage() );
	}
}
