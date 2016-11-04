package com.iauto.wlink.client;

import com.iauto.wlink.client.exception.AuthenticationException;

public interface WlinkClient {

	void connect() throws Exception;

	void auth( String ticket ) throws AuthenticationException;

	void auth( String id, String userId, long timestamp, String signature ) throws AuthenticationException;

	void sendMessage( String receiver, String type, byte[] body );

	void disconnect() throws Exception;
}
