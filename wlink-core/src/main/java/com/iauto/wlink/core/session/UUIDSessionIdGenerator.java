package com.iauto.wlink.core.session;

import java.util.UUID;

public class UUIDSessionIdGenerator implements SessionIdGenerator {

	public String generate() {
		return UUID.randomUUID().toString();
	}
}
