package com.iauto.wlink.core.integration.rabbitmq;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.message.DefaultTerminalMessage;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.TerminalMessage;
import com.iauto.wlink.core.message.codec.ProtoTerminalMessageCodec;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionManager;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public class TerminalMessageConsumer implements Consumer {

	/** logger */
	private final static Logger logger = LoggerFactory.getLogger( TerminalMessageConsumer.class );

	private MessageCodec<TerminalMessage> terminalMessageCodec = new ProtoTerminalMessageCodec();

	/** 会话管理器 */
	private SessionManager sessionManager;

	@Override
	public void handleDelivery( String consumerTag, Envelope envelope,
			AMQP.BasicProperties properties, byte[] body ) throws IOException {

		Map<String, Object> headers = properties.getHeaders();
		String from = headers.get( "from" ).toString();
		String to = headers.get( "to" ).toString();
		String type = headers.get( "type" ).toString();

		// info log
		logger.info( "a terminal message received. type:{}, from:{}, to:{}, message:{}bytes",
				type, from, to, body.length );

		// check
		if ( sessionManager == null || terminalMessageCodec == null )
			throw new IllegalArgumentException();

		// TODO 这里会产生竟态条件，不能保证send时，session还未被其他线程编辑后改变
		List<Session> sessions = sessionManager.get( to );
		if ( sessions == null || sessions.isEmpty() )
			return;

		// info log
		logger.info( "send termianl message to receiver. receiver:{}", to );

		// 创建终端消息并编码
		TerminalMessage tmsg = new DefaultTerminalMessage( type, from, to, body );
		byte[] payload = terminalMessageCodec.encode( tmsg );

		// 创建通讯消息，并返回给终端
		CommunicationMessage msg = new CommunicationMessage();
		msg.setType( MessageType.Terminal );
		msg.setPayload( payload );

		for ( Session session : sessions ) {
			session.send( msg );
		}
	}

	@Override
	public void handleConsumeOk( String consumerTag ) {

	}

	@Override
	public void handleCancelOk( String consumerTag ) {

	}

	@Override
	public void handleCancel( String consumerTag ) throws IOException {

	}

	@Override
	public void handleShutdownSignal( String consumerTag, ShutdownSignalException sig ) {

	}

	@Override
	public void handleRecoverOk( String consumerTag ) {

	}

	// ===========================================================================
	// setter/getter

	public void setSessionManager( SessionManager sessionManager ) {
		this.sessionManager = sessionManager;
	}

	public void setTerminalMessageCodec( MessageCodec<TerminalMessage> terminalMessageCodec ) {
		this.terminalMessageCodec = terminalMessageCodec;
	}
}
