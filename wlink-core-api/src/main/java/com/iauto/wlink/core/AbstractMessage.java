package com.iauto.wlink.core;

public abstract class AbstractMessage<T> implements Message<T> {

	/** 消息类型 */
	private final String type;

	/** 有效荷载 */
	private final T payload;

	public AbstractMessage( String type, T layload ) {
		this.type = type;
		this.payload = layload;
	}

	public String type() {
		return this.type;
	}

	public T payload() {
		return this.payload;
	}
}
