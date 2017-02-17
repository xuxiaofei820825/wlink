package com.iauto.wlink.core.message;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.message.handler.AbstractMessageHandler;
import com.iauto.wlink.core.session.Session;

@PrepareForTest(MessageEventHandler.class)
public class MessageEventHandlerTest {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	private MessageEventHandler messageEventHandler;
	private MessageCodec<ErrorMessage> errorMessageCodec;

	private List<AbstractMessageHandler> handlers = new ArrayList<AbstractMessageHandler>();
	private AbstractMessageHandler handler1;
	private AbstractMessageHandler handler2;
	private AbstractMessageHandler handler3;

	private MessageEvent event;
	private Session session;
	private CommunicationMessage commMessage;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {

		event = Mockito.mock( MessageEvent.class );
		session = Mockito.mock( Session.class );
		commMessage = Mockito.mock( CommunicationMessage.class );
		errorMessageCodec = Mockito.mock( MessageCodec.class );

		handler1 = Mockito.mock( AbstractMessageHandler.class );
		handler2 = Mockito.mock( AbstractMessageHandler.class );
		handler3 = Mockito.mock( AbstractMessageHandler.class );

		// 创建处理器组
		handlers.add( handler1 );
		handlers.add( handler2 );
		handlers.add( handler3 );

		// 模拟MessageEvent的属性值
		Mockito.when( event.getSession() ).thenReturn( session );
		Mockito.when( event.getMessage() ).thenReturn( commMessage );

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMessageEventHandlerThrowsIllegalArgumentException() {
		messageEventHandler = new MessageEventHandler( null );
	}

	@Test
	public void testMessageEventHandler() {

		messageEventHandler = new MessageEventHandler( handlers );

		// 验证是否构成了处理链
		verify( handler1 ).setNextHandler( handler2 );
		verify( handler2 ).setNextHandler( handler3 );
	}

	@Test
	public void testOnEvent() throws Exception {

		messageEventHandler = new MessageEventHandler( handlers );

		// 测试
		messageEventHandler.onEvent( event, 1, false );
		
		verify( handler1 ).handleMessage( session, commMessage );
	}

	@SuppressWarnings("serial")
	@Test
	public void testOnEventThrowsMessageProcessException() throws Exception {

		final String ERROR_CODE = "UN_AUTHENTICATED";

		messageEventHandler = new MessageEventHandler( handlers );
		messageEventHandler.setErrorMessageCodec( errorMessageCodec );

		Mockito.doThrow( new MessageProcessException( ERROR_CODE ) {
		} )
			.when( handler1 ).handleMessage( session, commMessage );

		// 模拟ErrorMessage构建
		ErrorMessage errorMessage = PowerMockito.mock( ErrorMessage.class );
		PowerMockito.whenNew( ErrorMessage.class ).withArguments( ERROR_CODE )
			.thenReturn( errorMessage );

		// 模拟ErrorMessage编码器的返回结果
		byte[] errorMessageBytes = new byte[] {};
		Mockito.doReturn( errorMessageBytes ).when( errorMessageCodec ).encode( errorMessage );

		// 模拟CommunicationMessage构建
		CommunicationMessage commMessageSend = PowerMockito.mock( CommunicationMessage.class );
		PowerMockito.whenNew( CommunicationMessage.class ).withArguments( MessageType.Error, errorMessageBytes )
			.thenReturn( commMessageSend );

		// 2.测试
		messageEventHandler.onEvent( event, 1, false );

		// 3.验证
		// 验证handleMessage被调用了
		verify( handler1 ).handleMessage( session, commMessage );
		// 验证错误响应被发送给终端
		verify( session ).send( commMessageSend );
	}
}
