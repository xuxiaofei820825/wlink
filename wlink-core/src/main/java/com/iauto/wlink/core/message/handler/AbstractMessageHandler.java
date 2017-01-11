package com.iauto.wlink.core.message.handler;

import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.session.Session;

public abstract class AbstractMessageHandler implements MessageHandler {

	/** 下一个处理者 */
	private AbstractMessageHandler nextHandler = null;

	/**
	 * 设置下一个处理器
	 * 
	 * @param handler
	 *          处理器
	 */
	public void setNextHandler( AbstractMessageHandler handler ) {
		this.nextHandler = handler;
	}

	public AbstractMessageHandler getNextHandler() {
		return this.nextHandler;
	}

	/**
	 * 实现处理逻辑
	 * 
	 * @param session
	 *          会话对象
	 * @param message
	 *          通讯消息
	 * @return true:已处理结束，false:未处理，传递到链的下一个处理器
	 */
	public abstract void handleMessage( Session session, CommunicationMessage message )
			throws MessageProcessException;
}
