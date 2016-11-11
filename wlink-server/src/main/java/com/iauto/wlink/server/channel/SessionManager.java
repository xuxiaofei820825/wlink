package com.iauto.wlink.server.channel;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {

	/**
	 * 系统唯一号与通道的对应表<br/>
	 * 可能存在一个系统唯一号的持有者(如用户)，登入了同一个服务器的情况，所有一个系统唯一号对应一个通道的列表
	 */
	public static ConcurrentHashMap<String, ConcurrentHashMap<String, Channel>> channels =
			new ConcurrentHashMap<String, ConcurrentHashMap<String, Channel>>();

	private SessionManager() {
	}

	public static void add( String uuid, String sessionId, Channel channel ) {
		ConcurrentHashMap<String, Channel> map_channels = channels.get( uuid );
		if ( map_channels == null ) {
			map_channels = new ConcurrentHashMap<String, Channel>();
			channels.put( uuid, map_channels );
		}
		map_channels.put( sessionId, channel );
	}

	public static ConcurrentHashMap<String, Channel> get( String uuid ) {
		return channels.get( uuid );
	}

	public static ConcurrentHashMap<String, Channel> remove( String uuid ) {
		return channels.remove( uuid );
	}

	public static Channel remove( String uuid, String sessionId ) {
		ConcurrentHashMap<String, Channel> map_channels = channels.get( uuid );
		if ( map_channels == null ) {
			return null;
		} else {
			return map_channels.remove( sessionId );
		}
	}
}
