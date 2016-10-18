package com.iauto.wlink.server;

import io.netty.util.AttributeKey;

import com.iauto.wlink.core.session.Session;

public final class Constant {
	/** 会话键值 */
	public static final AttributeKey<Session> SessionKey =
			AttributeKey.newInstance( "session" );
}
