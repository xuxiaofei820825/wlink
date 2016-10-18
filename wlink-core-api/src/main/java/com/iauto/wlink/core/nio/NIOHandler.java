package com.iauto.wlink.core.nio;

public interface NIOHandler {
	void write( String sessionid, byte[] message );
}
