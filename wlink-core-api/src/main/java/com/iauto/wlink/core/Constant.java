package com.iauto.wlink.core;

public abstract class Constant {

	/** 消息类型 */
	public class MessageType {
		// =======================================================
		// system message type
		public static final String Session = "system.session";
		public static final String Auth = "system.auth";
		public static final String Heartbeat = "system.heartbeat";
		public static final String Error = "system.error";

		public static final String P2PMessage = "terminal.p2p";
		public static final String BroadcastMessage = "terminal.broadcast";
		public static final String Terminal = "terminal.message";
	}
}
