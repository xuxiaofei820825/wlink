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

	public String send( final Connection conn, String sender, String receiver, String type, byte[] message ) {

		Session session = null;

		try {

			// info
			logger.info( "Send a message. [from:{}, to:{}]", sender, receiver );

			// 与broker建立Session
			session = conn.createSession( false, Session.AUTO_ACKNOWLEDGE );

			// 设置连接的节点名，如果不存在该topic节点，则新建一个
			Destination dest = new AMQAnyDestination( "ADDR:message.topic/" + receiver
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

			// 获取消息编号
			String msgId = msg.getJMSMessageID().substring( 3 );

			// info
			logger.info( "Succeed to send the message. ID:{}", msgId );

			return msgId;
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

		return null;
	}
}
