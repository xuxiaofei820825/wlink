package com.iauto.wlink.client;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.tools.ReserveAccountTool;

public class SingleClient {

	/** logger */
	private static final Logger logger = LoggerFactory.getLogger( SingleClient.class );

	private static final int port = 2391;
	private static final String TEXT_MESSAGE = "xxxxx";

	public static void main( String[] args ) throws Exception {

		long loginUser = 0;
		String receiver = "";

		// 登录用户
		if ( args.length == 1 )
			loginUser = Long.valueOf( args[0] );

		if ( args.length == 2 ) {
			loginUser = Long.valueOf( args[0] );
			receiver = args[1];
		}

		// info log
		logger.info( "sender:{}, receiver:{}", loginUser, receiver );

		// 创建一个客户端实例
		WlinkClient client = DefaultWlinkClient.newInstance( "localhost", port );

		// 连接并认证客户端身份
		client.connect();
		client.auth( ReserveAccountTool.generate( loginUser ) );

		if ( !StringUtils.isEmpty( receiver ) ) {
			for ( int idx = 1; idx <= 10000; idx++ ) {
				client.sendMessage( receiver, "text", TEXT_MESSAGE.getBytes() );
				Thread.sleep( 5000 );
			}
		}
	}
}
