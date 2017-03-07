package com.iauto.wlink.core.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 该类实现默认的会话管理器。<br/>
 * 
 * @author xiaofei.xu
 * 
 */
public class DefaultSessionManager implements SessionManager {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( DefaultSessionManager.class );

	/** 会话容器 */
	private final Map<String, UIDSessionList> sessions = new HashMap<String, UIDSessionList>();

	/** 会话活动监听器 */
	private List<SessionListener> sessionListeners = new ArrayList<SessionListener>();

	/** 读写锁 */
	private final ReadWriteLock readwritelock = new ReentrantReadWriteLock();
	private final Lock readLock = readwritelock.readLock();
	private final Lock writeLock = readwritelock.writeLock();

	/** UID的序列号 */
	private AtomicLong uidSequence = new AtomicLong( 0 );

	/** 会话个数（同一个UID可能有多个会话） */
	private AtomicLong sessionTotal = new AtomicLong( 0 );

	@Override
	public List<Session> get( String uid ) {

		// debug log
		logger.debug( "UID: {}", uid );

		// check
		if ( StringUtils.isEmpty( uid ) ) {
			return null;
		}

		// 获取读锁
		readLock.lock();

		try {

			UIDSessionList sessionList = sessions.get( uid );

			if ( sessionList != null && sessionList.getSessions() != null
					&& sessionList.getSessions().size() > 0 ) {
				return sessionList.getSessions();
			}
		}
		finally {
			// 释放读锁
			readLock.unlock();
		}

		return null;
	}

	public Session get( final String uid, final String id ) {

		// check
		if ( StringUtils.isEmpty( uid ) || StringUtils.isEmpty( id ) ) {
			return null;
		}

		// 获取读锁
		readLock.lock();

		Session ret = null;

		try {

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
		finally {
			// 释放读锁
			readLock.unlock();
		}
	}

	@Override
	public Collection<UIDSessionList> getAll() {

		// 获取读锁
		readLock.lock();

		try {
			return sessions.values();
		}
		finally {
			// 释放读锁
			readLock.unlock();
		}
	}

	public void add( final Session session ) {

		// debug log
		logger.debug( "Adding a session. uid:{}, id:{}", session.getUid(), session.getId() );

		// check
		if ( session == null
				|| StringUtils.isEmpty( session.getUid() )
				|| StringUtils.isEmpty( session.getId() ) ) {
			throw new IllegalArgumentException();
		}

		// 获取写锁
		writeLock.lock();

		long sequence = -1;

		try {

			UIDSessionList uidsessionList = sessions.get( session.getUid() );
			if ( uidsessionList == null ) {

				// debug log
				logger.debug( "Session list of uid:{} is not exist.", session.getUid() );

				uidsessionList = new UIDSessionList();

				// 发放UID的序列号，只有新的UID被增加时才发放
				sequence = uidSequence.incrementAndGet();
				uidsessionList.setSequence( sequence );
				uidsessionList.setUid( session.getUid() );

				List<Session> sessionList = new ArrayList<Session>();
				uidsessionList.setSessions( sessionList );
				sessionList.add( session );

				// 放到Map中
				sessions.put( session.getUid(), uidsessionList );
			}
			else {
				// 使用已发放的序列号
				sequence = uidsessionList.getSequence();

				// debug log
				logger.debug( "Session list of uid:{} is exist", session.getUid() );

				// 添加到即存数组中
				if ( !uidsessionList.getSessions().contains( session ) )
					uidsessionList.getSessions().add( session );
			}

			// debug log
			logger.debug( "uid:{}, sequence of uid:{}", session.getUid(), sequence );

			// 增加会话总数
			long total = sessionTotal.incrementAndGet();

			// info log
			logger.info( "a session was added to session manager. uid:{}, id:{}, total:{}",
					session.getUid(), session.getId(), total );

			int remain = uidsessionList.getSessions().size();
			for ( SessionListener listener : sessionListeners ) {
				listener.onCreated( session, sequence, remain );
			}

			// debug log
			logger.debug( "send onCreated event to all session listeners." );
		}
		finally {

			// 释放写锁
			writeLock.unlock();
		}
	}

	public Session remove( final String uid, final String id ) {

		// debug log
		logger.debug( "removing a session from session manager. uid:{}, id:{}", uid, id );

		// check
		if ( StringUtils.isEmpty( uid ) || StringUtils.isEmpty( id ) ) {
			return null;
		}

		// 获取写锁
		writeLock.lock();

		try {

			UIDSessionList sessionList = sessions.get( uid );

			if ( sessionList == null || sessionList.getSessions() == null
					|| sessionList.getSessions().size() == 0 ) {
				// debug log
				logger.debug( "session is not exist, return null." );
				return null;
			}

			for ( Session session : sessionList.getSessions() ) {
				if ( StringUtils.equals( id, session.getId() ) ) {

					// debug log
					logger.debug( "session is exist, remove it." );

					// 删除会话
					sessionList.getSessions().remove( session );

					// 减少会话总数
					long total = sessionTotal.decrementAndGet();

					// info log
					logger.info( "a session was removed from session manager. uid:{}, id:{}, remain:{}",
							session.getUid(), session.getId(), total );

					// 当前UID对应剩余的会话数
					int remain = sessionList.getSessions().size();
					for ( SessionListener listener : sessionListeners ) {
						listener.onRemoved( session, sessionList.getSequence(), remain );
					}

					// debug log
					logger.debug( "send onRemoved event to all session listeners." );

					// 如果UID对应的会话数为0，则删除MAP映射
					if ( remain == 0 ) {

						// debug log
						logger.debug( "list of session is empty, remove the list." );

						sessions.remove( uid );
					}

					return session;
				}
			}
		}
		finally {
			// 释放写锁
			writeLock.unlock();
		}

		return null;
	}

	@Override
	public ReadWriteLock getLock() {
		return this.readwritelock;
	}

	public void addListener( final SessionListener listener ) {
		this.sessionListeners.add( listener );
	}

	// ===========================================================================
	// setter/getter

	public void setSessionListeners( List<SessionListener> sessionListeners ) {
		this.sessionListeners = sessionListeners;
	}
}
