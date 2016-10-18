package com.iauto.wlink.core.message;

public class DefaultTerminalMessage implements TerminalMessage {

	/** 消息类型 */
	private String type;

	/** 发送者ID */
	private String from;

	/** 接收者ID */
	private String to;

	/** 消息的有效荷载 */
	private byte[] payload;

	public DefaultTerminalMessage( String type, String from, String to, byte[] payload ) {
		this.type = type;
		this.from = from;
		this.to = to;
		this.payload = payload;
	}

	public String type() {
		return this.type;
	}

	public String from() {
		return this.from;
	}

	public String to() {
		return this.to;
	}

	public byte[] payload() {
		return this.payload;
	}
}
