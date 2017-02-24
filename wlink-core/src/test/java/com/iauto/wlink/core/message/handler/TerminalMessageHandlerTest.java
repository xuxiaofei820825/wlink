package com.iauto.wlink.core.message.handler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.mockpolicies.Slf4jMockPolicy;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.modules.junit4.PowerMockRunner;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.message.DefaultTerminalMessage;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.TerminalMessage;
import com.iauto.wlink.core.message.TerminalMessageRouter;
import com.iauto.wlink.core.session.Session;

@RunWith(PowerMockRunner.class)
@MockPolicy(Slf4jMockPolicy.class)
public class TerminalMessageHandlerTest {

	@Mock
	protected MessageCodec<TerminalMessage> terminalMessageCodec;
	@Mock
	private TerminalMessageRouter terminalMessageRouter;
	@Mock
	protected Session session;

	@InjectMocks
	private TerminalMessageHandler terminalMessageHandler;

	private TerminalMessage terminalMessage;
	private byte[] terminalMessageBytes = new byte[] {};
	private CommunicationMessage commMessage;

	private final static String type = "user.location";
	private final static String from = "10001";
	private final static String to = "10002";
	private final static byte[] terminalPayload = new byte[] {};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		commMessage = new CommunicationMessage();
		commMessage.setPayload( terminalMessageBytes );
		commMessage.setType( MessageType.Terminal );

		// 创建一个TerminalMessage
		terminalMessage = new DefaultTerminalMessage( type, from, to, terminalPayload );

		// 模拟MessageCodec，返回指定的TerminalMessage
		when( terminalMessageCodec.decode( commMessage.payload() ) )
				.thenReturn( terminalMessage );
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentException() throws MessageProcessException {
		// ====================================================
		// 测试如果终端路由器未设置时，抛出异常

		// 终端消息路由器设置NULL
		terminalMessageHandler.setTerminalMessageRouter( null );

		// Test
		terminalMessageHandler.handleMessage( session, commMessage );
	}

	@Test
	public void testNotTerminalMessage() {
		// ====================================================
		// 测试不是终端消息类型时，不做处理

		CommunicationMessage commMessage = new CommunicationMessage();
		commMessage.setPayload( terminalMessageBytes );
		commMessage.setType( MessageType.Session );

		// Test
		terminalMessageHandler.handleMessage( session, commMessage );

		verify( terminalMessageCodec, Mockito.times( 0 ) ).decode( terminalMessageBytes );
	}

	@Test
	public void testHandleMessage() {
		// ====================================================
		// 测试正常业务流程

		// Test
		terminalMessageHandler.handleMessage( session, commMessage );

		verify( terminalMessageCodec ).decode( terminalMessageBytes );
		verify( terminalMessageRouter ).send( type, from, to, terminalPayload );
	}
}
