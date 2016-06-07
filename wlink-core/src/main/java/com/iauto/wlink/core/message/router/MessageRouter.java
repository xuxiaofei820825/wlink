package com.iauto.wlink.core.message.router;

public interface MessageRouter {

	/**
	 * 发送消息
	 * 
	 * @param sender
	 *          发送者
	 * @param receiver
	 *          接收者
	 * @param message
	 *          消息体
	 */
	void send( String sender, String receiver, byte[] message ) throws Exception;

	void receive( String receiver );
}
