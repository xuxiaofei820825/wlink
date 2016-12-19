package com.iauto.wlink.core.message;

import java.util.concurrent.Executors;

import com.iauto.wlink.core.session.Session;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class DefaultMessageListener implements MessageListener {

	/** size of ring buffer */
	private static final int size = 16384;

	/** ring buffer */
	private final RingBuffer<MessageEvent> ringBuffer;

	@SuppressWarnings("unchecked")
	public DefaultMessageListener() {
		Disruptor<MessageEvent> disruptor = new Disruptor<MessageEvent>( new MessageEventFactory(), size,
			Executors.defaultThreadFactory() );

		EventHandler<MessageEvent> handler = new MessageEventHandler();
		disruptor.handleEventsWith( handler );
		ringBuffer = disruptor.start();
	}

	public void onMessage( Session session, CommunicationMessage message ) {
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
