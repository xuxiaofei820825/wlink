package com.iauto.wlink.core.message.handler;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

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
import com.iauto.wlink.core.exception.ExpiredSessionException;
import com.iauto.wlink.core.exception.InvalidSignatureSessionException;
import com.iauto.wlink.core.exception.MessageProcessException;
import com.iauto.wlink.core.message.CommunicationMessage;
import com.iauto.wlink.core.message.MessageCodec;
import com.iauto.wlink.core.message.SessionMessage;
import com.iauto.wlink.core.session.Session;
import com.iauto.wlink.core.session.SessionSignHandler;

@RunWith(PowerMockRunner.class)
@MockPolicy(Slf4jMockPolicy.class)
public class SessionMessageHandlerTest {

	@Mock
	protected MessageCodec<SessionMessage> sessionMessageCodec;
	@Mock
	protected SessionSignHandler sessionSignHandler;
	@Mock
	protected Session session;

	@InjectMocks
	protected SessionMessageHandler sessionMessageHandler;

	protected SessionMessage sessionMessage;
	protected byte[] sessionMessageBytes = new byte[] {};
	protected CommunicationMessage commMessage;

	/* ====================================================== */
	private String id;
	private String signature;
	private String tuid;
	private long expireTime;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		commMessage = new CommunicationMessage();
		commMessage.setPayload( sessionMessageBytes );
		commMessage.setType( MessageType.Session );

		// 创建一个SessionMessage
		sessionMessage = new SessionMessage();

		this.id = UUID.randomUUID().toString();
		this.signature = "XXXXXX";
		this.tuid = "TUID001";
		this.expireTime = System.currentTimeMillis() + 100000;

		sessionMessage.setId( id );
		sessionMessage.setSignature( signature );
		sessionMessage.setTuid( tuid );
		sessionMessage.setExpireTime( expireTime );

		// 模拟MessageCodec，返回指定的SessionMessage
		when( sessionMessageCodec.decode( commMessage.payload() ) )
			.thenReturn( sessionMessage );
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = InvalidSignatureSessionException.class)
	public void testInvalidSignatureSessionException() throws MessageProcessException {
		// 无效签名验证的测试

		// 模拟抛出异常
		doThrow( InvalidSignatureSessionException.class )
			.when( sessionSignHandler ).validate( Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyString() );

		// Test
		sessionMessageHandler.handleMessage( session, commMessage );
	}

	@Test(expected = ExpiredSessionException.class)
	public void testExpiredSessionException() throws MessageProcessException {
		// 会话过期的测试

		// 模拟过期
		sessionMessage.setExpireTime( System.currentTimeMillis() - 10000 );

		// Test
		sessionMessageHandler.handleMessage( session, commMessage );
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentException() throws MessageProcessException {
		// 会话过期的测试

		// 模拟过期
		sessionMessageHandler.setSessionSignHandler( null );

		// Test
		sessionMessageHandler.handleMessage( session, commMessage );
	}

	@Test
	public void testhandleNotSessionMessage() throws MessageProcessException {
		// 如果不是Session消息，则要下一个处理器处理

		CommunicationMessage commMessage = new CommunicationMessage();
		commMessage.setPayload( sessionMessageBytes );
		commMessage.setType( "NO_SESSION" );

		// 设置下一个处理器
		AbstractMessageHandler nextHandler = mock( AbstractMessageHandler.class );
		sessionMessageHandler.setNextHandler( nextHandler );

		// Test
		sessionMessageHandler.handleMessage( session, commMessage );

		// 验证下一个处理器被调用了
		verify( nextHandler ).handleMessage( session, commMessage );
		verify( session, Mockito.times( 0 ) ).setId( id );
	}

	@Test
	public void testhandleMessage() throws MessageProcessException {
		// 正常CASE的测试

		// 设置下一个处理器
		AbstractMessageHandler nextHandler = mock( AbstractMessageHandler.class );
		sessionMessageHandler.setNextHandler( nextHandler );

		// Test
		sessionMessageHandler.handleMessage( session, commMessage );

		// 验证下一个处理器未被调用
		verify( nextHandler, Mockito.times( 0 ) ).handleMessage( session, commMessage );

		// 验证session的值被重新设置
		verify( session ).setId( this.id );
		verify( session ).setTUId( this.tuid );
		verify( session ).setExpiredTime( this.expireTime );
	}
}
