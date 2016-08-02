package com.iauto.wlink.core.message.router;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.qpid.client.AMQAnyDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.mq.router.MessageSender;

public class QpidMessageSender implements MessageSender {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( getClass() );

	public QpidMessageSender() {
	}

	public void send( final Connection conn, String sender, String receiver, String type, byte[] message ) {

		Session session = null;

		try {

			// info
			logger.info( "Sending a message. from:{}, to:{}, type:{}", sender, receiver, type );

			// 与broker建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

			// 设置连接的节点名，如果不存在该topic节点，则新建一个
			Destination dest = new AMQAnyDestination( "ADDR:wlink.message.topic/" + receiver
					+ ";{create:always, node:{ type:topic }}" );

			// 为指定的节点创建消息发送者
			MessageProducer producer = session.createProducer( dest );

			// 创建字节消息
			BytesMessage msg = session.createBytesMessage();
			msg.writeBytes( message );
			msg.setStringProperty( "from", sender );
			msg.setStringProperty( "to", receiver );
			msg.setStringProperty( "type", type );

			// 发送消息
			producer.send( msg );
		} catch ( Exception ex ) {
			// error
			logger.error( "Failed to send the message!!! Caused by: {}", ex.getMessage() );

		} finally {
			// 关闭会话
			if ( session != null ) {
				try {
					session.close();
				} catch ( JMSException e ) {
					// ignore
				}
			}
		}
	}

	public void notify( final Connection conn, final byte[] message, long user ) {
		Session session = null;

		try {

			// info
			logger.info( "Sending a status change notification of user[ID:{}]", user );

			// 与broker建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

			// 设置连接的节点名，如果不存在该topic节点，则新建一个
			Destination dest = new AMQAnyDestination( "ADDR:wlink.status.topic/" + String.valueOf( user ) );

			// 为指定的节点创建消息发送者
			MessageProducer producer = session.createProducer( dest );

			// 创建字节消息
			BytesMessage msg = session.createBytesMessage();
			msg.writeBytes( message );

			// 发送消息
			producer.send( msg );
		} catch ( Exception ex ) {
			// error
			logger.error( "Failed to send the message!!! Caused by: {}", ex.getMessage() );

		} finally {
			// 关闭会话
			if ( session != null ) {
				try {
					session.close();
				} catch ( JMSException e ) {
					// ignore
				}
			}
		}
	}
}
