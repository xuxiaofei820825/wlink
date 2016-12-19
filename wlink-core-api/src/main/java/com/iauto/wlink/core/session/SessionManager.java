package com.iauto.wlink.core.session;

public interface SessionManager {
	Session get( String id );

	void add( String id );

	Session remove( String id );
}
