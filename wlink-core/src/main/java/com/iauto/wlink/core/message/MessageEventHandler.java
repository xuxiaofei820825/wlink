package com.iauto.wlink.core.message;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.message.codec.ProtoErrorMessageCodec;
import com.iauto.wlink.core.message.handler.AbstractMessageHandler;
import com.iauto.wlink.core.message.handler.MessageHandler;
import com.iauto.wlink.core.session.Session;
import com.lmax.disruptor.EventHandler;

/**
 * 实现一个事件处理器，处理接收到的终端消息。 <br/>
 * 该类创建一个责任链，在责任链中依次增加消息处理器。
 * 
 * @author xiaofei.xu
 * 
 */
public class MessageEventHandler implements EventHandler<MessageEvent> {

	private ExecutorService exectors = Executors.newFixedThreadPool( 10, new CommMessageThreadFactory() );

	/** logger */
	private final Logger logger = LoggerFactory.getLogger( MessageEventHandler.class );

	/** 处理责任链 */
	private MessageHandler chain = null;

	/** 错误消息编解码器 */
	private MessageCodec<ErrorMessage> errorMessageCodec = new ProtoErrorMessageCodec();

	public MessageEventHandler( List<AbstractMessageHandler> handlers ) {

		if ( handlers == null || handlers.size() == 0 )
			throw new IllegalArgumentException( "Message handler list is required." );

		// 根据List中的顺序构建责任链
		for ( int cnt = 0; cnt < handlers.size(); cnt++ ) {
			if ( cnt == 0 ) {
				this.chain = handlers.get( 0 );
			} else {
				handlers.get( cnt - 1 ).setNextHandler( handlers.get( cnt ) );
			}
		}
	}

	/**
	 * 处理接收到终端消息的事件
	 */
	public void onEvent( final MessageEvent event, long sequence, boolean endOfBatch ) throws Exception {

		final Session session = event.getSession();
		final CommunicationMessage message = event.getMessage();

		// 提交给线程池处理。
		// 否则消息处理器的处理时间会阻塞消费者线程

		exectors.execute( new Runnable() {
			@Override
			public void run() {
				try {
					chain.handleMessage( session, message );
				} catch ( MessageProcessException ex ) {
					// info log
					logger.info( "A message process error occured. Code: {}", ex.getErrorCode() );

					ErrorMessage errorMsg = new ErrorMessage( ex.getErrorCode() );
					CommunicationMessage commMessage = new CommunicationMessage(
						MessageType.Error, errorMessageCodec.encode( errorMsg ) );

					session.send( commMessage );
				}
			}
		} );

	}

	// ===================================================================
	// setter/getter

	public void setErrorMessageCodec( MessageCodec<ErrorMessage> errorMessageCodec ) {
		this.errorMessageCodec = errorMessageCodec;
	}

	static class CommMessageThreadFactory implements ThreadFactory {
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger( 1 );
		private final String namePrefix;

		CommMessageThreadFactory() {
			SecurityManager s = System.getSecurityManager();
			group = ( s != null ) ? s.getThreadGroup() :
					Thread.currentThread().getThreadGroup();
			namePrefix = "commMessage" +
					"-thread-";
		}

		public Thread newThread( Runnable r ) {
			Thread t = new Thread( group, r,
				namePrefix + threadNumber.getAndIncrement(),
				0 );
			if ( t.isDaemon() )
				t.setDaemon( false );
			if ( t.getPriority() != Thread.NORM_PRIORITY )
				t.setPriority( Thread.NORM_PRIORITY );
			return t;
		}
	}
}
