package com.iauto.wlink.core.integration.qpid;

import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;

public abstract class QpidTools {

	public static MessageConsumer createConsumer( final String destUrl, final AMQConnection conn,
			final MessageListener listener ) throws Exception {

		// 创建会话
		Session session = conn.getSessions().size() == 0 ?
				conn.createSession( false, Session.CLIENT_ACKNOWLEDGE ) : conn.getSession( 0 );

		// 设置监听器
		Destination dest = new AMQAnyDestination( destUrl );
		MessageConsumer consumer = session.createConsumer( dest );
		consumer.setMessageListener( listener );

		return consumer;
	}
}
