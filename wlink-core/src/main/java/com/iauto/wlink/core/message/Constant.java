package com.iauto.wlink.core.message;

public class Constant {

	/** 消息类型 */
	public class MessageType {
		public static final String Session = "session";
		public static final String Auth = "auth";
		public static final String Message = "message";
	}

	public enum SessionCodecEnv {
		Client,
		Server
	}

}
