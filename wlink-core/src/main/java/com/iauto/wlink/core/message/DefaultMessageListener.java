package com.iauto.wlink.core.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.DefaultThreadFactory;
import com.iauto.wlink.core.session.Session;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class DefaultMessageListener implements MessageListener {

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( DefaultMessageListener.class );

	/** size of ring buffer */
	private static final int size = 2048;

	/** ring buffer */
	private final RingBuffer<MessageEvent> ringBuffer;

	@SuppressWarnings("unchecked")
	public DefaultMessageListener( EventHandler<MessageEvent> eventHandler ) {
		Disruptor<MessageEvent> disruptor = new Disruptor<MessageEvent>( new MessageEventFactory(), size,
			new DefaultThreadFactory( "disruptor-consumer" ) );

		if ( eventHandler == null ) {
			throw new IllegalArgumentException( "MessageEvent handler is required." );
		}

		disruptor.handleEventsWith( eventHandler );

		// start
		ringBuffer = disruptor.start();
	}

	public void onMessage( Session session, CommunicationMessage message ) {

		// info log
		logger.info( "Push communication message to ring buffer. type:{}", message.type() );

		// 获取下一个序列号
		long sequence = ringBuffer.next();
		try {
			// 根据序列号获取预分配的数据槽
			MessageEvent event = ringBuffer.get( sequence );
			event.setSession( session );
			event.setMessage( message );
		} finally {
			ringBuffer.publish( sequence );
		}
	}

	// ==========================================================================
	// private classes

	private class MessageEventFactory implements EventFactory<MessageEvent> {
		public MessageEvent newInstance() {
			return new MessageEvent();
		}
	}
}
