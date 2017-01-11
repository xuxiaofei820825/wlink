package com.iauto.wlink.core.message.handler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.session.Session;

public class AbstractMessageHandlerTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private AbstractMessageHandler handler1;

	@Mock
	private AbstractMessageHandler handler2;

	@Mock
	private AbstractMessageHandler handler3;

	@Mock
	private Session session;
	@Mock
	private CommunicationMessage commMessage;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHandle() throws MessageProcessException {

		// 构成处理器链
		when( handler1.getNextHandler() ).thenReturn( handler2 );
		when( handler2.getNextHandler() ).thenReturn( handler3 );

		handler1.handleMessage( session, commMessage );

		verify( handler1 ).handleMessage( session, commMessage );
		verify( handler2 ).handleMessage( session, commMessage );
		verify( handler3 ).handleMessage( session, commMessage );
	}

}
