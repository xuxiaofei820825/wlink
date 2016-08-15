package com.iauto.wlink.core.message;

import com.iauto.wlink.core.AbstractMessage;

public abstract class AbstractPointToPointMessage<T> extends AbstractMessage<T>
		implements PointToPointMessage<T> {

	/** 发送者ID */
	private final long from;

	/** 接收者ID */
	private final long to;

	public AbstractPointToPointMessage( String type, T payload, long from, long to ) {
		// super
		super( type, payload );

		this.from = from;
		this.to = to;
	}

	public long from() {
		return this.from;
	}

	public long to() {
		return this.to;
	}
}
