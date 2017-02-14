package com.iauto.wlink.core.session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

/**
 * 该类实现默认的会话管理器。<br/>
 * 使用ConcurrentHashMap作为会话实例的容器，支持多线程操作。
 * 
 * @author xiaofei.xu
 * 
 */
public class DefaultSessionManager implements SessionManager {

	/** 会话容器 */
	private final ConcurrentHashMap<String, List<Session>> sessions = new ConcurrentHashMap<String, List<Session>>();

	/** 会话监听器 */
	private List<SessionListener> sessionListeners = new ArrayList<SessionListener>();

	public Session get( final String tuid, final String id ) {
		Session ret = null;
		List<Session> sessionList = sessions.get( tuid );
		if ( sessionList != null && sessionList.size() > 0 ) {
			for ( Session session : sessionList ) {
				if ( StringUtils.equals( id, session.getId() ) )
					ret = session;
			}
		}
		return ret;
	}

	public void add( final Session session ) {

		List<Session> sessionList = sessions.get( session.getTUId() );
		if ( sessionList == null ) {
			sessionList = new ArrayList<Session>();
			sessionList.add( session );
			sessions.put( session.getTUId(), sessionList );
		} else {
			sessionList.add( session );
		}

		for ( SessionListener listener : sessionListeners ) {
			listener.onCreated( session );
		}
	}

	public Session remove( final String tuid, final String id ) {

		List<Session> sessionList = sessions.get( tuid );
		if ( sessionList == null || sessionList.size() == 0 )
			return null;

		for ( Session session : sessionList ) {
			if ( StringUtils.equals( id, session.getId() ) ) {
				sessionList.remove( session );

				for ( SessionListener listener : sessionListeners ) {
					listener.onRemoved( session );
				}
				return session;
			}
		}

		return null;
	}

	public void addListener( final SessionListener listener ) {
		this.sessionListeners.add( listener );
	}
}
