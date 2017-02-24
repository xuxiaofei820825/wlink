package com.iauto.wlink.core.session;

import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.mockpolicies.Slf4jMockPolicy;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@MockPolicy(Slf4jMockPolicy.class)
public class DefaultSessionManagerTest {

	private final static String uid = "session_tuid";
	private final static String id = "session_id";

	private DefaultSessionManager sessionManager;
	private Session session;
	private SessionListener sessionListener1;
	private SessionListener sessionListener2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		// 创建测试对象
		sessionManager = new DefaultSessionManager();

		// 模拟一个Session实例
		session = Mockito.mock( Session.class );
		Mockito.when( session.getUid() ).thenReturn( uid );
		Mockito.when( session.getId() ).thenReturn( id );

		// 模拟SessionListener实例
		sessionListener1 = Mockito.mock( SessionListener.class );
		sessionListener2 = Mockito.mock( SessionListener.class );

		// 添加SessionListener
		sessionManager.addListener( sessionListener1 );
		sessionManager.addListener( sessionListener2 );
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddSession() {

		// 添加Session
		sessionManager.add( session );

		// 验证是否可以取出已添加的Session
		Assert.assertEquals( session, sessionManager.get( uid, id ) );
		// 验证已注册的SessionListener获得添加Session的通知
		verify( sessionListener1, Mockito.times( 1 ) ).onCreated( session, 1, 1 );
		verify( sessionListener2, Mockito.times( 1 ) ).onCreated( session, 1, 1 );
	}

	@Test
	public void testRemoveSession() {
		// 添加Session
		sessionManager.add( session );
		// 再删除掉
		Session removedSession = sessionManager.remove( uid, id );

		// 验证删除的是否是已添加的
		Assert.assertEquals( session, removedSession );
		// 验证已注册的SessionListener获得删除Session的通知
		verify( sessionListener1, Mockito.times( 1 ) ).onRemoved( session, 1, 0 );
		verify( sessionListener2, Mockito.times( 1 ) ).onRemoved( session, 1, 0 );
	}
}
