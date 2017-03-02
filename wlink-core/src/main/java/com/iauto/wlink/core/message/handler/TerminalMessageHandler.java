package com.iauto.wlink.core.message.handler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.TerminalMessage;
import com.iauto.wlink.core.message.TerminalMessageRouter;
import com.iauto.wlink.core.message.codec.ProtoTerminalMessageCodec;
import com.iauto.wlink.core.session.Session;

/**
 * 终端消息处理器。<br>
 * 接收到终端消息后，使用指定的消息路由器，发送给接收者。
 * 
 * @author xiaofei.xu
 * 
 */
public class TerminalMessageHandler extends AbstractMessageHandler {

	/** logger */
	private static final Logger logger = LoggerFactory.getLogger( TerminalMessageHandler.class );

	/** 终端消息编解码器 */
	private MessageCodec<TerminalMessage> terminalMessageCodec = new ProtoTerminalMessageCodec();

	/** 终端消息转发器 */
	private TerminalMessageRouter terminalMessageRouter;

	@Override
	public void handleMessage( Session session, CommunicationMessage message ) {

		// debug log
		logger.debug( "starting to process the message. type:{}", message.type() );

		if ( !StringUtils.equals( message.type(), MessageType.Terminal ) ) {
			// 判断是否为终端消息

			// 传递给下一个处理器处理
			if ( getNextHandler() != null ) {
				getNextHandler().handleMessage( session, message );
			}

			return;
		}

		// check
		if ( terminalMessageRouter == null )
			throw new IllegalArgumentException( "Terminal message router is required." );

		// 解码终端消息
		TerminalMessage terminalMessage = terminalMessageCodec.decode( message.payload() );

		// info log
		logger.info( "information of the decoded terminal message. type:{}, from:{}, to:{}, payload:{}bytes",
				terminalMessage.type(), terminalMessage.from(), terminalMessage.to(), terminalMessage.payload().length );

		// 转发终端消息
		terminalMessageRouter.send( terminalMessage.type(),
				terminalMessage.from(), terminalMessage.to(),
				terminalMessage.payload() );
	}

	// =============================================================================
	// setter/getter

	public void setTerminalMessageRouter( TerminalMessageRouter terminalMessageRouter ) {
		this.terminalMessageRouter = terminalMessageRouter;
	}

	public void setTerminalMessageCodec( MessageCodec<TerminalMessage> terminalMessageCodec ) {
		this.terminalMessageCodec = terminalMessageCodec;
	}
}
