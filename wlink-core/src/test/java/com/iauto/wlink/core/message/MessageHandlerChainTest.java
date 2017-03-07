package com.iauto.wlink.core.message;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.mockpolicies.Slf4jMockPolicy;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.exception.NoCommMessageHandlerException;
import com.iauto.wlink.core.message.handler.AbstractMessageHandler;
import com.iauto.wlink.core.session.Session;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MessageHandlerChain.class)
@MockPolicy(Slf4jMockPolicy.class)
public class MessageHandlerChainTest {

	private MessageHandlerChain chain;

	private List<AbstractMessageHandler> handlers = new ArrayList<AbstractMessageHandler>();
	private AbstractMessageHandler spyHandler1;
	private AbstractMessageHandler spyHandler2;
	private AbstractMessageHandler spyHandler3;

	private MessageEvent event;
	private Session session;
	private CommunicationMessage commMessage;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		event = Mockito.mock( MessageEvent.class );
		session = Mockito.mock( Session.class );
		commMessage = Mockito.mock( CommunicationMessage.class );

		AbstractMessageHandler handler1 = new AbstractMessageHandler() {
			@Override
			public void handleMessage( Session session, CommunicationMessage message ) throws MessageProcessException {
				this.getNextHandler().handleMessage( session, message );
			}
		};
		AbstractMessageHandler handler2 = new AbstractMessageHandler() {
			@Override
			public void handleMessage( Session session, CommunicationMessage message ) throws MessageProcessException {
				this.getNextHandler().handleMessage( session, message );
			}
		};
		AbstractMessageHandler handler3 = new AbstractMessageHandler() {
			@Override
			public void handleMessage( Session session, CommunicationMessage message ) throws MessageProcessException {
				// 不传递给下一个处理器，模拟被处理了
			}
		};

		spyHandler1 = Mockito.spy( handler1 );
		spyHandler2 = Mockito.spy( handler2 );
		spyHandler3 = Mockito.spy( handler3 );

		// 创建处理器组
		handlers.add( spyHandler1 );
		handlers.add( spyHandler2 );
		handlers.add( spyHandler3 );

		// 模拟MessageEvent的属性值
		Mockito.when( event.getSession() ).thenReturn( session );
		Mockito.when( event.getMessage() ).thenReturn( commMessage );
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMessageHandlerChainThrowsIllegalArgumentException() {
		chain = new MessageHandlerChain( null );
	}

	@Test
	public void testMessageEventHandler() {
		// 测试通讯消息进过了所有的处理器，直到找到合适的处理器(spyHandler3)

		chain = new MessageHandlerChain( handlers );

		// 验证是否构成了处理链
		verify( spyHandler1 ).setNextHandler( spyHandler2 );
		verify( spyHandler2 ).setNextHandler( spyHandler3 );

		// 测试
		chain.handleMessage( session, commMessage );

		verify( spyHandler1 ).handleMessage( session, commMessage );
		verify( spyHandler2 ).handleMessage( session, commMessage );
		verify( spyHandler3 ).handleMessage( session, commMessage );
	}

	@Test(expected = NoCommMessageHandlerException.class)
	public void testNoCommMessageHandlerException() {
		// 测试没有合适通讯消息处理器处理消息，抛出对应的异常

		handlers.clear();
		handlers.add( spyHandler1 );
		handlers.add( spyHandler2 );

		chain = new MessageHandlerChain( handlers );

		// 测试
		chain.handleMessage( session, commMessage );
	}
}
