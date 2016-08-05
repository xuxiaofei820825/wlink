package com.iauto.wlink.core;

public class Constant {

	/** 消息类型 */
	public class MessageType {
		public static final String Session = "basic.session";
		public static final String Auth = "basic.auth";
		public static final String Message = "comm.message";
		public static final String Text = "comm.message.text";
	}

	/** 用户状态 */
	public enum Status {
		OnLine,
		OffLine
	}
}
