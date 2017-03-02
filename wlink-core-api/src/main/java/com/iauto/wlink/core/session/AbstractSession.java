package com.iauto.wlink.core.session;

import org.apache.commons.lang.StringUtils;

import com.iauto.wlink.core.message.CommunicationMessage;

public abstract class AbstractSession implements Session {

	protected String id;
	protected String uid;
	protected long expiredTime;

	public void setId( String id ) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setUid( String tuid ) {
		this.uid = tuid;
	}

	public String getUid() {
		return this.uid;
	}

	public void setExpiredTime( long expiredTime ) {
		this.expiredTime = expiredTime;
	}

	public long getExpiredTime() {
		return this.expiredTime;
	}

	@Override
	public boolean equals( Object obj ) {
		if ( obj == null || !( obj instanceof Session ) )
			return false;

		Session tmp = (Session) obj;
		return StringUtils.equals( tmp.getId(), this.id );
	}

	abstract public boolean isAuthenticated();

	abstract public void send( CommunicationMessage message );
}
