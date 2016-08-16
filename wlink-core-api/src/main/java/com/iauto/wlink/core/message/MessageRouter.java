package com.iauto.wlink.core.message;

import com.google.common.util.concurrent.ListenableFuture;
import com.iauto.wlink.core.exception.MessageRouteException;
import com.iauto.wlink.core.session.SessionContext;

public interface MessageRouter {

	/**
	 * 订阅者订阅发送给自己的消息。
	 * 
	 * @param ctx
	 *          订阅者会话上下文
	 * @return {@link ListenableFuture}
	 * @throws MessageRouteException
	 */
	ListenableFuture<?> subscribe( SessionContext ctx ) throws MessageRouteException;

	/**
	 * 订阅者订阅指定用户的消息。
	 * 
	 * @param ctx
	 *          订阅者会话上下文
	 * @param userId
	 *          被订阅者
	 * @return {@link ListenableFuture}
	 * @throws MessageRouteException
	 */
	ListenableFuture<?> subscribe( SessionContext ctx, long userId ) throws MessageRouteException;

	/**
	 * 发送点对点消息
	 * 
	 * @param message
	 *          消息
	 * @return {@link ListenableFuture}
	 * @throws MessageRouteException
	 */
	ListenableFuture<?> send( PointToPointMessage<byte[]> message ) throws MessageRouteException;

	/**
	 * 广播指定的用户消息
	 * 
	 * @param message
	 *          消息
	 * @return {@link ListenableFuture}
	 * @throws MessageRouteException
	 */
	ListenableFuture<?> broadcast( BroadcastMessage<byte[]> message ) throws MessageRouteException;

	void unregister( SessionContext ctx ) throws MessageRouteException;
}
