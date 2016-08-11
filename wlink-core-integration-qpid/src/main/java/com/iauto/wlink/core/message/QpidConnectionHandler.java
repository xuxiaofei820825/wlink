package com.iauto.wlink.core.message;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.qpid.client.AMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检查当前I/O线程是否已有QPID连接，如果没有，则新建
 * 
 * @author xiaofei.xu
 * 
 */
public class QpidConnectionHandler extends ChannelInboundHandlerAdapter {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	/** 连接用URL */
	private final String url;

	public QpidConnectionHandler( String url ) {
		this.url = url;
	}

	@Override
	public void channelActive( ChannelHandlerContext ctx ) throws Exception {
		// 获取当前I/O线程的连接
		AMQConnection conn = QpidConnectionManager.get();

		if ( conn == null ) {
			// 如果未建立，则为当前I/O线程创建一个连接
			newConnection( ctx, url );
		}

		// 流转
		ctx.fireChannelActive();
	}

	/*
	 * 创建新的QPID服务连接
	 */
	private void newConnection( final ChannelHandlerContext ctx, final String url ) throws Exception {
		// info
		logger.info( "Connection of current I/O thread is not exist, create a connection first!" );

		// 与broker创建一个连接
		AMQConnection conn = new AMQConnection( url );

		// 设置异常处理器
		conn.setExceptionListener( new ExceptionListener() {
			public void onException( JMSException exception ) {
				// error
				logger.error( "Error occoured. Caused by:{}",
					exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage() );
				ctx.fireExceptionCaught( exception );
			}
		} );

		// 创建连接
		conn.start();

		// 添加到连接管理管理
		QpidConnectionManager.add( conn );
	}
}
