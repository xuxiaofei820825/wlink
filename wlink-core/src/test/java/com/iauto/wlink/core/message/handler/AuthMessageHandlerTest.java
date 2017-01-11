package com.iauto.wlink.core.message.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.iauto.wlink.core.Constant.MessageType;
import com.iauto.wlink.core.auth.AuthenticationProvider;
import com.iauto.wlink.core.auth.TicketAuthentication;
import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.exception.UnAuthenticatedException;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.SessionMessage;
import com.iauto.wlink.core.message.TicketAuthMessage;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionSignHandler;

public class AuthMessageHandlerTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private AuthenticationProvider authProvider;
	@Mock
	private Session session;
	@Mock
	private CommunicationMessage message;
	@Mock
	private MessageCodec<TicketAuthMessage> authMessageCodec;
	@Mock
	private MessageCodec<SessionMessage> sessionMessageCodec;
	@Mock
	private SessionSignHandler sessionSignHandler;

	@InjectMocks
	private AuthMessageHandler authMessageHandler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		// 认证消息
		when( message.type() ).thenReturn( MessageType.Auth );

		TicketAuthMessage ticketAuthMessage = new TicketAuthMessage( "TK_AAAAA" );
		// 模拟解码
		when( authMessageCodec.decode( message.payload() ) )
			.thenReturn( ticketAuthMessage );

		TicketAuthentication ticketAuthentication = new TicketAuthentication( "TK_AAAAA" );
		ticketAuthentication.setPrincipal( 10000 );

		// 模拟认证
		when( authProvider.authenticate( Mockito.any( TicketAuthentication.class ) ) )
			.thenReturn( ticketAuthentication );
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = UnAuthenticatedException.class)
	public void testUnAuthenticatedException() throws MessageProcessException {

		// 非认证消息
		when( message.type() ).thenReturn( "AAAAA" );
		when( session.isAuthenticated() ).thenReturn( false );

		authMessageHandler.handleMessage( session, message );
	}

	@Test
	public void testNextHandler() throws MessageProcessException {

		// 非认证消息
		when( message.type() ).thenReturn( "AAAAA" );
		when( session.isAuthenticated() ).thenReturn( true );

		// 设置下一个处理器
		AbstractMessageHandler nextHandler = mock( AbstractMessageHandler.class );
		authMessageHandler.setNextHandler( nextHandler );

		// 测试
		authMessageHandler.handleMessage( session, message );

		// 验证消息处理传递到下一个处理器
		verify( nextHandler ).handleMessage( session, message );
		// 验证没有继续进行处理
		verify( authMessageCodec, Mockito.times( 0 ) ).decode( message.payload() );
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAuthProviderException() throws MessageProcessException {
		// 设置认证处理器为空
		authMessageHandler.setAuthProvider( null );

		// 测试
		authMessageHandler.handleMessage( session, message );
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullSessionSignHandlerException() throws MessageProcessException {
		// 设置认证处理器为空
		authMessageHandler.setSessionSignHandler( null );

		// 测试
		authMessageHandler.handleMessage( session, message );
	}

	@Test
	public void testHandleMessage() throws Exception {
		// 正常CASE测试

		// 测试
		authMessageHandler.handleMessage( session, message );

		// 验证调用了解码
		verify( authMessageCodec ).decode( message.payload() );
		// 验证调用了认证
		verify( authProvider ).authenticate( Mockito.any( TicketAuthentication.class ) );

		// 验证会话被设置了值
		verify( session ).setTUId( String.valueOf( 10000 ) );
		// 验证响应被发回客户端
		verify( session ).send( Mockito.any( CommunicationMessage.class ) );

		whenNew( CommunicationMessage.class ).withArguments( "", new byte[] {} ).thenReturn( null );
	}
}
