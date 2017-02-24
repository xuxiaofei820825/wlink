package com.iauto.wlink.core.session;

import com.iauto.wlink.core.message.CommunicationMessage;

public abstract class AbstractSession implements Session {

	protected String id;
	protected String tuid;
	protected long expireTime;

	public void setId( String id ) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setUid( String tuid ) {
		this.tuid = tuid;
	}

	public String getUid() {
		return this.tuid;
	}

	public void setExpiredTime( long expireTime ) {
		this.expireTime = expireTime;
	}

	public long getExpireTime() {
		return this.expireTime;
	}

	abstract public boolean isAuthenticated();

	abstract public void send( CommunicationMessage message );
}
