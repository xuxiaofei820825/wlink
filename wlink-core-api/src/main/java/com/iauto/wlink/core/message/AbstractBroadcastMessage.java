package com.iauto.wlink.core.message;

import com.iauto.wlink.core.AbstractMessage;

public abstract class AbstractBroadcastMessage<T> extends AbstractMessage<T> implements BroadcastMessage<T> {

	private final long from;

	public AbstractBroadcastMessage( String type, T layload, long from ) {
		super( type, layload );

		this.from = from;
	}

	public long from() {
		return this.from;
	}

}
