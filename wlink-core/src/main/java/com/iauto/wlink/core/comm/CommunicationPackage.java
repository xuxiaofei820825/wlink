package com.iauto.wlink.core.comm;

import io.netty.buffer.ByteBuf;

public class CommunicationPackage {

	/** 数据包类型 */
	private String type;

	/** 数据体长度 */
	private int length;

	/** 数据体 */
	private ByteBuf body;

	public CommunicationPackage() {
	}

	// ==========================================================
	// setter/getter

	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength( int length ) {
		this.length = length;
	}

	public ByteBuf getBody() {
		return body;
	}

	public void setBody( ByteBuf body ) {
		this.body = body;
	}

}
