package com.iauto.wlink.core.message;

import com.google.common.util.concurrent.ListenableFuture;
import com.iauto.wlink.core.exception.MessageRouteException;

public interface TerminalMessageRouter {

	/**
	 * 初始化
	 */
	void init();

	void setMessageReceivedHandler( MessageReceivedHandler messageReceivedHandler );

	/**
	 * 订阅指定用户的消息。
	 * 
	 * @param uuid
	 *          发布者唯一编号
	 * @return {@link ListenableFuture}
	 * @throws MessageRouteException
	 */
//	ListenableFuture<?> subscribe( String uuid, long sequence )
//			throws MessageRouteException;

	/**
	 * 取消消息的订阅
	 * 
	 * @param uuid
	 *          唯一编号
	 */
//	void unsubscribe( String uuid );

	/**
	 * 发送一个点对点消息
	 * 
	 * @param type
	 *          消息类型
	 * @param from
	 *          发送者
	 * @param to
	 *          接收者
	 * @param message
	 *          消息有效荷载
	 * @return {@link ListenableFuture}
	 * @throws MessageRouteException
	 */
	ListenableFuture<?> send( String type, String from, String to, byte[] message ) throws MessageRouteException;

	/**
	 * 广播指定的用户消息
	 * 
	 * @param type
	 *          消息类型
	 * @param from
	 *          发布广播消息的用户
	 * @param message
	 *          消息内容
	 * @return {@link ListenableFuture}
	 * @throws MessageRouteException
	 */
	ListenableFuture<?> broadcast( String type, String from, byte[] message ) throws MessageRouteException;

}
