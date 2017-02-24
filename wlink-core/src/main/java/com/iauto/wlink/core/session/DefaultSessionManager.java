package com.iauto.wlink.core.session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 该类实现默认的会话管理器。<br/>
 * 使用ConcurrentHashMap作为会话实例的容器，支持多线程操作。
 * 
 * @author xiaofei.xu
 * 
 */
public class DefaultSessionManager implements SessionManager {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( DefaultSessionManager.class );

	/** 会话容器 */
	private final ConcurrentHashMap<String, UIDSessionList> sessions = new ConcurrentHashMap<String, UIDSessionList>();

	/** 会话监听器 */
	private List<SessionListener> sessionListeners = new ArrayList<SessionListener>();

	/** UID的序列号 */
	private AtomicLong tuidSequence = new AtomicLong( 0 );

	/** 会话个数（同一个UID可能有多个会话） */
	private AtomicLong sessionTotal = new AtomicLong( 0 );

	public Session get( final String uid, final String id ) {

		// check
		if ( StringUtils.isEmpty( uid ) || StringUtils.isEmpty( id ) ) {
			return null;
		}

		Session ret = null;
		UIDSessionList sessionList = sessions.get( uid );

		if ( sessionList != null && sessionList.getSessions() != null
				&& sessionList.getSessions().size() > 0 ) {
			for ( Session session : sessionList.getSessions() ) {
				if ( StringUtils.equals( id, session.getId() ) )
					ret = session;
			}
		}
		return ret;
	}

	public void add( final Session session ) {
		// check
		if ( session == null
				|| StringUtils.isEmpty( session.getUid() )
				|| StringUtils.isEmpty( session.getId() ) ) {
			throw new IllegalArgumentException();
		}

		// info log
		logger.info( "Add a session to session manager. uid:{}, id:{}", session.getUid(), session.getId() );

		long sequence = -1;

		UIDSessionList uidsessionList = sessions.get( session.getUid() );
		if ( uidsessionList == null ) {

			uidsessionList = new UIDSessionList();

			// 发放UID的序列号，只有新的UID被增加时才发放
			sequence = tuidSequence.incrementAndGet();
			uidsessionList.setSequence( sequence );

			List<Session> sessionList = new ArrayList<Session>();
			uidsessionList.setSessions( sessionList );
			sessionList.add( session );

			// 放到Map中
			sessions.put( session.getUid(), uidsessionList );
		}
		else {
			// 使用已发放的序列号
			sequence = uidsessionList.getSequence();

			// 添加到即存数组中
			uidsessionList.getSessions().add( session );
		}

		// 增加会话总数
		long total = sessionTotal.incrementAndGet();

		// info log
		logger.info( "Total of session:{}, sequence of tuid:{}", total, sequence );

		int remain = uidsessionList.getSessions().size();
		for ( SessionListener listener : sessionListeners ) {
			listener.onCreated( session, sequence, remain );
		}
	}

	public Session remove( final String uid, final String id ) {

		// check
		if ( StringUtils.isEmpty( uid ) || StringUtils.isEmpty( id ) ) {
			return null;
		}

		// info log
		logger.info( "Remove a session from session manager. tuid:{}, id:{}", uid, id );

		UIDSessionList sessionList = sessions.get( uid );
		if ( sessionList == null || sessionList.getSessions() == null
				|| sessionList.getSessions().size() == 0 )
			return null;

		for ( Session session : sessionList.getSessions() ) {
			if ( StringUtils.equals( id, session.getId() ) ) {
				sessionList.getSessions().remove( session );

				for ( SessionListener listener : sessionListeners ) {
					listener.onRemoved( session, sessionList.getSequence(),
							sessionList.getSessions().size() );
				}
				return session;
			}
		}

		return null;
	}

	public void addListener( final SessionListener listener ) {
		this.sessionListeners.add( listener );
	}

	public void setSessionListeners( List<SessionListener> sessionListeners ) {
		this.sessionListeners = sessionListeners;
	}
}
