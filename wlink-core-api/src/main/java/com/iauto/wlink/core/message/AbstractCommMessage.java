package com.iauto.wlink.core.message;

import com.iauto.wlink.core.AbstractMessage;

public abstract class AbstractCommMessage<T> extends AbstractMessage<T>
		implements CommMessage<T> {

	/** 发送者ID */
	private final long from;

	/** 接收者ID */
	private final long to;

	public AbstractCommMessage( String type, T payload, long from, long to ) {
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
