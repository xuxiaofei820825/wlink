package com.iauto.wlink.core.message;

public class CommMessage {

	/** 发送者 */
	private String from;

	/** 接收者 */
	private String to;

	/** 消息类型 */
	private String type;

	/** 消息数据内容 */
	private byte[] body;

	public CommMessage() {
	}

	// ===========================================================
	// setter/getter

	public String getFrom() {
		return from;
	}

	public void setFrom( String from ) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo( String to ) {
		this.to = to;
	}

	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody( byte[] body ) {
		this.body = body;
	}
}
