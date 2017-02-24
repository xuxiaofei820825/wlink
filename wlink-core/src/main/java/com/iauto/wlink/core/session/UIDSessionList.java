package com.iauto.wlink.core.session;

import java.util.List;

public class UIDSessionList {

	/** 系统内唯一识别号 */
	private String uid;

	/** 识别号的序列号 */
	private long sequence;

	/** 识别号对应的所有会话 */
	private List<Session> sessions;

	public String getUid() {
		return uid;
	}

	public void setUid( String uid ) {
		this.uid = uid;
	}

	public long getSequence() {
		return sequence;
	}

	public void setSequence( long sequence ) {
		this.sequence = sequence;
	}

	public List<Session> getSessions() {
		return sessions;
	}

	public void setSessions( List<Session> sessions ) {
		this.sessions = sessions;
	}

}
