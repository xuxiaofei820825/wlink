package com.iauto.wlink.core.message.event;

public class QpidDisconnectEvent {

	private final String url;

	public QpidDisconnectEvent( String url ) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

}
