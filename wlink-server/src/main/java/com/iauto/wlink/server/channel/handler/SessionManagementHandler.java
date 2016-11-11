package com.iauto.wlink.server.channel.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.iauto.wlink.core.message.TerminalMessageRouter;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.server.Constant;
import com.iauto.wlink.server.channel.SessionManager;

@Sharable
public class SessionManagementHandler extends ChannelInboundHandlerAdapter implements InitializingBean {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	private TerminalMessageRouter router;

	public void afterPropertiesSet() throws Exception {
		Assert.notNull( this.router, "Message router is required." );
	}

	@Override
	public void channelInactive( ChannelHandlerContext ctx ) throws Exception {

		// 判断终端会话是否已经建立
		Session session = ctx.channel().attr( Constant.SessionKey ).get();

		if ( session != null ) {
			String sessionId = session.getId();
			String uuid = session.getUuId();

			Channel channel = SessionManager.remove( uuid, sessionId );

			// log
			if ( logger.isDebugEnabled() && channel != null ) {
				logger.debug( "Succeed to remove session from management. UUID:{}, SID: {}", uuid, sessionId );
			}

			ConcurrentHashMap<String, Channel> channels = SessionManager.get( uuid );
			if ( channels == null || channels.size() == 0 ) {
				router.unsubscribe( uuid );
			}
		}

		ctx.fireChannelInactive();
	}

	// ===========================================================
	// setter/getter

	public void setRouter( TerminalMessageRouter router ) {
		this.router = router;
	}
}
