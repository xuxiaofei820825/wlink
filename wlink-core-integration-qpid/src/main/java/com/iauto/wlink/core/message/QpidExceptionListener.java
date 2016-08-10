package com.iauto.wlink.core.message;

import io.netty.channel.ChannelHandlerContext;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.message.event.QpidDisconnectEvent;

public class QpidExceptionListener implements ExceptionListener {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	private final ChannelHandlerContext ctx;
	private final String url;

	public QpidExceptionListener( final ChannelHandlerContext ctx, final String url ) {
		this.ctx = ctx;
		this.url = url;
	}

	public void onException( JMSException exception ) {
		// 显示错误日志，提醒运维尽快恢复环境
		logger.error( "Connection exception occoured! Caused by:{}", exception.getMessage() );

		ctx.fireUserEventTriggered( new QpidDisconnectEvent( url ) );
	}
}
