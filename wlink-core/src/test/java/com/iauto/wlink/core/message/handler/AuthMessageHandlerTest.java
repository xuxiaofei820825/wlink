package com.iauto.wlink.core.message.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

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

@PrepareForTest(AuthMessageHandler.class)
public class AuthMessageHandlerTest {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	private AuthenticationProvider authProvider;
	private Session session;
	private CommunicationMessage message;
	private MessageCodec<TicketAuthMessage> authMessageCodec;
	private MessageCodec<SessionMessage> sessionMessageCodec;
	private SessionSignHandler sessionSignHandler;
	private AuthMessageHandler authMessageHandler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {

		// 创建需要测试的实例
		authMessageHandler = new AuthMessageHandler();

		// 模拟对象
		session = mock( Session.class );
		message = mock( CommunicationMessage.class );

		authMessageCodec = mock( MessageCodec.class );
		authProvider = mock( AuthenticationProvider.class );
		sessionMessageCodec = mock( MessageCodec.class );
		sessionSignHandler = mock( SessionSignHandler.class );

		// 使用模拟对象设置测试对象的依赖
		authMessageHandler.setAuthProvider( authProvider );
		authMessageHandler.setAuthMessageCodec( authMessageCodec );
		authMessageHandler.setSessionSignHandler( sessionSignHandler );
		authMessageHandler.setSessionMessageCodec( sessionMessageCodec );

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

		// ============ 1.模拟 ============

		// 当使用无参数构造函数创建SessionMessage时，返回模拟对象
		SessionMessage sessionMessage = mock( SessionMessage.class );
		whenNew( SessionMessage.class ).withNoArguments().thenReturn( sessionMessage );

		CommunicationMessage commMessage = mock( CommunicationMessage.class );
		whenNew( CommunicationMessage.class ).withNoArguments().thenReturn( commMessage );

		// 模拟签名处理器
		String signature = "Signature";
		when( sessionSignHandler.sign( Mockito.anyString(), Mockito.anyString(), Mockito.anyLong() ) )
			.thenReturn( signature );

		// 模拟会话的返回值
		String sessionId = "SESSION_ID";
		long expireTime = System.currentTimeMillis() + 1000;
		when( session.getId() ).thenReturn( sessionId );
		when( session.getExpireTime() ).thenReturn( expireTime );

		byte[] sessionBytes = new byte[] {};
		when( sessionMessageCodec.encode( sessionMessage ) ).thenReturn( sessionBytes );

		// ============ 2.测试 ============

		authMessageHandler.handleMessage( session, message );

		// ============ 3.验证 ============

		// 验证调用了解码
		verify( authMessageCodec ).decode( message.payload() );
		// 验证调用了认证
		verify( authProvider ).authenticate( any( TicketAuthentication.class ) );

		// 验证会话被设置了值
		verify( session ).setTUId( String.valueOf( 10000 ) );

		// 验证会话消息被设置了正确的值
		verify( sessionMessage ).setTuid( String.valueOf( 10000 ) );
		verify( sessionMessage ).setSignature( signature );
		verify( sessionMessage ).setId( sessionId );
		verify( sessionMessage ).setExpireTime( expireTime );

		// 验证SessionMessage的编码函数被调用了
		verify( sessionMessageCodec ).encode( sessionMessage );

		// 验证commMessage被设置了正确的值
		verify( commMessage ).setType( MessageType.Session );
		verify( commMessage ).setPayload( sessionBytes );

		// 验证会话响应被发回客户端
		verify( session ).send( commMessage );
	}
}